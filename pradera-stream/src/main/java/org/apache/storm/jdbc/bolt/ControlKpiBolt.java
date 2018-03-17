package org.apache.storm.jdbc.bolt;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.apache.storm.Config;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseTickTupleAwareRichBolt;
import org.apache.storm.tuple.Tuple;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.collect.Maps;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.singleton.ConnectionManager;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.util.DateUtil;
import com.pradera.stream.util.jdbc.NamedParameterStatement;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"rawtypes" , "unchecked" , "unlikely-arg-type"})
public class ControlKpiBolt extends BaseTickTupleAwareRichBolt{
	
	/**
	 * 
	 */
	private static final long   serialVersionUID = -8783495568069984732L;
	private static final Logger LOG = LoggerFactory.getLogger(ControlKpiBolt.class);
	protected OutputCollector   collector;
	protected Integer queryTimeoutSecs ;
	public ConnectionProvider 	connectionProviderControl;
	public String _updateControl	= Strings.EMPTY;
	
	public ControlKpiBolt() {
	}
	
	@Override
    public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector collector ) {

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
		

		
		Map map = getPropertiesConnection( mongoDBClient);
		
		if ( ConnectionManager.lostReference("dbKpi")) {
			ConnectionManager.loadDataSources((List<Map<String, Object>>) map.get(Constant.Fields.DATASOURCES));
		}
		
		queryTimeoutSecs = Integer.parseInt(stormConf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS).toString());
		connectionProviderControl	= ((HikariCPConnectionSingletonSource)ConnectionManager.getHikariCPConnectionProvider("dbKpi")).getInstance();
		connectionProviderControl.prepare();
		
		StringBuilder _sb3 = new StringBuilder();
		_sb3.append(" update STREAM_UPDATE SET COUNT_RECORD = :COUNT_RECORD , END_HOUR = :END_HOUR ,STATUS = :FINAL_STATUS ");
		_sb3.append(" WHERE STREAM_UPDATE_NAME = :STREAM_NAME AND STATUS = :STATUS" );
		_sb3.append(" AND ID_STREAM_UPDATE_PK = ( SELECT ID_STREAM_UPDATE_PK FROM STREAM_UPDATE ORDER BY START_HOUR DESC LIMIT 1 ) " );
		_sb3.append(" AND to_char(REGISTER_DATE , 'YYYY-MM-DD') = :CURRENT_DATE  ");
		_updateControl = _sb3.toString();

		mongoDBClient.close();
		this.collector = collector;
    }
	
	
	@Override
    protected void process(Tuple tuple) {
        
		Connection 	connection 			= null;
		String 		query	 			= StringUtils.EMPTY;
		Boolean		connectionCreated	= Boolean.FALSE;
		
		try {	
				LOG.debug("----> Initial time  :" + System.currentTimeMillis()+ " for component : ControlKpiBolt ");

				String _streamId = (String) tuple.getValueByField("streamId");
				OperationPayload operationPayload = (OperationPayload) tuple.getValueByField("PAYLOAD");
				
				NamedParameterStatement namedParameterStatement	= null;
				if (operationPayload.getCode().equals(operationPayload.getTotal())) {// if tuple is the last.
					
					query = _updateControl;
					connection = connectionProviderControl.getConnection();
					namedParameterStatement	=new NamedParameterStatement(connection, query);
					namedParameterStatement.setLong("COUNT_RECORD", operationPayload.getTotal());
					namedParameterStatement.setTimestamp("END_HOUR", (Timestamp) DateUtil.getSystemTimestamp());
					namedParameterStatement.setLong("FINAL_STATUS", 1L);
					namedParameterStatement.setString("CURRENT_DATE", DateUtil.getDateFormatted(DateUtil.getSystemDate(), DateUtil.PATTERN_ONLY_DATE));
			        namedParameterStatement.setLong("STATUS", 0L);
					namedParameterStatement.setString("STREAM_NAME", _streamId);

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
					
			LOG.debug("----> Final time  :" + System.currentTimeMillis()+ " for component : ControlKpiBolt");
			this.collector.ack(tuple);
        } 
		catch (Exception e) {
            this.collector.reportError(e);
            this.collector.fail(tuple);
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

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
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
			map.put("maximumPoolSize", 50);
			map.remove(Constant.Fields.DATASOURCE_NAME);
			dataSourcesListMap.add(map);
		}
		
		Map<String, Object> map = Maps.newHashMap();
		map.put(Constant.Fields.DATASOURCES, dataSourcesListMap);
		return map;
	}
	
}
