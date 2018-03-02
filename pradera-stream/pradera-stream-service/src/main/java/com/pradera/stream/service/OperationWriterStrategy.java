package com.pradera.stream.service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;

import com.pradera.stream.model.OperationPayload;


/**
 * 
 * @author jalor
 *
 */
public interface OperationWriterStrategy {

	void write(OperationPayload operationPayload) throws ClientProtocolException, IOException ,UnsupportedEncodingException;

}
