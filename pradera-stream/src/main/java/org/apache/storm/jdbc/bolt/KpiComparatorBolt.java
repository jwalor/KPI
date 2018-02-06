package org.apache.storm.jdbc.bolt;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.storm.Config;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.KpiJdbcLookupMapper;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
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
import com.pradera.stream.model.CustomColumn;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.singleton.HikariCPConnectionSingletonTarget;
import com.pradera.stream.util.jdbc.JdbcClientExt;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"rawtypes" , "unchecked"})
public class KpiComparatorBolt extends JdbcLookupBolt{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3281390542266749673L;
	
	private static final Logger LOG = LoggerFactory.getLogger(KpiComparatorBolt.class);
    public static final String NATIVE_STREAM = "NATIVE_STREAM";
    public static final String LOGIC_STREAM = "LOGIC_STREAM";
    private String name; 
   
    public KpiComparatorBolt(String selectQuery, KpiJdbcLookupMapper jdbcLookupMapper ) {
		super(HikariCPConnectionSingletonTarget.getInstance(), selectQuery, jdbcLookupMapper);
		setJdbcLookupMapper(jdbcLookupMapper);
	}
    
    @Override
    public void prepare(Map stormConf, TopologyContext topologyContext, OutputCollector collector) {
        this.collector = collector;
        
        LOG.info("Setting dinamycs  operations  on CustomUpsertKpiBolt");
        
		String user 	=  (stormConf.get(Constant.SettingMongo.USER) == null || 
		    			StringUtils.isEmpty(stormConf.get(Constant.SettingMongo.USER).toString()) )	?
		    			null:stormConf.get(Constant.SettingMongo.USER).toString();
		
		String password =  (stormConf.get(Constant.SettingMongo.PASSWORD) == null || 
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
						Filters.eq("name", this.name));
		
		Document 	process		=	mongoDBClient.find(filter, "process");
		
		/**
		 * 
		 */
		List<Object> fields				=	(List)process.get("outputFields");
		List<String> _fieldsName		=  new ArrayList<>(fields.size());
		
		for(int i=0; i<fields.size(); i++) {
			Map _mapField =  (Map)fields.get(i);
			_fieldsName.add(_mapField.get("name").toString().toUpperCase());
		}
		
		Fields outputFields	=	new Fields(_fieldsName);
		
		List<Object> parameters			=	(List)process.get("paramsColumns");
		Map	_mapParameter = null;
		List<Column> queryParamColumns = new ArrayList<Column>(parameters.size());
		
		for(int i=0; i<parameters.size(); i++) {
			_mapParameter =  (Map)parameters.get(i);
			queryParamColumns.add(new CustomColumn(_mapParameter.get("name").toString().toUpperCase(),(Integer)_mapParameter.get("type")));
		}
		
		List<Object> columnFields			=	(List)process.get("columnFieldsMap");
		Map	_mapColumnFields = null;
		List<Column> queryColumnFields = new ArrayList<Column>(columnFields.size());
		
		for(int i=0; i<columnFields.size(); i++) {
			_mapColumnFields =  (Map)columnFields.get(i);
			Boolean _shouldCompare = _mapColumnFields.get("shouldCompare")!=null?(Boolean) _mapColumnFields.get("shouldCompare"):false;
			queryColumnFields.add(new CustomColumn(_mapColumnFields.get("name").toString().toUpperCase() ,(Integer)_mapColumnFields.get("type") ,_shouldCompare));
		}
		
		JdbcLookupMapper jdbcLookupMapper = new KpiJdbcLookupMapper(outputFields, queryParamColumns);
        ((KpiJdbcLookupMapper)jdbcLookupMapper).setOutputColumnFields(queryColumnFields);
        setJdbcLookupMapper(jdbcLookupMapper);
        
        this.setSelectQuery((String)process.get(Constant.Fields.SCRIPT));
        
        if(queryTimeoutSecs == null) {
            queryTimeoutSecs = Integer.parseInt(stormConf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS).toString());
        }
    	
        Map<String, Object> mapSource = getPropertiesConnection(mongoDBClient,_setTopologyName);
		if (HikariCPConnectionSingletonTarget.lostReferences()) {
			HikariCPConnectionSingletonTarget.setHikariCPConfigMap((Map<String, Object>) mapSource);
    	}
    	
		ConnectionProvider connectionProvider =  HikariCPConnectionSingletonTarget.getInstance();
		connectionProvider.prepare();
		
		mongoDBClient.close();
        this.jdbcClient = new JdbcClientExt(connectionProvider, queryTimeoutSecs);
    }
    
	
	@Override
    protected void process(Tuple tuple) {
       
		try {
			Validate.notNull(HikariCPConnectionSingletonSource.getInstance()) ;
			List<Column> columns 		= getJdbcLookupMapper().getColumns(tuple);
            List<List<Column>> result 	= jdbcClient.select(this.getSelectQuery(), columns);
            Map header 					= null;
            OperationPayload payLoad 	= new OperationPayload();
            
            /**
             *  row   : information getting of target via TCP. Example : PostgresDB.
             *  tuple : information anchor getting via streaming by Spout. Example: OracleDB.
             */
            
            if (result != null && result.size() != 0) {
                for (List<Column> row : result) {
                LOG.debug("Sending message to LogicBolt");	
                    
                	header = ((KpiJdbcLookupMapper)getJdbcLookupMapper()).convertTupleToMap(tuple);
                    header.put("NATIVE", Boolean.FALSE);
                	payLoad.setHeader(header);
                	payLoad.setPayload(((KpiJdbcLookupMapper)getJdbcLookupMapper()).convertColumnsToMap(row));
                	collector.emit(LOGIC_STREAM,tuple, new Values(payLoad));
                }
            }
             else{
            	 
            	LOG.debug("Sending message to InsertBolt");
            	
            	Map<String,Object> 	body = new HashMap<String,Object>();
            	header = new HashMap();
            	
            	header.put("NATIVE", Boolean.TRUE);
            	body.put("TUPLE", tuple);
            	payLoad.setPayload(body);
            	payLoad.setHeader(header);
            	
            	collector.emit(NATIVE_STREAM,tuple,new Values(payLoad));
            	
            }
            
            this.collector.ack(tuple);
        } catch (Exception e) {
            this.collector.reportError(e);
            this.collector.fail(tuple);
        }
    }
	
	@Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
		
		/** it's same to ::> declarer.declareStream(streamId,outputFields)
		 */
       ((KpiJdbcLookupMapper)getJdbcLookupMapper()).declareOutputFields(NATIVE_STREAM,outputFieldsDeclarer);
       outputFieldsDeclarer.declareStream(LOGIC_STREAM, new Fields("Constants.PAYLOAD"));
       
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
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
		mapSource.put("maximumPoolSize", 40);
		
		return mapSource;
	}
	
}
