package org.apache.storm.jdbc.bolt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.task.OutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseTickTupleAwareRichBolt;
import org.apache.storm.tuple.Tuple;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class WriterApiBolt extends BaseTickTupleAwareRichBolt{

	/**
	 * 
	 */
	private static final long 	serialVersionUID 	= -8783495568069984732L;
	private static final Logger LOG 				= LoggerFactory.getLogger(WriterApiBolt.class);
	protected 			OutputCollector 	collector;
	private				List<Map>			configOperations	;

	@Override
	public void prepare(Map stormConf, TopologyContext context,
			OutputCollector collector) {
		

		this.collector = collector;
		LOG.info("Setting dinamycs  operations  on WriterApiBolt");
		
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
				Filters.eq("name", Constant.StormComponent.WRITER_BOLT));


		Document 	process		=	mongoDBClient.find(filter, "bolt");
		Document d = (Document) process.get("executors");
		Object[]  executorArray = (Object[]) d.values().toArray();


		if (!ArrayUtils.isEmpty(executorArray)){
			configOperations =   new  ArrayList<Map>();

			Map executor = null;
			for (int i = 0; i < executorArray.length; i++) {
				Document obj = (Document) executorArray[i];
				executor = new  HashMap<String , Object>();
				
				executor.put(Constant.OPERATION_TYPE, obj.get(Constant.OPERATION_TYPE));
				executor.put(Constant.IMPLEMENTATION_TYPE, obj.get(Constant.IMPLEMENTATION_TYPE));
				
				if ( obj.get("setting") instanceof Document) {

					Document settingDoc =(Document)obj.get("setting");
					String _url = (String)settingDoc.get("url") + (String)settingDoc.get("uri");
					HttpPost post = new HttpPost(_url);
					post.setHeader("Accept", (String)settingDoc.get("Accept"));
					post.setHeader("Accept-Language", (String)settingDoc.get("Accept-Language"));
					post.setHeader("Content-Type", (String)settingDoc.get("Content-Type"));
					executor.put("method", settingDoc.get("method"));
					executor.put("client", HttpClientBuilder.create().build());
					executor.put("post", post);
					executor.put(Constant.Fields.TABLE_TARGET , topology.get(Constant.Fields.TABLE_TARGET));

				}

				configOperations.add(executor);
			}			
		}
		mongoDBClient.close();

	}


	@Override
	protected void process(Tuple tuple) {

		try {

			LOG.info("----> Processing logic about kpi's implementation on  WriterApiBolt ---- ");
			LOG.info("----> Initial time :" + System.currentTimeMillis());

			LOG.info("----> Woking!!!!!!!");

			OperationPayload operationPayload = (OperationPayload) tuple.getValueByField("PAYLOAD");
			
			for (Map configOperationMap : configOperations) {
				
				String operationType		=	""+configOperationMap.get(Constant.OPERATION_TYPE);
				String implementationType	=	""+configOperationMap.get(Constant.IMPLEMENTATION_TYPE);
				configOperationMap.put(Constant.OPERATION_TYPE, operationType);
				configOperationMap.put(Constant.IMPLEMENTATION_TYPE, implementationType);
				
				operationPayload.setConfigOperationCurrent(configOperationMap);
				OperationProcessor.process(operationPayload);
				
			}

			LOG.info("----> Final time :" + System.currentTimeMillis());
			this.collector.ack(tuple);
		}catch (Exception e) {
			LOG.error(e.getMessage());
			this.collector.reportError(e);
			this.collector.fail(tuple);
		}
	}


	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
	}

}
