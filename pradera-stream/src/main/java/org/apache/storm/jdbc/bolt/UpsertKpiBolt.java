package org.apache.storm.jdbc.bolt;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.Config;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.KpiJdbcLookupMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcMapper;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.util.KpiTuple;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.CustomColumn;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.singleton.ConnectionManager;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.util.DateUtil;
import com.pradera.stream.util.jdbc.JdbcClientExt;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class UpsertKpiBolt extends JdbcInsertBolt {

	/**
	 * 
	 */

	private static final long serialVersionUID = -8783495568069984732L;
	private static final Logger LOG = LoggerFactory.getLogger(UpsertKpiBolt.class);
	private Map<String, Object> upsertsKpiMap =  Maps.newHashMap();


	public UpsertKpiBolt(HikariCPConnectionProvider hikariCPConnectionProvider ,JdbcLookupMapper jdbcMapper, Map mapUpser) {

		super(hikariCPConnectionProvider, jdbcMapper);
		Validate.notNull(mapUpser);
	}

	@Override
	public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector collector) {

		this.collector = collector;
		LOG.info("Setting dinamycs  operations  on UpsertsKpiBolt");

		if (queryTimeoutSecs == null) {
			queryTimeoutSecs = Integer.parseInt(stormConf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS).toString());
		}

		String user = (stormConf.get(Constant.SettingMongo.USER) == null
				|| StringUtils.isEmpty(stormConf.get(Constant.SettingMongo.USER).toString())) ? null
						: stormConf.get(Constant.SettingMongo.USER).toString();

		String password = (stormConf.get(Constant.SettingMongo.PASSWORD) == null
				|| StringUtils.isEmpty(stormConf.get(Constant.SettingMongo.PASSWORD).toString())) ? null
						: stormConf.get(Constant.SettingMongo.PASSWORD).toString();

		String host = stormConf.get(Constant.SettingMongo.HOST).toString();
		int port = Integer.parseInt(stormConf.get(Constant.SettingMongo.PORT).toString());
		String dbName = stormConf.get(Constant.SettingMongo.DB).toString();
		MongoDBClient mongoDBClient = new MongoDBClient(user, password, dbName, host, port);

		String _setTopologyName = (String) stormConf.get("name");
		Bson 		filter 			= 	Filters.eq("name", _setTopologyName);
		Document 	topology		=	mongoDBClient.find(filter, "topology");

		List<DBRef> processes		=	(List)topology.get("bolts");	

		filter 		= 	Filters.and( Filters.or(
				Filters.eq("_id", processes.get(0).getId())
				,Filters.eq("_id", processes.get(1).getId())
				,Filters.eq("_id", processes.get(2).getId())
				,Filters.eq("_id", processes.get(3).getId())
				,Filters.eq("_id", processes.get(4).getId())
				,Filters.eq("_id", processes.get(5).getId())),
				Filters.eq("name",  Constant.StormComponent.INSERT_NATIVE_BOLT));

		Document 		process		=	mongoDBClient.find(filter, "bolt");
		List<Object> 	streamsList	=	(List)process.get("streams");

		Map	streamMap = null;
		Map	parametersMap = null;
		for(int i=0; i< streamsList.size(); i++) {
			streamMap =  (Map)streamsList.get(i);
			parametersMap =  Maps.newHashMap();
			parametersMap.put(Constant.Fields.SCRIPT, (String)streamMap.get(Constant.Fields.SCRIPT));
			loadingConfigurationJdbcMapper(streamMap, parametersMap);
			loadingConfigurationJdbcClient(streamMap, parametersMap, stormConf ,mongoDBClient);
			upsertsKpiMap.put((String)streamMap.get("streamName"), parametersMap);
		}

		mongoDBClient.close();
	}

	@Override
	protected void process(Tuple tuple) {

		LOG.info(" Processing message on bol UpsertKpiBolt Native ");

		try {

			String _streamId = (String) tuple.getValueByField("streamId");

			/**
			 * Getting Payload from Tuple
			 */
			OperationPayload operationPayload = (OperationPayload) tuple.getValueByField("PAYLOAD");

			Map upsertKpiMap	  =	null;
			try {
				upsertKpiMap	  =	(Map) upsertsKpiMap.get(_streamId);
				Validate.notNull(upsertKpiMap, "The upsertKpiMap  must not be null");
			} catch (Exception e) {
				if ( e instanceof NullPointerException) {
					LOG.error(" NO EXISTE EL VALOR MAPEADO DEL streamId  : "+ _streamId + "  EN EL COMPONENTE : UpsertKpiBolt");
					throw e;
				}
			}

			String upsertQuery = (String) upsertKpiMap.get(Constant.Fields.SCRIPT);

			KpiJdbcLookupMapper kpiJdbcLookupMapper = (KpiJdbcLookupMapper) upsertKpiMap.get("jdbcLookupMapper");	

			Map<String, Object> payloadMap = operationPayload.getPayload();
			KpiTuple tupleNative = (KpiTuple) payloadMap.get("TUPLE");
			JdbcClient jdbcClient =  (JdbcClient) upsertKpiMap.get("jdbcClient");
			LOG.debug(" UpsertKpiBolt ::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
			LOG.debug(" UpsertKpiBolt " + tupleNative.toString());


			/**
			 * Working with Columns which will be part of final query.
			 */
			/**
			 * Adding columns generics.Don't move thats columns which are used in query for
			 * logic and options of work with last record.
			 */

			List<Column> columns = kpiJdbcLookupMapper.getColumnsKpi(tupleNative);

			columns.add(new CustomColumn("REGISTER_DATE", DateUtil.getSystemTimestamp(), Types.TIMESTAMP));
			columns.add(new CustomColumn("STATE", 1, Types.INTEGER));
			List<List<Column>> columnLists = new ArrayList<List<Column>>();
			columnLists.add(columns);

			jdbcClient.executeInsertQuery(upsertQuery, columnLists);

			this.collector.ack(tuple);

			////////////////////////////////////////////////////////////////////////////////////////////
			/**
			 * Only send tuple to WriterBolt when the next conditional is True.Just due to
			 * improve the performance on WebSocket Component.
			 */
			////////////////////////////////////////////////////////////////////////////////////////////
			collector.emit("REST_STREAM", tuple, new Values(_streamId,operationPayload));

		} catch (Exception e) {
			this.collector.reportError(e);
			this.collector.fail(tuple);
		}
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		outputFieldsDeclarer.declareStream("REST_STREAM", new Fields("streamId","PAYLOAD"));
	}

	private void loadingConfigurationJdbcClient(Map streamMap , Map	parametersMap,Map stormConf,MongoDBClient mongoDBClient) {

		Map map = getPropertiesConnection( mongoDBClient);
		if ( ConnectionManager.lostReference((String) streamMap.get(Constant.Fields.DATASOURCE_NAME))) {
			ConnectionManager.loadDataSources((List<Map<String, Object>>) map.get(Constant.Fields.DATASOURCES));
		} 
		ConnectionProvider connectionProvider = ((HikariCPConnectionSingletonSource)ConnectionManager.
				getHikariCPConnectionProvider((String) streamMap.get(Constant.Fields.DATASOURCE_NAME))).getInstance();
		connectionProvider.prepare();
		if(queryTimeoutSecs == null) {
			queryTimeoutSecs = Integer.parseInt(stormConf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS).toString());
		}
		JdbcClient jdbcClient = new JdbcClientExt(connectionProvider, queryTimeoutSecs);
		parametersMap.put("jdbcClient", jdbcClient);
	}

	private void loadingConfigurationJdbcMapper(Map streamMap , Map	parametersMap) {
		List<Object> parameters = (List) streamMap.get("columnFieldsMap");
		Map _mapParameter = null;
		List<Column> columns = new ArrayList<Column>(parameters.size());

		for (int i = 0; i < parameters.size(); i++) {
			_mapParameter = (Map) parameters.get(i);
			columns.add(new Column((String) _mapParameter.get("name"), (Integer) _mapParameter.get("type")));
			LOG.debug(columns.toString());
		}

		Fields outputFieldsMock = new Fields("MOCK");
		SimpleJdbcMapper mapper = new KpiJdbcLookupMapper(outputFieldsMock, columns);
		parametersMap.put("jdbcLookupMapper", mapper);
	}

	protected Map getPropertiesConnection(MongoDBClient mongoDBClient) {

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
