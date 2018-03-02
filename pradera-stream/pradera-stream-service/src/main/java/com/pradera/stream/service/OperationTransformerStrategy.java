package com.pradera.stream.service;

import com.pradera.stream.model.OperationPayload;


/**
 * 
 * @author jalor
 *
 */
public interface OperationTransformerStrategy {

	void transform(OperationPayload operationPayload)  throws Exception;

}
