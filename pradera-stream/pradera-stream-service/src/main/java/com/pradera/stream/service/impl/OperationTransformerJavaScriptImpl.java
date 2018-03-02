package com.pradera.stream.service.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.model.OperationPayload;
import com.pradera.stream.service.OperationTransformerStrategy;
import com.pradera.stream.util.JavaScriptUtil;

@SuppressWarnings({"rawtypes"})
public class OperationTransformerJavaScriptImpl implements OperationTransformerStrategy {

	private static final Log LOG = LogFactory.getLog(OperationTransformerJavaScriptImpl.class);

	public void transform(OperationPayload operationPayload) throws Exception {
		LOG.debug("---- ---- Transforming. ");
		LOG.debug("---- ---- OperationTransformerJavaScriptImpl in :" + operationPayload.getPayload());
		
		try {
			Map currentOperation= operationPayload.getConfigOperationCurrent();
			JavaScriptUtil.executeJS(Constant.JAVASCRIPT_FUNCTION_NAME_DEFAULT,""+currentOperation.get(Constant.Fields.SCRIPT), operationPayload.getHeader(),operationPayload.getPayload());

		}  finally {
			System.gc();
		}
	}

}
