
package org.apache.storm.jdbc.spout;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.util.Strings;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.IRichSpout;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.hazelcast.client.HazelcastClient;
import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.config.GroupConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IQueue;
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.singleton.ConnectionManager;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.util.DateUtil;
import com.pradera.stream.util.jdbc.JdbcClientExt;
import com.pradera.stream.util.jdbc.NamedParameterStatement;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({ "unchecked", "rawtypes","unlikely-arg-type" })
public class KpiSpout implements IRichSpout, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final Logger LOG = LoggerFactory.getLogger(KpiSpout.class);

	private SpoutOutputCollector collector;
	protected transient JdbcClient jdbcClient;
	private String selectQuery;
	protected Integer queryTimeoutSecs;
	private Fields outputFields;
	private List<Column> parameters;
	private Map<String, Object> spoutConfig;
	public ConnectionProvider connectionProviderControl;
	private Long _sleepTime;
	private String _streamId = Strings.EMPTY;
	private Map<String, Object> preExecutions;
	public String _insertControl	= Strings.EMPTY;
	
	public KpiSpout(Map<String, Object> spoutConfig) {
		Validate.notNull(spoutConfig);
		this.spoutConfig = spoutConfig;
		this.preExecutions = Maps.newHashMap();
	}
	
	
	/**
	 * 
	 */
	public void open(Map stormConf, TopologyContext context, SpoutOutputCollector collector) {
		
		ClientConfig config = new ClientConfig();
		GroupConfig groupConfig = config.getGroupConfig();
		groupConfig.setName("dev");
		groupConfig.setPassword("dev-pass");
		
		HazelcastInstance hzClient = HazelcastClient.newHazelcastClient(config);
		IQueue<String> queue = hzClient.getQueue("queueSpouts");		 
	    _streamId = queue.poll();
	    
	    LOG.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
	    LOG.info(" Starting open method with KPISpout with id :" + _streamId);
	    LOG.info("::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::");
		
	    loadingConfigurationForKPI(_streamId);
		
		if ( ConnectionManager.lostReference("dbWari")) {
			ConnectionManager.loadDataSources( (List<Map<String, Object>>) stormConf.get(Constant.Fields.DATASOURCES) );
		}

		ConnectionProvider connectionProvider  = ((HikariCPConnectionSingletonSource)ConnectionManager.getHikariCPConnectionProvider("dbWari")).getInstance();
		connectionProvider.prepare();
		
		connectionProviderControl  = ((HikariCPConnectionSingletonSource)ConnectionManager.getHikariCPConnectionProvider("dbKpi")).getInstance();
		connectionProviderControl.prepare();
		

		StringBuilder _sb1 = new StringBuilder();
		_sb1.append("INSERT INTO STREAM_UPDATE(STREAM_UPDATE_NAME,START_HOUR,STATUS,REGISTER_DATE) ");
		_sb1.append(" VALUES( :STREAM_NAME , :START_HOUR, :STATUS,:REGISTER_DATE)");
		_insertControl = _sb1.toString();
		
		this.jdbcClient = new JdbcClientExt(connectionProvider, queryTimeoutSecs);
		this.collector = collector;
		
		// call method executing all operations.
		//executePreExecutions(this.preExecutions);
	}

	public void nextTuple() {

		LOG.debug("sending tuple: " + _streamId);
		Values values = null;
		Values valuesKpi = null;
		OperationPayload operationPayload = null;
		
		Boolean _finished = Boolean.TRUE;
		
		try {
			
		    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
			
			if (candReadTuples()) {		
			
					List<List<Column>> result = jdbcClient.select(this.selectQuery, this.parameters);
					
					if (result != null && result.size() != 0) {
						
							for (List<Column> row : result) {
								values = new Values();
								valuesKpi = new Values();
								valuesKpi.add(_streamId);
								operationPayload = new OperationPayload();
								for (String field : outputFields) {
									for (Column column : row) {
										if (column.getColumnName().equalsIgnoreCase(field)) {
											values.add(column.getVal());
											if (field.equalsIgnoreCase("TOTAL")) {
												operationPayload.setTotal(Long.valueOf(column.getVal().toString()));
											}else if (field.equalsIgnoreCase("CODE")) {
												operationPayload.setCode(Long.valueOf(column.getVal().toString()));
											}
										}
									}
								}
								operationPayload.set_streamId(_streamId);
								operationPayload.setValues(values);
								valuesKpi.add(operationPayload);
								
								///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
								verifyControl(_streamId, operationPayload);
							    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
								
								collector.emit(valuesKpi);

							}
					}
						
			}else {
				LOG.info("Process KPISpout with StreamID : " + _streamId + " haven't yet finished " );
				 _finished = Boolean.FALSE;
				Thread.sleep(_sleepTime);
			}
			
			if(_finished) {
				Thread.sleep(_sleepTime);
			}
		
		} catch (Exception e) {
			this.collector.reportError(e);
			// this.collector.fail(tuple);
		}
	}

	public void ack(Object msgId) {

	}

	public void fail(Object msgId) {

	}
	
	public void verifyControl(String _streamId ,OperationPayload operationPayload) {
		
		Connection 	connection 			= null;
		String 		query	 			= StringUtils.EMPTY;
		Boolean		connectionCreated	= Boolean.FALSE;
		
		try {	
				LOG.debug("----> Initial time  :" + System.currentTimeMillis()+ " for method : verifyControl");
				
				NamedParameterStatement namedParameterStatement	= null;
		            
				if ( operationPayload.getCode().equals(Long.valueOf(1)) ) { // if tuple is the first.
					
					query = _insertControl;
					connection = connectionProviderControl.getConnection();
					namedParameterStatement	=new NamedParameterStatement(connection, query);
					namedParameterStatement.setString("STREAM_NAME", _streamId);
			        namedParameterStatement.setTimestamp("START_HOUR", (Timestamp) DateUtil.getSystemTimestamp());
			        namedParameterStatement.setTimestamp("REGISTER_DATE", (Timestamp) DateUtil.getSystemTimestamp());
			        namedParameterStatement.setLong("STATUS", 0L);
			        connectionCreated	= Boolean.TRUE;
				}
				
				if ( connectionCreated ) {
					
					if(queryTimeoutSecs > 0) {
		            	namedParameterStatement.getStatement().setQueryTimeout(queryTimeoutSecs);
		            }
					
		            boolean autoCommit = connection.getAutoCommit();
		            if(autoCommit) {
		                connection.setAutoCommit(false);
		            }
		            
		            LOG.debug("Executing query {}", query);
	
		            namedParameterStatement.addBatch();
		            int[] results = namedParameterStatement.executeBatch();
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
					
			LOG.debug("----> Final time  :" + System.currentTimeMillis()+ "for method : verifyControl");
        } 
		catch (Exception e) {
            LOG.error("Failed to execute  query " + query, e);
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
	
	public Boolean candReadTuples() {
		
		Connection connection = null;
		Boolean _result = Boolean.FALSE;
		
		StringBuilder _sb1 = new StringBuilder();
		_sb1.append("       SELECT COUNT(1) as DONT_FINISH FROM STREAM_UPDATE ");
		_sb1.append("       WHERE STREAM_UPDATE_NAME = :STREAM_UPDATE_NAME AND STATUS = :STATUS  ");
		_sb1.append("       AND  to_char(REGISTER_DATE , 'YYYY-MM-DD') = :CURRENT_DATE ");//date_trunc('day', REGISTER_DATE + interval '0 day')
		String _queryControl = _sb1.toString();
		try {
			
			connection =  connectionProviderControl.getConnection();
            NamedParameterStatement namedParameterStatement	= new NamedParameterStatement(connection, _queryControl);
            namedParameterStatement.setString("STREAM_UPDATE_NAME", _streamId);
            namedParameterStatement.setLong("STATUS", 0);
            namedParameterStatement.setString("CURRENT_DATE", DateUtil.getDateFormatted(DateUtil.getSystemDate(), DateUtil.PATTERN_ONLY_DATE));
            if(queryTimeoutSecs > 0) {
            	namedParameterStatement.getStatement().setQueryTimeout(queryTimeoutSecs);
            }
            
            Long _dont_finish = 0L;
            
            ResultSet resultSet = namedParameterStatement.executeQuery();
            while(resultSet.next()){
            	_dont_finish = resultSet.getLong("dont_finish");
            }
            
            if (_dont_finish.equals(Long.valueOf(0))  ) {
            	_result = Boolean.TRUE;
            }
            	
		} catch (Exception e) {
			 LOG.info("Failed to execute  query " + _queryControl , e);
		}finally {
			if (connection != null) {
	            try {
	                connection.close();
	            } catch (SQLException e) {
	                throw new RuntimeException("Failed to close connection", e);
	            }
	        }
        }		
		
		return _result;
	}
	
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("streamId","PAYLOAD"));
	}
	
	public void loadingConfigurationForKPI(String streamId) {
		
		LOG.info("Setting dinamycs  operations  on KpiSpout");
		MongoDBClient mongoDBClient = gettingMongoDBClient();
		
		String _setTopologyName = (String) spoutConfig.get("name");
		Bson filter = Filters.eq("name", _setTopologyName);
		Document topology = mongoDBClient.find(filter, "topology");

		List<DBRef> spoutList = (List) topology.get(Constant.Fields.SPOUTS);

		List<Bson> list = new ArrayList<Bson>();
		for (int i = 0; i < spoutList.size(); i++) {
			list.add(Filters.eq("_id", spoutList.get(i).getId()));
		}

		filter = Filters.and(Filters.or(list), Filters.eq("streamId", streamId));
		Document spoutComponent = mongoDBClient.find(filter, "spout");
		
		if (spoutComponent==null) {
			LOG.info("Didn't found Spout who streamId : " + streamId );
			//lanzar exception
		}
		
		List<Object> fields = (List) spoutComponent.get("fields");

		List<String> _fieldsName = new ArrayList<>(fields.size());
		
		for (int i = 0; i < fields.size(); i++) {
			Map _mapField = (Map) fields.get(i);
			_fieldsName.add((String) _mapField.get("name"));
		}

		Fields outputFields = new Fields(_fieldsName);

		List<Object> parameters = (List) spoutComponent.get("parameters");
		Map _mapParameter = null;
		List<Column> columns = new ArrayList<Column>(parameters.size());

		for (int i = 0; i < parameters.size(); i++) {
			_mapParameter = (Map) parameters.get(i);
			columns.add(new Column((String) _mapParameter.get("name"), _mapParameter.get("value"),
					(Integer) _mapParameter.get("type")));
		}
		
		setOutputFields(outputFields);
		setSelectQuery((String) spoutComponent.get("script"));
		setQueryTimeoutSecs(60);
		setParameters(columns);
		this._sleepTime = (Long) spoutComponent.get("time");
		
	}
	
	@Override
	public Map<String, Object> getComponentConfiguration() {

		LOG.info("Setting dinamycs  operations  on KpiSpout");
		MongoDBClient mongoDBClient = gettingMongoDBClient();

		String _connectionType = "sql";
		Bson filter = Filters.eq("connectionType", _connectionType);
		List<Document> communications = mongoDBClient.findDocuments(filter, "communication");
		Assertions.assertThat(communications.size()).isGreaterThan(0);
//
//		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//		
//		this.preExecutions	= (Map<String, Object>) spoutComponent.get("preExecutions");
//		this.preExecutions.put(com.pradera.stream.constant.Constant.Fields.INITIALIZE_SCRIPT, (Boolean) spoutComponent
//				.get(com.pradera.stream.constant.Constant.Fields.INITIALIZE_SCRIPT));
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		// Getting info about connections


		/**
		 * Loading properties to be used in HikariFactory or HikariProvider
		 */

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
			map.put("maximumPoolSize", 50);
			map.remove(Constant.Fields.DATASOURCE_NAME);
			dataSourcesListMap.add(map);
		}
		
		Map<String, Object> map = Maps.newHashMap();
		map.put(Constant.Fields.DATASOURCES, dataSourcesListMap);
		return map;
	}

	

	@Override
	public void close() {
	}

	@Override
	public void activate() {
		
	}

	@Override
	public void deactivate() {
	}

	protected void executePreExecutions(Map<String, Object> preExecutions) {

		Boolean _initializeScripts = (Boolean) preExecutions
				.get(Constant.Fields.INITIALIZE_SCRIPT);

		if (_initializeScripts) {
			
			Map<String, Object> setupSqls = (Map<String, Object>) preExecutions.get("preExecutions");

			Object[] setupSqlsObj = setupSqls.values().toArray();
			
			JdbcClient jdbcClientTmp =  null;
			
			for (Object sql : setupSqlsObj) {
				try {
					jdbcClientTmp.executeSql((String) sql);
				} catch (Exception e) {
					LOG.error(" ERROR MANAGMENT  :::: " + e.getCause());
					throw e;
					//System.exit(-1);
				}
			}

			//////////////////////////////////////////////////////////////////
			// Updating state _iniatialize to False. Only the first time that to run
			////////////////////////////////////////////////////////////////// Topology.
			MongoDBClient mongoDBClient = gettingMongoDBClient();

			String _setTopologyName = (String) spoutConfig.get("name");
			Bson filter = Filters.eq("name", _setTopologyName);
			Document topology = mongoDBClient.find(filter, "topology");
			List<DBRef> spoutList = (List) topology.get(Constant.Fields.SPOUTS);
			List<Bson> list = new ArrayList<Bson>();
			for (int i = 0; i < spoutList.size(); i++) {
				list.add(Filters.eq("_id", spoutList.get(i).getId()));
			}
			filter = Filters.and(Filters.or(list), Filters.eq("name", Constant.StormComponent.JDBC_SPOUT));
			Document spoutComponent = mongoDBClient.find(filter, "settlementSpout");
			
			topology.put(Constant.Fields.INITIALIZE_SCRIPT, false);
			mongoDBClient.update(filter, spoutComponent, true, false);
			
			mongoDBClient.close();
			jdbcClientTmp =  null;
		}
		
	}

	protected MongoDBClient gettingMongoDBClient() {

		String user = (this.spoutConfig.get(Constant.SettingMongo.USER) == null
				|| StringUtils.isEmpty(this.spoutConfig.get(Constant.SettingMongo.USER).toString())) ? null
						: this.spoutConfig.get(Constant.SettingMongo.USER).toString();

		String password = (this.spoutConfig.get(Constant.SettingMongo.PASSWORD) == null
				|| StringUtils.isEmpty(this.spoutConfig.get(Constant.SettingMongo.PASSWORD).toString())) ? null
						: this.spoutConfig.get(Constant.SettingMongo.PASSWORD).toString();

		String host = this.spoutConfig.get(Constant.SettingMongo.HOST).toString();
		int port = Integer.parseInt(this.spoutConfig.get(Constant.SettingMongo.PORT).toString());
		String dbName = this.spoutConfig.get(Constant.SettingMongo.DB).toString();
		return new MongoDBClient(user, password, dbName, host, port);
	}
	
	
	public Fields getOutputFields() {
		return outputFields;
	}

	public void setOutputFields(Fields outputFields) {
		this.outputFields = outputFields;
	}

	public String getSelectQuery() {
		return selectQuery;
	}

	public void setSelectQuery(String selectQuery) {
		this.selectQuery = selectQuery;
	}

	public Integer getQueryTimeoutSecs() {
		return queryTimeoutSecs;
	}

	public void setQueryTimeoutSecs(Integer queryTimeoutSecs) {
		this.queryTimeoutSecs = queryTimeoutSecs;
	}

	public List<Column> getParameters() {
		return parameters;
	}

	public void setParameters(List<Column> parameters) {
		this.parameters = parameters;
	}
}
