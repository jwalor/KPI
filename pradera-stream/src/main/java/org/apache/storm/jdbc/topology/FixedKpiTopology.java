package org.apache.storm.jdbc.topology;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.storm.flux.api.TopologySource;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.jdbc.bolt.CustomUpsertKpiBolt;
import org.apache.storm.jdbc.bolt.JdbcInsertBolt;
import org.apache.storm.jdbc.bolt.JdbcLookupBolt;
import org.apache.storm.jdbc.bolt.KpiComparatorBolt;
import org.apache.storm.jdbc.bolt.LogicKpiBolt;
import org.apache.storm.jdbc.bolt.UpsertKpiBolt;
import org.apache.storm.jdbc.bolt.WriterApiBolt;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.KpiJdbcLookupMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.jdbc.spout.KpiSpout;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import com.pradera.stream.constant.Constant;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.singleton.HikariCPConnectionSingletonTarget;


/**
 * 
 * @author jalor
 *
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class FixedKpiTopology  implements TopologySource {

	private static final String USER_SPOUT = "USER_SPOUT";
	private static final String LOOKUP_BOLT = "LOOKUP_BOLT";
	private static final String PERSISTANCE_BOLT = "PERSISTANCE_BOLT";
	private static final String LOGIC_BOLT = "LOGIC_BOLT";
	public static final String INSERT_STREAM = "INSERT_STREAM";
	public static final String NATIVE_STREAM = "NATIVE_STREAM";
	public static final String UPDATE_STREAM = "UPDATE_STREAM";
	public static final String LOGIC_STREAM = "LOGIC_STREAM";

	private static final Logger LOG = LoggerFactory.getLogger(FixedKpiTopology.class);
	private ConnectionProvider connectionProviderTarget;
	private ConnectionProvider connectionProviderSource;
	private static final Integer NUM_TASKS=1;


	@Override
	public StormTopology getTopology(Map<String, Object> setting) {
		
		String _setTopologyName = (String) setting.get("name");
		
		String user 	= setting.get(Constant.SettingMongo.USER).toString();
		String password = setting.get(Constant.SettingMongo.PASSWORD).toString();
		
		String host 	= setting.get(Constant.SettingMongo.HOST).toString();
		int    port 	= Integer.parseInt(setting.get(Constant.SettingMongo.PORT).toString());
		String dbName 		= setting.get(Constant.SettingMongo.DB).toString();
		MongoDBClient mongoDBClient = new MongoDBClient(user,password,dbName,host,port);
		
		Bson 		filter 		= 	Filters.eq("name", _setTopologyName);
		Document 	topology	=	mongoDBClient.find(filter, "topology");

		List<DBRef> communications		=	(List)topology.get(Constant.Fields.COMMUNICATIONS);		
		Assertions.assertThat(communications.size()).isEqualTo(2);


		DBRef	dbf1 = communications.get(0);
		DBRef	dbf2 = communications.get(1);

		filter 		= 	Filters.and(Filters.or(Filters.eq("_id", dbf1.getId()),Filters.eq("_id", dbf2.getId())),Filters.eq("origin", Constant.Fields.SOURCE));
		Document 	communicationSource	=	mongoDBClient.find(filter, "communication");
		Assertions.assertThat(communicationSource).isNotNull();
		filter 		= 	Filters.and(Filters.or(Filters.eq("_id", dbf1.getId()),Filters.eq("_id", dbf2.getId())),Filters.eq("origin", Constant.Fields.TARGET));
		Document 	communicationTarget	=	mongoDBClient.find(filter, "communication");
		Assertions.assertThat(communicationTarget).isNotNull();

		/**
		 *  Loading properties to be used in HikariFactory or HikariProvider
		 */
		Map<String, Object> mapSource = Maps.newHashMap();
		mapSource.putAll((Map) communicationSource.get(Constant.Fields.SETTING));

		Map<String, Object> mapTarget = Maps.newHashMap();
		mapTarget.putAll((Map) communicationTarget.get(Constant.Fields.SETTING));

		mapSource.put("dataSource."+Constant.Fields.URL, mapSource.get(Constant.Fields.URL));
		mapSource.remove(Constant.Fields.URL);
		mapSource.put("dataSource."+Constant.Fields.USER, mapSource.get(Constant.Fields.USER));
		mapSource.remove(Constant.Fields.USER);
		mapSource.put("dataSource."+Constant.Fields.PASSWORD, mapSource.get(Constant.Fields.PASSWORD));
		mapSource.remove(Constant.Fields.PASSWORD);
		mapSource.put("registerMbeans", Boolean.TRUE);

		mapTarget.put("dataSource."+Constant.Fields.URL, mapTarget.get(Constant.Fields.URL));
		mapTarget.remove(Constant.Fields.URL);
		mapTarget.put("dataSource."+Constant.Fields.USER, mapTarget.get(Constant.Fields.USER));
		mapTarget.remove(Constant.Fields.USER);
		mapTarget.put("dataSource."+Constant.Fields.PASSWORD, mapTarget.get(Constant.Fields.PASSWORD));
		mapTarget.remove(Constant.Fields.PASSWORD);
		mapTarget.put("registerMbeans", Boolean.TRUE);
		mapTarget.put("maximumPoolSize", 20);
		
		
		HikariCPConnectionSingletonTarget.setHikariCPConfigMap(mapTarget);
		connectionProviderTarget = HikariCPConnectionSingletonTarget.getInstance();
		connectionProviderTarget.cleanup();
		
		Boolean _initializeScripts = (Boolean) topology.get(com.pradera.stream.constant.Constant.Fields.INITIALIZE_SCRIPT);

		if ( _initializeScripts) {

			int queryTimeoutSecs = 60;
			JdbcClient jdbcClient = new JdbcClient(connectionProviderTarget, queryTimeoutSecs);

			connectionProviderTarget.prepare();
			Map<String, Object> setupSqls = Maps.newHashMap();
			setupSqls = (Map<String, Object>) topology.get("preExecutions");

			Object[] setupSqlsObj	=	setupSqls.values().toArray();

			        	 for (Object sql : setupSqlsObj) {
			 	            try {
			 	            	jdbcClient.executeSql((String) sql);
			 				} catch (Exception e) {
			 					LOG.error(" ERROR MANAGMENT  :::: " + e.getCause());
			 					return null;
			 				}
			           }

			//////////////////////////////////////////////////////////////////
			// Updating state _iniatialize to False. Only the first time that to run Topology.
			        	 
//			BasicDBObject newDocument = new BasicDBObject();
//			newDocument.append("$set", new BasicDBObject().append(com.pradera.stream.constant.Constant.Fields.INITIALIZE_SCRIPT, false));
//			topology.put(com.pradera.stream.constant.Constant.Fields.INITIALIZE_SCRIPT,false);

			Bson 		filter1 		= 	Filters.eq("name", _setTopologyName);
		 	topology	=	mongoDBClient.find(filter1, "topology");
		 	topology.put(com.pradera.stream.constant.Constant.Fields.INITIALIZE_SCRIPT,false);
			mongoDBClient.update(filter1, topology, true, false);
			mongoDBClient.close();
			
			connectionProviderTarget.cleanup();
		}

		/**
		 *  Valores por defauls , luego de la construcción de la topología serán reemplazados
		 */
		Fields outputFields = new Fields("Constants.PAYLOAD");
		List<Column> columnsMock = Lists.newArrayList(new Column("MOCK", Types.NUMERIC));

		SimpleJdbcMapper jdbcLookupMapperMock = new  KpiJdbcLookupMapper(outputFields , columnsMock);
		String queryMock = " Select now() ";
		JdbcLookupBolt kpiComparatorBolt = new KpiComparatorBolt( queryMock, (KpiJdbcLookupMapper) jdbcLookupMapperMock);
		((KpiComparatorBolt)kpiComparatorBolt).setName(Constant.StormComponent.COMPARATOR_BOLT);


		/**
		 * 
		 */

		Fields outputFieldsMock = new Fields("MOCK"); 
		List<Column> schemaColumns = new ArrayList<Column>();
		schemaColumns.add(new Column("MOCK", Types.NUMERIC));
		SimpleJdbcMapper mapper = new KpiJdbcLookupMapper(outputFieldsMock,schemaColumns);
		Map<String,String> 	mapInsert = new HashMap<String,String>();
		JdbcInsertBolt kpiPersistentBolt = new UpsertKpiBolt((JdbcLookupMapper) mapper,mapInsert)
				.withInsertQuery("EMPTY");

		/**
		 *  :::::::::::::::::::::::::::::::::::: Building Topology ::::::::::::::::::::::::::::::::::::::::::
		 */

		CustomUpsertKpiBolt customUpdateKpiBolt = new CustomUpsertKpiBolt(Constant.StormComponent.UPDATE_CUSTOM_BOLT);
		CustomUpsertKpiBolt customInsertKpiBolt = new CustomUpsertKpiBolt(Constant.StormComponent.INSERT_CUSTOM_BOLT);
		WriterApiBolt	restApiBolt = new WriterApiBolt();
		KpiSpout		kpiSpout = new KpiSpout(setting);
		
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout(USER_SPOUT, kpiSpout, 6);
		builder.setBolt(LOOKUP_BOLT, kpiComparatorBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(USER_SPOUT);
		builder.setBolt(PERSISTANCE_BOLT, kpiPersistentBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOOKUP_BOLT,NATIVE_STREAM);
		builder.setBolt(LOGIC_BOLT, new LogicKpiBolt(), NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOOKUP_BOLT,LOGIC_STREAM);
		builder.setBolt("UPDATE_BOLT", customUpdateKpiBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOGIC_BOLT,UPDATE_STREAM);
		builder.setBolt("INSERT_BOLT", customInsertKpiBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOGIC_BOLT,INSERT_STREAM);
		builder.setBolt("RES_API_BOLT1", restApiBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(PERSISTANCE_BOLT,"REST_STREAM");
		builder.setBolt("RES_API_BOLT2", restApiBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping("INSERT_BOLT","REST_STREAM");
		
		return builder.createTopology();
	}


}
