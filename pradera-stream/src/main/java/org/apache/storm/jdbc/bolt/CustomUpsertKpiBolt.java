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
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseTickTupleAwareRichBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.apache.storm.util.Constant;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.fest.assertions.Assertions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.singleton.HikariCPConnectionSingletonTarget;
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
	private ConnectionProvider  connectionProvider;
	protected OutputCollector   collector;
	private	List<Map>			configOperations	;
	
	
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
		List<DBRef> processes		=	(List)topology.get(Constant.Fields.PROCESSES);	
		
		filter 		= 	Filters.and( Filters.or(
									 Filters.eq("_id", processes.get(0).getId())
									 ,Filters.eq("_id", processes.get(1).getId())
									 ,Filters.eq("_id", processes.get(2).getId())
									 ,Filters.eq("_id", processes.get(3).getId())
									 ,Filters.eq("_id", processes.get(4).getId())
									 ,Filters.eq("_id", processes.get(5).getId())
									 ,Filters.eq("_id", processes.get(6).getId())),
						Filters.eq("name", this._name));
		
		Document 	process		=	mongoDBClient.find(filter, "process");
		Document d = (Document) process.get("executors");
		Object[]  executorArray = (Object[]) d.values().toArray();
		
		
		if (!ArrayUtils.isEmpty(executorArray)){
			configOperations =   new  ArrayList<Map>();
			
			Map executor = null;
			for (int i = 0; i < executorArray.length; i++) {
				Document obj = (Document) executorArray[i];
				executor = new  HashMap<String , Object>();
				executor.put(Constant.Fields.SCRIPT_TYPE, obj.get(Constant.Fields.SCRIPT_TYPE));
				executor.put(Constant.Fields.SCRIPT, obj.get(Constant.Fields.SCRIPT));
				configOperations.add(executor);
			}			
			
		}
		
		 Map<String, Object> mapSource = getPropertiesConnection(mongoDBClient, _setTopologyName);
		if (HikariCPConnectionSingletonTarget.lostReferences()) {
			HikariCPConnectionSingletonTarget.setHikariCPConfigMap((Map<String, Object>) mapSource);
    	}
		
		this.connectionProvider	 = HikariCPConnectionSingletonTarget.getInstance();
		connectionProvider.prepare();
		
		mongoDBClient.close();
		this.collector = collector;
    }
	
	
	@Override
    protected void process(Tuple tuple) {
        
		Connection connection = null;
		String query	 = StringUtils.EMPTY;
		
		Validate.notNull(HikariCPConnectionSingletonSource.getInstance()) ;
		try {	
				OperationPayload operationPayload = (OperationPayload) tuple.getValueByField("Constant.PAYLOAD");
	
				for (Map configOperationMap : configOperations) {

					query =  VelocityUtils.loadTemplateVM(configOperationMap.get(Constant.Fields.SCRIPT) ,operationPayload.getPayload());
		            connection = connectionProvider.getConnection();
		            boolean autoCommit = connection.getAutoCommit();
		            if(autoCommit) {
		                connection.setAutoCommit(false);
		            }
		            
		            LOG.debug("Executing query {}", query);
		            PreparedStatement preparedStatement = connection.prepareStatement(query);

		            /*if(queryTimeoutSecs > 0) {
		                preparedStatement.setQueryTimeout(queryTimeoutSecs);
		            }*/
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
				
				LOG.info("----> Final time  :" + System.currentTimeMillis());
				
				////////////////////////////////////////////////////////////////////////////////////////////
				/**
				*  Sending tuple to WriterBolt.
				*/
				///////////////////////////////////////////////////////////////////////////////////////////
			String _streamId = tuple.getSourceStreamId();
			if (_streamId.equalsIgnoreCase("INSERT_STREAM")) {
				LOG.info("::::::::::::::::::::::::::: SENDING TUPLE TO WRITER BOLT !!!!!!!!!!!!!!!!!!!!!!!!!!!");
				collector.emit("REST_STREAM",tuple, new Values(operationPayload));
			}
			
			this.collector.ack(tuple);
        }
		catch (SQLException e) {
            throw new RuntimeException("Failed to execute  query " + query, e);
        } 
		catch (Exception e) {
            this.collector.reportError(e);
            this.collector.fail(tuple);
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
		declarer.declareStream("REST_STREAM", new Fields("Constants.PAYLOAD"));
	}

	protected Map getPropertiesConnection( MongoDBClient mongoDBClient , String topologyName) {
		
		// Getting info about connections
		Bson filter 		= 	Filters.eq("name", topologyName);
		Document 	topology	=	mongoDBClient.find(filter, "topology");
		List<DBRef> communications		=	(List)topology.get(Constant.Fields.COMMUNICATIONS);		
		Assertions.assertThat(communications.size()).isEqualTo(2);

		DBRef	dbf1 = communications.get(0);
		DBRef	dbf2 = communications.get(1);

		filter 		= 	Filters.and(Filters.or(Filters.eq("_id", dbf1.getId()),Filters.eq("_id", dbf2.getId())),Filters.eq("origin", Constant.Fields.TARGET));
		Document 	communicationSource	=	mongoDBClient.find(filter, "communication");
		Assertions.assertThat(communicationSource).isNotNull();
		
		/**
		 *  Loading properties to be used in HikariFactory or HikariProvider
		 */
		Map<String, Object> mapSource = Maps.newHashMap();
		mapSource.putAll((Map) communicationSource.get(Constant.Fields.SETTING));
		
		mapSource.put("dataSource."+Constant.Fields.URL, mapSource.get(Constant.Fields.URL));
		mapSource.remove(Constant.Fields.URL);
		mapSource.put("dataSource."+Constant.Fields.USER, mapSource.get(Constant.Fields.USER));
		mapSource.remove(Constant.Fields.USER);
		mapSource.put("dataSource."+Constant.Fields.PASSWORD, mapSource.get(Constant.Fields.PASSWORD));
		mapSource.remove(Constant.Fields.PASSWORD);
		mapSource.put("registerMbeans", Boolean.TRUE);
		mapSource.put("maximumPoolSize", 39);
		
		return mapSource;
	}
	
}
