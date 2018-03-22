package org.apache.storm.jdbc.bolt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.storm.Config;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseTickTupleAwareRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.singleton.ConnectionManager;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.util.VelocityUtils;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"rawtypes" , "unchecked" , "unlikely-arg-type"})
public class CustomUpsertKpiBolt extends BaseTickTupleAwareRichBolt{

	/**
	 * 
	 */
	private static final long   serialVersionUID = -8783495568069984732L;
	private static final Logger LOG = LoggerFactory.getLogger(CustomUpsertKpiBolt.class);
	private String			 	_name;
	protected OutputCollector   collector;
	private Map<String, Object> customUpsertsMap =  Maps.newHashMap();

	public CustomUpsertKpiBolt(String name) {
		this._name = name;
	}

	@Override
	public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector collector ) {

		LOG.info("Setting dinamycs  operations  on CustomUpsertKpiBolt");
		String user 	=  (stormConf.get(Constant.SettingMongo.USER) == null || 
				StringUtils.isEmpty(stormConf.get(Constant.SettingMongo.USER).toString()) )	?
						null:stormConf.get(Constant.SettingMongo.USER).toString();

		String password = (stormConf.get(Constant.SettingMongo.PASSWORD) == null || 
				StringUtils.isEmpty(stormConf.get(Constant.SettingMongo.PASSWORD).toString()) )	?
						null:stormConf.get(Constant.SettingMongo.PASSWORD).toString();

		String host 	= stormConf.get(Constant.SettingMongo.HOST).toString();
		int    port 	= Integer.parseInt(stormConf.get(Constant.SettingMongo.PORT).toString());
		String dbName 		= stormConf.get(Constant.SettingMongo.DB).toString();
		MongoDBClient mongoDBClient = new MongoDBClient(user,password,dbName,host,port);

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
				Filters.eq("name", this._name));

		Document 	process		=	mongoDBClient.find(filter, "bolt");
		List<Object> 	streamsList	=	(List)process.get("streams");

		Map	streamMap = null;
		Map	parametersMap = null;

		for(int i=0; i< streamsList.size(); i++) {
			streamMap =  (Map)streamsList.get(i);
			parametersMap =  Maps.newHashMap();
			loadingConfigurationConnectionProvider(streamMap, parametersMap, stormConf ,mongoDBClient);
			loadingConfigurationOperations(streamMap, parametersMap);

			customUpsertsMap.put((String)streamMap.get("streamName"), parametersMap);
		}

		mongoDBClient.close();
		this.collector = collector;
	}

	private void loadingConfigurationConnectionProvider(Map streamMap , Map	parametersMap,Map stormConf,MongoDBClient mongoDBClient) {

		Map map = getPropertiesConnection( mongoDBClient);

		if ( ConnectionManager.lostReference((String) streamMap.get(Constant.Fields.DATASOURCE_NAME))) {
			ConnectionManager.loadDataSources((List<Map<String, Object>>) map.get(Constant.Fields.DATASOURCES));
		}

		ConnectionProvider connectionProvider  = ((HikariCPConnectionSingletonSource)ConnectionManager.getHikariCPConnectionProvider((String) streamMap.get(Constant.Fields.DATASOURCE_NAME))).
				getInstance();
		connectionProvider.prepare();

		parametersMap.put("queryTimeoutSecs", Integer.parseInt(stormConf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS).toString()));
		parametersMap.put("connectionProvider",connectionProvider);
	}

	private void loadingConfigurationOperations(Map streamMap , Map	parametersMap) {

		Document d = (Document) streamMap.get("executors");
		Object[]  executorArray = (Object[]) d.values().toArray();

		if (!ArrayUtils.isEmpty(executorArray)){
			List<Map>	configOperations =   new  ArrayList<Map>();

			Map executor = null;
			for (int j = 0; j < executorArray.length; j++) {
				Document obj = (Document) executorArray[j];
				executor = new  HashMap<String , Object>();
				executor.put(Constant.Fields.SCRIPT_TYPE, obj.get(Constant.Fields.SCRIPT_TYPE));
				executor.put(Constant.Fields.SCRIPT, obj.get(Constant.Fields.SCRIPT));
				configOperations.add(executor);
			}			

			parametersMap.put("configOperations",configOperations );
		}
	}

	@Override
	protected void process(Tuple tuple) {

		Connection connection = null;
		String query	 = StringUtils.EMPTY;

		try {	

			String _streamId = (String) tuple.getValueByField("streamId");
			OperationPayload operationPayload = (OperationPayload) tuple.getValueByField("PAYLOAD");

			Map customUpsertMap	= null;
			try {
				customUpsertMap	  =	(Map) customUpsertsMap.get(_streamId);
				Validate.notNull(customUpsertMap, "The customUpsertMap  must not be null");
			} catch (Exception e) {
				if ( e instanceof NullPointerException) {
					LOG.error(" NO EXISTE EL VALOR MAPEADO DEL streamId : "+ _streamId + " EN EL COMPONENTE : CustomUpsertKpiBolt");
					throw e;
				}
			}

			List<Map>	configOperations =   (List<Map>) customUpsertMap.get("configOperations");
			ConnectionProvider connectionProvider   =  (ConnectionProvider) customUpsertMap.get("connectionProvider");
			Integer queryTimeoutSecs  =  (Integer) customUpsertMap.get("queryTimeoutSecs");

			LOG.info("----> Initial time  :" + System.currentTimeMillis() + " for component : CustomUpsertKpiBolt");

			for (Map configOperationMap : configOperations) {

				query =  VelocityUtils.loadTemplateVM(configOperationMap.get(Constant.Fields.SCRIPT) ,operationPayload.getPayload());
				connection = connectionProvider.getConnection();
				if ( !((HikariCPConnectionSingletonSource)connectionProvider).getSchema().isEmpty()) {
					connection.setSchema(((HikariCPConnectionSingletonSource)connectionProvider).getSchema());
				}
				boolean autoCommit = connection.getAutoCommit();
				if(autoCommit) {
					connection.setAutoCommit(false);
				}

				LOG.debug("Executing query {}", query);
				PreparedStatement preparedStatement = connection.prepareStatement(query);

				if(queryTimeoutSecs > 0) {
					preparedStatement.setQueryTimeout(queryTimeoutSecs);
				}
				preparedStatement.addBatch();
				int[] results = preparedStatement.executeBatch();
				if(Arrays.asList(results).contains(Statement.EXECUTE_FAILED)) {
					connection.rollback();
					throw new RuntimeException("failed at least one sql statement in the batch, operation rolled back.");
				} else {
					try {
						connection.commit();
					} catch (SQLException e) {
						throw new RuntimeException("Failed to commit  query " + query, e);
					}
				}

			}

			LOG.info("----> Final time  :" + System.currentTimeMillis()+ " for component : CustomUpsertKpiBolt");

			////////////////////////////////////////////////////////////////////////////////////////////
			/**
			 *  Sending tuple to WriterBolt.
			 */
			///////////////////////////////////////////////////////////////////////////////////////////
			String _streamName = tuple.getSourceStreamId();
			if (_streamName.equalsIgnoreCase("INSERT_STREAM")) {
				LOG.info("::::::::::::::::::::::::::: SENDING TUPLE TO WRITER BOLT !!!!!!!!!!!!!!!!!!!!!!!!!!!");
				collector.emit("REST_STREAM",tuple, new Values(_streamId,operationPayload));
			}

			this.collector.ack(tuple);
		} 
		catch (Exception e) {
			this.collector.reportError(e);
			this.collector.fail(tuple);
			LOG.info("Failed to execute  query " + query, e);
		}
		finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					throw new RuntimeException("Failed to close connection", e);
				}
			}
		}

	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("REST_STREAM", new Fields("streamId","PAYLOAD"));
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
			map.put("registerMbeans", Boolean.TRUE);
			map.put("poolName", communication.get(Constant.Fields.DATASOURCE_NAME));
			map.remove(Constant.Fields.DATASOURCE_NAME);
			if ( communication.get(Constant.Fields.MAXIMUM_POOL_SIZE)!=null && !communication.get(Constant.Fields.MAXIMUM_POOL_SIZE).toString().isEmpty() ) {
				map.put(Constant.Fields.MAXIMUM_POOL_SIZE, communication.get(Constant.Fields.MAXIMUM_POOL_SIZE));
			}
			if ( communication.get(Constant.Fields.MINIMUMIDLE)!=null && !communication.get(Constant.Fields.MINIMUMIDLE).toString().isEmpty() ) {
				map.put(Constant.Fields.MINIMUMIDLE, communication.get(Constant.Fields.MINIMUMIDLE));
			}
			dataSourcesListMap.add(map);
		}

		Map<String, Object> map = Maps.newHashMap();
		map.put(Constant.Fields.DATASOURCES, dataSourcesListMap);
		return map;
	}

}
