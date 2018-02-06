package com.pradera.stream.service;

import com.pradera.stream.model.OperationPayload;


/**
 * 
 * @author jalor
 *
 */
public interface OperationWriterStrategy {

	void write(OperationPayload operationPayload);

}
