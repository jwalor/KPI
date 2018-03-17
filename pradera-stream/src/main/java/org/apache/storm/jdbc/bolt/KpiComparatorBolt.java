package org.apache.storm.jdbc.bolt;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.storm.Config;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.KpiJdbcLookupMapper;
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
	private Map<String, Object> comparatorsMap =  Maps.newHashMap();
   
    public KpiComparatorBolt(HikariCPConnectionProvider hikariCPConnectionProvider ,String selectQuery, KpiJdbcLookupMapper jdbcLookupMapper ) {
		super(hikariCPConnectionProvider, selectQuery, jdbcLookupMapper);
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
		
		
		List<DBRef> processes		=	(List)topology.get("bolts");	
		
		filter 		= 	Filters.and( Filters.or(
									 Filters.eq("_id", processes.get(0).getId())
									 ,Filters.eq("_id", processes.get(1).getId())
									 ,Filters.eq("_id", processes.get(2).getId())
									 ,Filters.eq("_id", processes.get(3).getId())
									 ,Filters.eq("_id", processes.get(4).getId())
									 ,Filters.eq("_id", processes.get(5).getId())),
						Filters.eq("name", this.name));
		
		Document 		process		=	mongoDBClient.find(filter, "bolt");
		List<Object> 	streamsList	=	(List)process.get("streams");
		
		Map map = getPropertiesConnection( mongoDBClient);
		
		if ( ConnectionManager.lostReference("dbKpi")) {
			ConnectionManager.loadDataSources((List<Map<String, Object>>) map.get(Constant.Fields.DATASOURCES));
		}
		
		Map	streamMap = null;
		Map	parametersMap = null;
		for(int i=0; i< streamsList.size(); i++) {
			streamMap =  (Map)streamsList.get(i);
			parametersMap =  Maps.newHashMap();
			parametersMap.put(Constant.Fields.SCRIPT, (String)streamMap.get(Constant.Fields.SCRIPT));
			loadingConfigurationJdbcMapper(streamMap, parametersMap);
			loadingConfigurationJdbcClient(streamMap, parametersMap, stormConf);
			comparatorsMap.put((String)streamMap.get("streamName"), parametersMap);
		}
		
		mongoDBClient.close();
    }
    
	
	@Override
    protected void process(Tuple tuple) {
			
		try {
			
			String _streamId = (String) tuple.getValueByField("streamId");
			
			OperationPayload _operationPayload = (OperationPayload) tuple.getValueByField("PAYLOAD");
			
			

			Map comparatorMap	  =	(Map) comparatorsMap.get(_streamId);
			
			JdbcLookupMapper jdbcLookupMapper = (JdbcLookupMapper) comparatorMap.get("jdbcLookupMapper");
			JdbcClient jdbcClient =  (JdbcClient) comparatorMap.get("jdbcClient");
			String query		  =  (String) comparatorMap.get(Constant.Fields.SCRIPT);
			
			KpiTuple _tuple	= new KpiTuple( (List<Object>) _operationPayload.getValues(),
											((KpiJdbcLookupMapper)jdbcLookupMapper).getOutputFields());
			
			List<Column> parameters  =  ((KpiJdbcLookupMapper)jdbcLookupMapper).getColumnsKpi(_tuple);
			
			List<List<Column>> result 	= jdbcClient.select(query, parameters);
			
			Map header 					= null;
            OperationPayload payLoad 	= new OperationPayload();
            payLoad.setCode(_operationPayload.getCode());
        	payLoad.setTotal(_operationPayload.getTotal());
            /**
             *  row   : information getting of target via TCP. Example : PostgresDB.
             *  tuple : information anchor getting via streaming by Spout. Example: OracleDB.
             */
            
            if (result != null && result.size() != 0) {
                for (List<Column> row : result) {
                LOG.debug("Sending message to LogicBolt");	
                    
                	header = ((KpiJdbcLookupMapper)jdbcLookupMapper).convertTupleToMap(_tuple);
                    header.put("NATIVE", Boolean.FALSE);
                	payLoad.setHeader(header);
                	payLoad.setPayload(((KpiJdbcLookupMapper)jdbcLookupMapper).convertColumnsToMap(row));
                	
                	collector.emit(LOGIC_STREAM,tuple, new Values(_streamId,payLoad));
                }
            }
             else{
            	 
            	LOG.debug("Sending message to InsertNativeBolt");
            	
            	Map<String,Object> 	body = new HashMap<String,Object>();
            	body.put("TUPLE", _tuple);
            	payLoad.setPayload(body);
            	
            	collector.emit(NATIVE_STREAM,tuple,new Values(_streamId,payLoad));
            	collector.emit("CONTROL_STREAM",tuple,new Values(_streamId,payLoad));
            }
            
            this.collector.ack(tuple);
        } catch (Exception e) {
            this.collector.reportError(e);
            this.collector.fail(tuple);
        }
    }
	
	@Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
	   outputFieldsDeclarer.declareStream("CONTROL_STREAM", new Fields("streamId","PAYLOAD"));
       outputFieldsDeclarer.declareStream(NATIVE_STREAM, new Fields("streamId","PAYLOAD"));
       outputFieldsDeclarer.declareStream(LOGIC_STREAM, new Fields("streamId","PAYLOAD"));
    }
	
	private void loadingConfigurationJdbcClient(Map streamMap , Map	parametersMap,Map stormConf) {
		
		 if(queryTimeoutSecs == null) {
	            queryTimeoutSecs = Integer.parseInt(stormConf.get(Config.TOPOLOGY_MESSAGE_TIMEOUT_SECS).toString());
	     }
	     
		ConnectionProvider connectionProvider;
		connectionProvider = ((HikariCPConnectionSingletonSource)ConnectionManager.getHikariCPConnectionProvider("dbKpi")).getInstance();
	     connectionProvider.prepare();
	     JdbcClient jdbcClient = new JdbcClientExt(connectionProvider, queryTimeoutSecs);
	     parametersMap.put("jdbcClient", jdbcClient);
	}
	
	private void loadingConfigurationJdbcMapper(Map streamMap , Map	parametersMap) {
		
		List<Object> fields				=	(List)streamMap.get("outputFields");//
		List<String> _fieldsName		=  new ArrayList<>(fields.size());
		
		for(int i=0; i<fields.size(); i++) {
			Map _mapField =  (Map)fields.get(i);
			_fieldsName.add(_mapField.get("name").toString().toUpperCase());
		}
		
		Fields outputFields	=	new Fields(_fieldsName);
		
		List<Object> parameters			=	(List)streamMap.get("paramsColumns");//
		Map	_mapParameter = null;
		List<Column> queryParamColumns = new ArrayList<Column>(parameters.size());
		
		for(int i=0; i<parameters.size(); i++) {
			_mapParameter =  (Map)parameters.get(i);
			queryParamColumns.add(new CustomColumn(_mapParameter.get("name").toString().toUpperCase(),(Integer)_mapParameter.get("type")));
		}
		
		List<Object> columnFields			=	(List)streamMap.get("columnFieldsMap");//
		Map	_mapColumnFields = null;
		List<Column> queryColumnFields = new ArrayList<Column>(columnFields.size());
		
		for(int i=0; i<columnFields.size(); i++) {
			_mapColumnFields =  (Map)columnFields.get(i);
			Boolean _shouldCompare = _mapColumnFields.get("shouldCompare")!=null?(Boolean) _mapColumnFields.get("shouldCompare"):false;
			queryColumnFields.add(new CustomColumn(_mapColumnFields.get("name").toString().toUpperCase() ,(Integer)_mapColumnFields.get("type") ,_shouldCompare));
		}
		
		JdbcLookupMapper jdbcLookupMapper = new KpiJdbcLookupMapper(outputFields, queryParamColumns);
        ((KpiJdbcLookupMapper)jdbcLookupMapper).setOutputColumnFields(queryColumnFields);
        
        parametersMap.put("jdbcLookupMapper", jdbcLookupMapper);
        
	}
	
	protected Map getPropertiesConnection( MongoDBClient mongoDBClient ) {
		
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
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
