package org.apache.storm.jdbc.bolt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.core.util.Assert;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Maps;
import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.service.impl.OperationProcessor;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"rawtypes" , "unchecked"})
public class LogicKpiBolt extends BaseTickTupleAwareRichBolt{
	
	/**
	 * 
	 */
	private static final long 	serialVersionUID 	= -8783495568069984732L;
	
	private static final Logger LOG 				= LoggerFactory.getLogger(LogicKpiBolt.class);
	protected OutputCollector 	collector;
    public static final String  UPDATE_STREAM 		= "UPDATE_STREAM";
    public static final String  INSERT_STREAM 		= "INSERT_STREAM";	
    private Map<String, Object> logicMaps =  Maps.newHashMap();
    
	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		
		LOG.info("Setting dinamycs  operations  on LogicKpiBolt");
		
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
						Filters.eq("name", Constant.StormComponent.LOGIC_BOLT));
		
		Document 	process		=	mongoDBClient.find(filter, "bolt");
		List<Object> 	streamsList	=	(List)process.get("streams");
		
		Map	streamMap = null;
		Map	parametersMap = null;
		
		for(int i=0; i< streamsList.size(); i++) {
			streamMap =  (Map)streamsList.get(i);
			parametersMap =  Maps.newHashMap();
			
			loadingConfigurationOperations(streamMap, parametersMap);
			
			logicMaps.put((String)streamMap.get("streamName"), parametersMap);
		}
		
		mongoDBClient.close();
		this.collector = collector;
		
	}
	
	private void loadingConfigurationOperations(Map streamMap , Map	parametersMap) {
		
		Document d = (Document) streamMap.get("executors");
		
		Object[]  executorArray = (Object[]) d.values().toArray();
		
		List<Map>	configOperations	;
		
		if (!ArrayUtils.isEmpty(executorArray)){
			configOperations =   new  ArrayList<Map>();
			
			Map executor = null;
			for (int i = 0; i < executorArray.length; i++) {
				Document obj = (Document) executorArray[i];
				executor = new  HashMap<String , Object>();
				executor.put(Constant.OPERATION_TYPE, obj.get(Constant.OPERATION_TYPE));
				executor.put(Constant.IMPLEMENTATION_TYPE, obj.get(Constant.IMPLEMENTATION_TYPE));
				executor.put(Constant.Fields.SCRIPT_TYPE, obj.get(Constant.Fields.SCRIPT_TYPE));
				executor.put(Constant.Fields.SCRIPT, obj.get(Constant.Fields.SCRIPT));
				configOperations.add(executor);
			}			
			parametersMap.put("configOperations",configOperations );
		}
		
	}

	@Override
	protected void process(Tuple tuple) {
		
		try {
			
			LOG.info("----> Processing logic about kpi's implementation on  LogicKpiBolt ---- ");
			LOG.debug("----> Initial time :" + System.currentTimeMillis());
			
			String _streamId = (String) tuple.getValueByField("streamId");
			OperationPayload operationPayload = (OperationPayload) tuple.getValueByField("PAYLOAD");		
			
			Map upsertKpiMap	  =	(Map) logicMaps.get(_streamId);
			List<Map>	configOperations = (List<Map>) upsertKpiMap.get("configOperations");
			for (Map configOperationMap : configOperations) {
				operationPayload.setConfigOperationCurrent(configOperationMap);
				OperationProcessor.process(operationPayload);
				
			}
			
			LOG.debug("----> Final time  :" + System.currentTimeMillis());
			Object result = operationPayload.getHeader().get(Constant.ACTION_RESULT);
			
			if ( Assert.isEmpty(result)){ 
				collector.ack(tuple);
				return;
			}
			
			String _action =  (String)result;
			
			if (_action.equalsIgnoreCase(Constant.UPSERT_BOLT)){

				collector.emit(UPDATE_STREAM,tuple, new Values(_streamId,operationPayload));
				collector.emit(INSERT_STREAM,tuple, new Values(_streamId,operationPayload));
				
			}else if (_action.equalsIgnoreCase(Constant.UPDATE_BOLT)){
				collector.emit(UPDATE_STREAM,tuple, new Values(_streamId,operationPayload));
			}else if (_action.equalsIgnoreCase(Constant.INSERT_BOLT)){
				collector.emit(INSERT_STREAM,tuple, new Values(_streamId,operationPayload));
			}
			
			collector.emit("CONTROL_STREAM",tuple, new Values(_streamId,operationPayload));
			this.collector.ack(tuple);
			
		}catch (Exception e) {
			LOG.error(e.getMessage());
            this.collector.reportError(e);
            this.collector.fail(tuple);
        }
	}
	
	
	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declareStream("CONTROL_STREAM", new Fields("streamId","PAYLOAD"));
		declarer.declareStream(UPDATE_STREAM,new Fields("streamId","PAYLOAD"));
		declarer.declareStream(INSERT_STREAM,new Fields("streamId","PAYLOAD"));		
	}
	
}
