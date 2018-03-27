package org.apache.storm.jdbc.topology;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import org.apache.commons.lang3.Validate;
import org.apache.storm.Config;
import org.apache.storm.flux.api.TopologySource;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.jdbc.bolt.ControlKpiBolt;
import org.apache.storm.jdbc.bolt.CustomUpsertKpiBolt;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.bolt.JdbcLookupBolt;
import org.apache.storm.jdbc.bolt.KpiComparatorBolt;
import org.apache.storm.jdbc.bolt.LogicKpiBolt;
import org.apache.storm.jdbc.bolt.UpsertKpiBolt;
import org.apache.storm.jdbc.bolt.WriterApiBolt;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.KpiJdbcLookupMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.jdbc.spout.KpiSpout;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.singleton.ConnectionManager;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.util.jdbc.JdbcClientExt;


/**
 * 
 * @author jalor
 *
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class FixedKpiTopology  implements TopologySource {

	private static final Logger LOG = LoggerFactory.getLogger(FixedKpiTopology.class);

	private static final String USER_SPOUT = "USER_SPOUT";
	private static final String LOOKUP_BOLT = "LOOKUP_BOLT";
	private static final String PERSISTANCE_BOLT = "PERSISTANCE_BOLT";
	private static final String LOGIC_BOLT = "LOGIC_BOLT";
	public static final String INSERT_STREAM = "INSERT_STREAM";
	public static final String NATIVE_STREAM = "NATIVE_STREAM";
	public static final String UPDATE_STREAM = "UPDATE_STREAM";
	public static final String LOGIC_STREAM = "LOGIC_STREAM";
	public Map<String, Object> stormConf;
	public String _dataSourceName;
	public String _dataSourceTName;

	@Override
	public StormTopology getTopology(Map<String, Object> setting) {

		this.stormConf = setting;
		String _setTopologyName = (String) setting.get("name");
		
		String user 	= setting.get(Constant.SettingMongo.USER).toString();
		String password = setting.get(Constant.SettingMongo.PASSWORD).toString();
		String host 	= setting.get(Constant.SettingMongo.HOST).toString();
		int    port 	= Integer.parseInt(setting.get(Constant.SettingMongo.PORT).toString());
		String dbName 		= setting.get(Constant.SettingMongo.DB).toString();

		MongoDBClient mongoDBClient = new MongoDBClient(user,password,dbName,host,port);
		Bson 		filter 		= 	Filters.eq("name", _setTopologyName);
		Document 	topology	=	mongoDBClient.find(filter, "topology");
		Validate.notNull(topology, "The topology  "+ _setTopologyName + " must not be null");

		_dataSourceName  = (String) topology.get("dataSourceName");
		_dataSourceTName = (String) topology.get("dataSourceTargetName");

		Map numTaskMap			=	(Map)topology.get("numTask");//

		List<Object> 	streamsList	=	(List)topology.get("streamIds");
		Validate.notNull(streamsList, "The streamsList  must not be null");


		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		List<DBRef> spoutList = (List) topology.get(Constant.Fields.SPOUTS);
		List<Bson> list = new ArrayList<Bson>();
		for (int i = 0; i < spoutList.size(); i++) {
			list.add(Filters.eq("_id", spoutList.get(i).getId()));
		}
		filter = Filters.and(Filters.or(list));
		List<Document> spouts = mongoDBClient.findDocuments(filter, "spout");
		Assertions.assertThat(spouts.size()).isGreaterThan(0);

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////
		Map map = getPropertiesConnection( mongoDBClient);
		ConnectionManager.loadDataSources((List<Map<String, Object>>) map.get(Constant.Fields.DATASOURCES));

		Document spout = null;
		for (int i = 0; i < spouts.size(); i++) {
			spout	=	spouts.get(i);
			try {
				executePreExecutions(spout , mongoDBClient);
			} catch (Exception e) {
				LOG.error(" ERROR MANAGMENT  :::: " + e.getCause());
				throw e;
			}

		}
		mongoDBClient.close();
		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
		Queue<String> q = hzInstance.getQueue("queueSpouts");

		for(int i=0; i< streamsList.size(); i++) {
			q.add(streamsList.get(i).toString());
		}

		//////////////////////////////////////////////////////////////////////////////////////////////////////////////

		/**
		 *  Valores por defauls , luego de la construcción de la topología serán reemplazados
		 */
		Fields outputFields = new Fields("Constants.PAYLOAD");
		List<Column> columnsMock = Lists.newArrayList(new Column("MOCK", Types.NUMERIC));
		SimpleJdbcMapper jdbcLookupMapperMock = new  KpiJdbcLookupMapper(outputFields , columnsMock);
		String queryMock = " Select now() ";

		HikariCPConnectionProvider hikariCPConnectionProviderMock = new HikariCPConnectionProvider(Maps.newHashMap());
		JdbcLookupBolt kpiComparatorBolt = new KpiComparatorBolt( hikariCPConnectionProviderMock,queryMock, (KpiJdbcLookupMapper) jdbcLookupMapperMock);
		((KpiComparatorBolt)kpiComparatorBolt).setName(Constant.StormComponent.COMPARATOR_BOLT);

		Fields outputFieldsMock = new Fields("MOCK"); 
		List<Column> schemaColumns = new ArrayList<Column>();
		schemaColumns.add(new Column("MOCK", Types.NUMERIC));
		SimpleJdbcMapper mapper = new KpiJdbcLookupMapper(outputFieldsMock,schemaColumns);
		Map<String,String> 	mapInsert = new HashMap<String,String>();
		JdbcInsertBolt kpiPersistentBolt = new UpsertKpiBolt( hikariCPConnectionProviderMock, (JdbcLookupMapper) mapper,mapInsert)
				.withInsertQuery("EMPTY");

		/**
		 *  :::::::::::::::::::::::::::::::::::: Building Topology ::::::::::::::::::::::::::::::::::::::::::
		 */

		CustomUpsertKpiBolt customUpdateKpiBolt = new CustomUpsertKpiBolt(Constant.StormComponent.UPDATE_CUSTOM_BOLT);
		CustomUpsertKpiBolt customInsertKpiBolt = new CustomUpsertKpiBolt(Constant.StormComponent.INSERT_CUSTOM_BOLT);
		WriterApiBolt	restApiBolt = new WriterApiBolt();
		KpiSpout		kpiSpout = new KpiSpout(setting);


		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout(USER_SPOUT, kpiSpout, Integer.parseInt(numTaskMap.get("USER_SPOUT").toString()));
		builder.setBolt(LOOKUP_BOLT, kpiComparatorBolt).setNumTasks(Integer.parseInt(numTaskMap.get("LOOKUP_BOLT").toString())).shuffleGrouping(USER_SPOUT);
		builder.setBolt("CONTROL1",  new ControlKpiBolt(_dataSourceTName)).setNumTasks(Integer.parseInt(numTaskMap.get("CONTROL1").toString())).shuffleGrouping(LOOKUP_BOLT,"CONTROL_STREAM");

		builder.setBolt(PERSISTANCE_BOLT, kpiPersistentBolt).setNumTasks(Integer.parseInt(numTaskMap.get("PERSISTANCE_BOLT").toString())).shuffleGrouping(LOOKUP_BOLT,NATIVE_STREAM);
		builder.setBolt(LOGIC_BOLT, new LogicKpiBolt()).setNumTasks(Integer.parseInt(numTaskMap.get("LOGIC_BOLT").toString())).shuffleGrouping(LOOKUP_BOLT,LOGIC_STREAM);

		builder.setBolt("CONTROL2",  new ControlKpiBolt(_dataSourceTName)).setNumTasks(Integer.parseInt(numTaskMap.get("CONTROL2").toString())).shuffleGrouping(LOGIC_BOLT,"CONTROL_STREAM");
		builder.setBolt("UPDATE_BOLT", customUpdateKpiBolt).setNumTasks(Integer.parseInt(numTaskMap.get("UPDATE_BOLT").toString())).shuffleGrouping(LOGIC_BOLT,UPDATE_STREAM);
		builder.setBolt("INSERT_BOLT", customInsertKpiBolt).setNumTasks(Integer.parseInt(numTaskMap.get("INSERT_BOLT").toString())).shuffleGrouping(LOGIC_BOLT,INSERT_STREAM);
		builder.setBolt("RES_API_BOLT1", restApiBolt).setNumTasks(Integer.parseInt(numTaskMap.get("RES_API_BOLT1").toString())).shuffleGrouping(PERSISTANCE_BOLT,"REST_STREAM");
		builder.setBolt("RES_API_BOLT2", restApiBolt).setNumTasks(Integer.parseInt(numTaskMap.get("RES_API_BOLT2").toString())).shuffleGrouping("INSERT_BOLT","REST_STREAM");

		return builder.createTopology();
	}

	protected void executePreExecutions(Document spout , MongoDBClient mongoDBClient) {

		Boolean _initializeScripts = (Boolean) spout.get(Constant.Fields.INITIALIZE_SCRIPT);

		if (_initializeScripts) {

			Map<String, Object> setupSqls = (Map<String, Object>) spout.get("preExecutions");

			Object[] setupSqlsObj = setupSqls.values().toArray();

			ConnectionProvider connectionProvider  = ((HikariCPConnectionSingletonSource)ConnectionManager.
					getHikariCPConnectionProvider(spout.get(Constant.Fields.DATASOURCE_T_NAME).toString())).getInstance();
			connectionProvider.prepare();

			JdbcClient jdbcClientTmp =  new JdbcClientExt(connectionProvider, Integer.parseInt(stormConf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS).toString()));

			for (Object sql : setupSqlsObj) {
				jdbcClientTmp.executeSql((String) sql);
			}

			//////////////////////////////////////////////////////////////////
			// Updating state _iniatialize to False. Only the first time that to run
			////////////////////////////////////////////////////////////////// Topology.

			Bson filter = Filters.eq("streamId", (String) spout.get("streamId"));
			Document spoutUpdate = mongoDBClient.find(filter, "spout");
			spoutUpdate.put(Constant.Fields.INITIALIZE_SCRIPT, false);
			mongoDBClient.update(filter, spoutUpdate, true, false);

			jdbcClientTmp =  null;
		}

	}

	protected Map getPropertiesConnection( MongoDBClient mongoDBClient) {

		String _connectionType = "sql";
		Bson filter = Filters.eq("connectionType", _connectionType);
		List<Document> communications = mongoDBClient.findDocuments(filter, "communication");
		Assertions.assertThat(communications.size()).isGreaterThan(0);

		List<Map<String, Object>> 	dataSourcesListMap 				= 	new ArrayList<Map<String, Object>>();
		Document communication = null;
		for (int i = 0; i < communications.size(); i++) {
			communication	=	communications.get(i);
			Map<String, Object> map	=	Maps.newHashMap();
			map.putAll((Map) communication.get(Constant.Fields.SETTING));
			map.put("dataSource."+Constant.Fields.URL, map.get(Constant.Fields.URL));
			map.remove(Constant.Fields.URL);
			map.put("dataSource."+Constant.Fields.USER, map.get(Constant.Fields.USER));
			map.remove(Constant.Fields.USER);
			map.put("dataSource."+Constant.Fields.PASSWORD, map.get(Constant.Fields.PASSWORD));
			map.remove(Constant.Fields.PASSWORD);

			if ( communication.get(Constant.Fields.MAXIMUM_POOL_SIZE)!=null && !communication.get(Constant.Fields.MAXIMUM_POOL_SIZE).toString().isEmpty() ) {
				map.put(Constant.Fields.MAXIMUM_POOL_SIZE, communication.get(Constant.Fields.MAXIMUM_POOL_SIZE));
			}
			if ( communication.get(Constant.Fields.MINIMUMIDLE)!=null && !communication.get(Constant.Fields.MINIMUMIDLE).toString().isEmpty() ) {
				map.put(Constant.Fields.MINIMUMIDLE, communication.get(Constant.Fields.MINIMUMIDLE));
			}
			map.put("registerMbeans", Boolean.TRUE);
			map.put("poolName", communication.get(Constant.Fields.DATASOURCE_NAME));
			map.remove(Constant.Fields.DATASOURCE_NAME);
			dataSourcesListMap.add(map);
		}

		Map<String, Object> map = Maps.newHashMap();
		map.put(Constant.Fields.DATASOURCES, dataSourcesListMap);
		return map;
	}
}
