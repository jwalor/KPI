
package org.apache.storm.jdbc.spout;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
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
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.util.jdbc.JdbcClientExt;


/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"unchecked" , "rawtypes"})
public class KpiSpout implements IRichSpout ,  Serializable {
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private static final Logger LOG = LoggerFactory.getLogger(KpiSpout.class);

	private		boolean 				isDistributed;
	private		SpoutOutputCollector 	collector;    
    protected   transient JdbcClient 	jdbcClient;
    private 	String 					selectQuery;
    protected 	Integer 				queryTimeoutSecs;
    private 	Fields 					outputFields;
    private 	List<Column> 			parameters;
    private 	Map<String, Object>		spoutConfig;
    public 		ConnectionProvider 		connectionProvider;
    private		Long					_sleepTime;
    private		String					_streamId;
    
        
    public KpiSpout(Map<String, Object>	spoutConfig ) {
    	this(true);
    	Validate.notNull(spoutConfig);
    	this.spoutConfig = spoutConfig;
    }
    
    public KpiSpout(Properties properties) {
        this(true);
    }
    
    public KpiSpout() {
        this(true);
    }

    public KpiSpout(boolean isDistributed) {
        this.isDistributed = isDistributed;
    }

    public boolean isDistributed() {
        return this.isDistributed;
    }
    
    public void open(Map stormConf, TopologyContext context, SpoutOutputCollector collector) {
    	
    	if (HikariCPConnectionSingletonSource.lostReferences()) {
    		HikariCPConnectionSingletonSource.setHikariCPConfigMap((Map<String, Object>) stormConf.get(Constant.Fields.SOURCE));
    	}
    	
    	ConnectionProvider connectionProvider =  HikariCPConnectionSingletonSource.getInstance();
    	connectionProvider.prepare();
    	
    	this.jdbcClient = new JdbcClientExt(connectionProvider, queryTimeoutSecs);
        this.collector = collector;
        
        
        
        
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
    }
    
    public void nextTuple() {
    	
    	LOG.debug("sending tuple: ");
    	  Values values = null;
        
    	  try {
    		 
    		Validate.notNull(HikariCPConnectionSingletonSource.getInstance()) ;
            List<List<Column>> result = jdbcClient.select(this.selectQuery, this.parameters);
            
            if (result != null && result.size() != 0) {
                for (List<Column> row : result) {                                        
                	values = new Values();
                	
                    for(String field : outputFields) {
                    	 for( Column column : row) {
                             if(column.getColumnName().equalsIgnoreCase(field)) {
                                 values.add(column.getVal());
                             } 
                         }
                    } 
                    collector.emit(values);
                    //this.collector.ack(tuple);
                }
            }
            
        	Thread.sleep(_sleepTime);
        } catch (Exception e) {
            this.collector.reportError(e);
            //this.collector.fail(tuple);
        }
    }

    public void ack(Object msgId) {
    	
    }

    public void fail(Object msgId) {

    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(this.outputFields);
    }

    
    @Override
    public Map<String, Object> getComponentConfiguration() {
    	
        LOG.info("Setting dinamycs  operations  on KpiSpout");
		MongoDBClient mongoDBClient =  gettingMongoDBClient();
		
		String _setTopologyName = (String) spoutConfig.get("name");
		Bson 		filter 			= 	Filters.eq("name", _setTopologyName);
		Document 	topology		=	mongoDBClient.find(filter, "topology");
		
		List<DBRef> spoutList		=	(List)topology.get(Constant.Fields.SPOUTS);	
	
		List<Bson> list = new ArrayList<Bson>();
		for(int i=0; i<spoutList.size(); i++) {
			list.add(Filters.eq("_id", spoutList.get(i).getId()));
		}
		
		filter 		= 	Filters.and( Filters.or(list), Filters.eq("name", Constant.StormComponent.JDBC_SPOUT));
		Document 	taskComponent		=	mongoDBClient.find(filter, "settlementSpout");
		List<Object> fields				=	(List)taskComponent.get("fields");
		
		List<String> _fieldsName		=   new ArrayList<>(fields.size());
		for(int i=0; i<fields.size(); i++) {
			Map _mapField =  (Map)fields.get(i);
			_fieldsName.add((String)_mapField.get("name"));
		}

		Fields outputFields	=	new Fields(_fieldsName);

		List<Object> parameters			=	(List)taskComponent.get("parameters");
		Map	_mapParameter = null;
		List<Column> columns = new ArrayList<Column>(parameters.size());

		for(int i=0; i<parameters.size(); i++) {
			_mapParameter =  (Map)parameters.get(i);
			columns.add(new Column((String)_mapParameter.get("name"), _mapParameter.get("value"),(Integer)_mapParameter.get("type")));
		}

		setOutputFields(outputFields);
		setSelectQuery((String)taskComponent.get("script"));
		setQueryTimeoutSecs(60);
		setParameters(columns);		
		this._sleepTime	=	(Long) taskComponent.get("time");
		
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		/////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		// Getting info about connections

		List<DBRef> communications		=	(List)topology.get(Constant.Fields.COMMUNICATIONS);		
		Assertions.assertThat(communications.size()).isEqualTo(2);

		DBRef	dbf1 = communications.get(0);
		DBRef	dbf2 = communications.get(1);

		filter 		= 	Filters.and(Filters.or(Filters.eq("_id", dbf1.getId()),Filters.eq("_id", dbf2.getId())),Filters.eq("origin", Constant.Fields.SOURCE));
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
		mapSource.put("maximumPoolSize", 50);
		
		mongoDBClient.close();
		
		Map<String, Object> map = Maps.newHashMap(); 
		map.put(Constant.Fields.SOURCE, mapSource);
		
		return map;
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

	@Override
	public void close() {
		this.connectionProvider.cleanup();
	}

	@Override
	public void activate() {
	}

	@Override
	public void deactivate() {
	}
	
	protected MongoDBClient gettingMongoDBClient() {
		
		 String user 	=  (this.spoutConfig.get(Constant.SettingMongo.USER) == null || 
     		    StringUtils.isEmpty(this.spoutConfig.get(Constant.SettingMongo.USER).toString()) )	?
					null:this.spoutConfig.get(Constant.SettingMongo.USER).toString();

		String password = (this.spoutConfig.get(Constant.SettingMongo.PASSWORD) == null || 
			    			StringUtils.isEmpty(this.spoutConfig.get(Constant.SettingMongo.PASSWORD).toString()) )	?
						   null:this.spoutConfig.get(Constant.SettingMongo.PASSWORD).toString(); 
		
		String host 	= this.spoutConfig.get(Constant.SettingMongo.HOST).toString();
		int    port 	= Integer.parseInt(this.spoutConfig.get(Constant.SettingMongo.PORT).toString());
		String dbName 		= this.spoutConfig.get(Constant.SettingMongo.DB).toString();
		return new MongoDBClient(user,password,dbName,host,port);
	}
	
}
