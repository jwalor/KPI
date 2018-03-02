package com.pradera.stream.service.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.service.OperationWriterStrategy;
import com.pradera.stream.util.JsonUtils;

@SuppressWarnings({"rawtypes"})
public class OperationWriterWsImpl implements OperationWriterStrategy {

	private static final Log LOG = LogFactory.getLog(OperationWriterWsImpl.class);
	
	@SuppressWarnings("unchecked")
	@Override
	public void write(OperationPayload operationPayload) throws ClientProtocolException, IOException ,UnsupportedEncodingException {
		LOG.info("---- ---- Transforming. ");
		LOG.info("---- ---- OperationWriterWsImpl in :" + operationPayload.getPayload());
		
		try {
			Map currentOperation= operationPayload.getConfigOperationCurrent();
			
			 ///////////////////////////////////////////////////
			 
			 HttpPost 		post 	 = (HttpPost) currentOperation.get("post");
			 
			 Map mapEntity = new HashMap();
			 mapEntity.put(Constant.Fields.TABLE_TARGET, currentOperation.get(Constant.Fields.TABLE_TARGET));
			 String strJson = JsonUtils.toJson(mapEntity);
			 StringEntity input = new StringEntity(strJson);
			 input.setContentType("application/json; charset=ISO-8859-1");
			 post.setEntity(input);
			 
			 HttpClient 	client 	 = (HttpClient) currentOperation.get("client");
			 HttpResponse 	response = client.execute(post);
			 
			 if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
					throw new RuntimeException("Failed : HTTP error code : "
						+ response.getStatusLine().getStatusCode());
			 }
			 
			 ///////////////////////////////////////////////////
			 
			 
		}  finally {
			System.gc();
		}
		
	}

}
