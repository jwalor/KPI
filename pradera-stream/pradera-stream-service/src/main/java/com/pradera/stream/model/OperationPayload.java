package com.pradera.stream.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@SuppressWarnings({ "rawtypes", "unchecked" })
public class OperationPayload {

	Map 						payload = new HashMap();
	Map 						header = new HashMap();
	Map 						configOperationCurrent = new HashMap();
	List<Map> 					configOperations = new ArrayList();
	OperationPayload			operationPayloadUpdate;
	
	public Map getPayload() {
		return payload;
	}

	public void setPayload(Map payload) {
		this.payload = payload;
	}

	public List<Map> getConfigOperations() {
		return configOperations;
	}

	public void setConfigOperations(List<Map> configOperations) {
		this.configOperations = configOperations;
		Collections.sort(this.configOperations, new Comparator<Map>() {
			@Override
			public int compare(Map o1, Map o2) {
				return (new Long("" + o1.get("order")).compareTo(new Long("" + o2.get("order"))));
			}
		});
	}

	public Map getHeader() {
		return header;
	}

	public void setHeader(Map header) {
		this.header = header;
	}

	public Map getConfigOperationCurrent() {
		return configOperationCurrent;
	}

	public void setConfigOperationCurrent(String nameConfigOperationCurrent) {
		for (Map operationMap : this.configOperations) {
			if (nameConfigOperationCurrent.equalsIgnoreCase("" + operationMap.get("name"))) {
				configOperationCurrent = operationMap;
			}
			break;
		}
	}
	
	public void setConfigOperationCurrent(Map configOperationCurrent) {
		this.configOperationCurrent=configOperationCurrent;
	}
	
	public void putHeaderValue(String key, Object value) {
		header.put(key, value);
	}

	public void removeHeaderValue(String key) {
		header.remove(key);
	}
	
	public OperationPayload getOperationPayloadUpdate() {
		return operationPayloadUpdate;
	}

	public void setOperationPayloadUpdate(OperationPayload operationPayloadUpdate) {
		this.operationPayloadUpdate = operationPayloadUpdate;
	}

	@Override
	public String toString() {
		return "OperationPayload [payload=" + payload + ", header=" + header + ", configOperationCurrent="
				+ configOperationCurrent + ", configOperations=" + configOperations + "]";
	}
	
	
}
