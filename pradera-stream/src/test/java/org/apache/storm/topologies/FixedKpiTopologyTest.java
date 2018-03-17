package org.apache.storm.topologies;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

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
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.KpiJdbcLookupMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.jdbc.spout.KpiSpout;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import org.bson.Document;
import org.bson.conversions.Bson;
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


/**
 * 
 * @author jalor
 *
 */

@SuppressWarnings({"rawtypes", "unchecked"})
public class FixedKpiTopologyTest  implements TopologySource {

	private static final String USER_SPOUT = "USER_SPOUT";
	private static final String LOOKUP_BOLT = "LOOKUP_BOLT";
	private static final String PERSISTANCE_BOLT = "PERSISTANCE_BOLT";
	private static final String LOGIC_BOLT = "LOGIC_BOLT";
	public static final String INSERT_STREAM = "INSERT_STREAM";
	public static final String NATIVE_STREAM = "NATIVE_STREAM";
	public static final String UPDATE_STREAM = "UPDATE_STREAM";
	public static final String LOGIC_STREAM = "LOGIC_STREAM";
	
    private static final Integer NUM_WORKERS=1;
	private static final Integer NUM_TASKS=1;

    
	private static final Logger LOG = LoggerFactory.getLogger(FixedKpiTopologyTest.class);


	@Override
	public StormTopology getTopology(Map<String, Object> setting) {
		
		String _setTopologyName = (String) setting.get("name");
		
		String host 	= setting.get(Constant.SettingMongo.HOST).toString();
		int    port 	= Integer.parseInt(setting.get(Constant.SettingMongo.PORT).toString());
		String dbName 		= setting.get(Constant.SettingMongo.DB).toString();
		
		MongoDBClient mongoDBClient = new MongoDBClient(null,null,dbName,host,port);
		Bson 		filter 		= 	Filters.eq("name", _setTopologyName);
		Document 	topology	=	mongoDBClient.find(filter, "topology");
		
		// SI NO EXISTE LA TOPOLOGIA DEBERIA TERNIMAR TODO ACÁ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//		SI NO EXISTE LA TOPOLOGIA DEBERIA TERNIMAR TODO ACÁ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//		SI NO EXISTE LA TOPOLOGIA DEBERIA TERNIMAR TODO ACÁ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//		SI NO EXISTE LA TOPOLOGIA DEBERIA TERNIMAR TODO ACÁ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//		
//		SI NO EXISTE LA TOPOLOGIA DEBERIA TERNIMAR TODO ACÁ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//		SI NO EXISTE LA TOPOLOGIA DEBERIA TERNIMAR TODO ACÁ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//		SI NO EXISTE LA TOPOLOGIA DEBERIA TERNIMAR TODO ACÁ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11
//		
//		SI NO EXISTE LA TOPOLOGIA DEBERIA TERNIMAR TODO ACÁ!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!11

//		List<DBRef> communications		=	(List)topology.get(Constant.Fields.COMMUNICATIONS);		
//		Assertions.assertThat(communications.size()).isEqualTo(2);
//
//
//		DBRef	dbf1 = communications.get(0);
//		DBRef	dbf2 = communications.get(1);
//
//		filter 		= 	Filters.and(Filters.or(Filters.eq("_id", dbf1.getId()),Filters.eq("_id", dbf2.getId())),Filters.eq("origin", Constant.Fields.SOURCE));
//		Document 	communicationSource	=	mongoDBClient.find(filter, "communication");
//		Assertions.assertThat(communicationSource).isNotNull();
//		filter 		= 	Filters.and(Filters.or(Filters.eq("_id", dbf1.getId()),Filters.eq("_id", dbf2.getId())),Filters.eq("origin", Constant.Fields.TARGET));
//		Document 	communicationTarget	=	mongoDBClient.find(filter, "communication");
//		Assertions.assertThat(communicationTarget).isNotNull();
//
//		/**
//		 *  Loading properties to be used in HikariFactory or HikariProvider
//		 */
//		Map<String, Object> mapSource = Maps.newHashMap();
//		mapSource.putAll((Map) communicationSource.get(Constant.Fields.SETTING));
//
//		Map<String, Object> mapTarget = Maps.newHashMap();
//		mapTarget.putAll((Map) communicationTarget.get(Constant.Fields.SETTING));
//
//		mapSource.put("dataSource."+Constant.Fields.URL, mapSource.get(Constant.Fields.URL));
//		mapSource.remove(Constant.Fields.URL);
//		mapSource.put("dataSource."+Constant.Fields.USER, mapSource.get(Constant.Fields.USER));
//		mapSource.remove(Constant.Fields.USER);
//		mapSource.put("dataSource."+Constant.Fields.PASSWORD, mapSource.get(Constant.Fields.PASSWORD));
//		mapSource.remove(Constant.Fields.PASSWORD);
//		mapSource.put("registerMbeans", Boolean.TRUE);
//
//		mapTarget.put("dataSource."+Constant.Fields.URL, mapTarget.get(Constant.Fields.URL));
//		mapTarget.remove(Constant.Fields.URL);
//		mapTarget.put("dataSource."+Constant.Fields.USER, mapTarget.get(Constant.Fields.USER));
//		mapTarget.remove(Constant.Fields.USER);
//		mapTarget.put("dataSource."+Constant.Fields.PASSWORD, mapTarget.get(Constant.Fields.PASSWORD));
//		mapTarget.remove(Constant.Fields.PASSWORD);
//		mapTarget.put("registerMbeans", Boolean.TRUE);
//		mapTarget.put("maximumPoolSize", 40);
//		
//		HikariCPConnectionSingletonTarget.setHikariCPConfigMap(mapTarget);
//		connectionProviderTarget = HikariCPConnectionSingletonTarget.getInstance();
//		connectionProviderTarget.cleanup();

		
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
		
		HazelcastInstance hzInstance = Hazelcast.newHazelcastInstance();
		Queue<String> q = hzInstance.getQueue("queueSpouts");
		q.add("settlementStream");
		
		TopologyBuilder builder = new TopologyBuilder();

		builder.setSpout(USER_SPOUT, kpiSpout,1);
		builder.setBolt(LOOKUP_BOLT, kpiComparatorBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(USER_SPOUT);
		builder.setBolt("CONTROL1",  new ControlKpiBolt(), NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOOKUP_BOLT,"CONTROL_STREAM");
		
		builder.setBolt(PERSISTANCE_BOLT, kpiPersistentBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOOKUP_BOLT,NATIVE_STREAM);
		builder.setBolt(LOGIC_BOLT, new LogicKpiBolt(), NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOOKUP_BOLT,LOGIC_STREAM);
		
		builder.setBolt("CONTROL2",  new ControlKpiBolt(), NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOGIC_BOLT,"CONTROL_STREAM");
		builder.setBolt("UPDATE_BOLT", customUpdateKpiBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOGIC_BOLT,UPDATE_STREAM);
		builder.setBolt("INSERT_BOLT", customInsertKpiBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(LOGIC_BOLT,INSERT_STREAM);
		builder.setBolt("RES_API_BOLT1", restApiBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping(PERSISTANCE_BOLT,"REST_STREAM");
		builder.setBolt("RES_API_BOLT2", restApiBolt, NUM_TASKS).setNumTasks(NUM_TASKS).shuffleGrouping("INSERT_BOLT","REST_STREAM");
		
		return builder.createTopology();
	}


}
