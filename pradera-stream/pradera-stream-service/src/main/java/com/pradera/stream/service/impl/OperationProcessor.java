package com.pradera.stream.service.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.OperationPayload;


/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"rawtypes"})
public class OperationProcessor {

	private static final Log LOG = LogFactory.getLog(OperationProcessor.class);

	
	public static void process(OperationPayload operationPayload) throws Exception {
		
		
		Map configOperationMap		= 	operationPayload.getConfigOperationCurrent();
		String operationType		=	""+configOperationMap.get(Constant.OPERATION_TYPE);
		String implementationType	=	""+configOperationMap.get(Constant.IMPLEMENTATION_TYPE);

		LOG.debug("---- ---- Header Current : "+operationPayload.getHeader());
		
		process(operationType,implementationType,operationPayload);
		
		LOG.debug("---- ---- Payload Final : "+operationPayload.getPayload());
		LOG.debug("---- ---- Header Final : "+operationPayload.getHeader());

	}


	public static void process(String operationType,String implementationType,OperationPayload operationPayload) throws Exception {
		switch (operationType) {
			case Constant.OPERATION_TYPE_ENRICHER:{
				switch (implementationType) {
					case Constant.IMPLEMENTATION_ENRICHER_TYPE_NOSQL:{
						
					}break;
					case Constant.IMPLEMENTATION_ENRICHER_TYPE_QUEUE:{
						
					}break;
					case Constant.IMPLEMENTATION_ENRICHER_TYPE_SQL:{
						
					}break;
					case Constant.IMPLEMENTATION_ENRICHER_TYPE_WS:{
						
					}break;
					
					default:
						break;
				}
			}break;
			case Constant.OPERATION_TYPE_TRANSFORMER:{
				switch (implementationType) {
					case Constant.IMPLEMENTATION_TRANSFORMER_TYPE_JAVASCRIPT:{
						new OperationTransformerJavaScriptImpl().transform(operationPayload);
					}break;
					
					default:
						break;
				}
			}break;
			case Constant.OPERATION_TYPE_WRITER:{
				switch (implementationType) {
					case Constant.IMPLEMENTATION_WRITER_TYPE_NOSQL:{
					}break;
					case Constant.IMPLEMENTATION_WRITER_TYPE_QUEUE:{
					}break;
					case Constant.IMPLEMENTATION_WRITER_TYPE_SQL:{
					}break;
					case Constant.IMPLEMENTATION_WRITER_TYPE_WS:{
						new OperationWriterWsImpl().write(operationPayload);
					}break;
					
					default:
						break;
				}
			}break;
	
			default:
				break;
		}
	}

}
