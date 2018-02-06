package com.pradera.stream.util;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.dslplatform.json.DslJson;
//import com.dslplatform.json.JsonWriter;

;

public final class MapperUtil {
	
	private static final Logger LOG = LoggerFactory.getLogger(MapperUtil.class);
	public static final String GENDER = "gender";
	private static final String V_KEY_HAS_PARAMETER_ADDITIONAL = "hasParameterAdditionals";
	public static final String FROM_DATE = "fromDate";		
	public static final String TO_DATE = "toDate";
	public static final String DATE_REFERENCED = "dateReference";
	
	private MapperUtil() {
	}
	
	
	
	/*
	public static void castParameter(Map<String, JobParameter> jobParameters, String k, Object v){
		if(v instanceof String){
			jobParameters.put(k, new JobParameter((String)v));
		}else if(v instanceof Date){
			jobParameters.put(k, new JobParameter((Date)v));
		}else if(v instanceof Double){
			jobParameters.put(k, new JobParameter((Double)v));
		}else if(v instanceof Long){
			jobParameters.put(k, new JobParameter((Long)v));
		}else{
			DslJson dslJson = new DslJson<>();			
			JsonWriter writer = dslJson.newWriter();
			try {
				dslJson.serialize(writer,v);
				jobParameters.put(k, new JobParameter(writer.toString()));
			} catch (IOException e) {
				LOG.warn(e.getMessage(), e);
			}						
		}
	}*/
	
	
	public static Map<String, Object> entrySetToMap(Set<Entry<String, Object>> entrySet) {
		Map<String, Object> parameters = new HashMap<>();
		for (Entry<String, Object> entry : entrySet) {
			parameters.put(entry.getKey(), entry.getValue());
		}
		return parameters;
	}
	
	
	/*
	public static Map<String, Object> configMapExecutor(String script,Map<String, Object> parameters){
		Map<String, Object> executorMap = new HashMap<>();
		executorMap.put(Field.Batch.SCRIPT, script);					
		executorMap.put(Field.Batch.PARAMETERS,parameters);
		return executorMap;
	}*/
	
	/*
	public static Map<String, Object> jobParametersToMap(JobParameters headers, Map<String, Object> ... parametersToGet) {		
		Map<String, Object> parameters = new HashMap<>();
		if(headers.getParameters().containsKey(V_KEY_HAS_PARAMETER_ADDITIONAL)&&new Boolean(""+headers.getParameters().get(V_KEY_HAS_PARAMETER_ADDITIONAL))){
				for (Entry<String, JobParameter> entryParameter : headers.getParameters().entrySet()) {
					JobParameter valueJobParameter=headers.getParameters().get(entryParameter.getKey());
					if(valueJobParameter!=null){
						if(JsonUtils.hasJsonListValue(valueJobParameter.toString())){
							parameters.put(entryParameter.getKey(),JsonUtils.jsonToList(valueJobParameter.toString()));
						}else if(JsonUtils.hasJsonMapValue(valueJobParameter.toString())){
							LOG.warn("Job Parameter "+entryParameter.getKey()+" , ignored.");
							//parameters.put(entryParameter.getKey(),JsonUtils.deserialize(valueJobParameter.toString(), Map.class));
						}else{
							parameters.put(entryParameter.getKey(),valueJobParameter.toString());
						}
					}
				}
			
		}else{
			Map<String, Object> parameterConf = new HashMap<>();
			for (Map<String, Object> map : parametersToGet) {
				parameterConf.putAll(map);
			}		
			
			for (Map.Entry<String, Object> entry : parameterConf.entrySet()) {
				if (headers.getParameters().containsKey(entry.getKey())){
					parameters.put(entry.getKey(), headers.getParameters().get(entry.getKey()).getValue());
				}
			}
		}
		return parameters;
	}
	*/
	
	/*
	public static Map<String, Object> mapCalculationRequest(JobParameters headers, Map measure) {
		Map<String, Object> paramsVal = new HashMap<>();

		List<Map> providers = (List) JsonUtils.jsonToList(headers.getString("providers"));
		List<String> strProviders = new ArrayList<>();
		for (Map provider : providers) {
			strProviders.add((String)provider.get("providerId"));
		}		
		
		Date beginDate = DateUtil.getDate((String) headers.getString("beginDate"), DateUtil.PATTERN_ONLY_DATE);
		long beginDateSeconds = DateUtil.dateToLongTime(DateUtil.getStartOfDay(beginDate));
		
		Date endDate = DateUtil.getDate((String) headers.getString("endDate"), DateUtil.PATTERN_ONLY_DATE);
		long endDateSeconds = DateUtil.dateToLongTime(DateUtil.getStartOfDay(endDate));
		
		String cqmReportId = headers.getString(Field.Batch.EXTERNAL_ID);
		
		paramsVal.put("measure_id", measure.get("hqmfId"));
		paramsVal.put("effective_start_date", beginDateSeconds);
		paramsVal.put("effective_date", endDateSeconds);		
		paramsVal.put("providers", strProviders);
		paramsVal.put("external_id", cqmReportId);
		
		return paramsVal;
	}
	*/
	
	/*
	public static Map<String, Object> mapToAuditRequest(String hqmfId, String status, String cqmReportId,
			String calculationId, String subId, String statusDetail,String type) {
		Map<String, Object> mapToAuditRequest = new HashMap<>();
		mapToAuditRequest.put("external_id", cqmReportId);
		mapToAuditRequest.put("calculation_id", null != calculationId ? calculationId : StringUtils.EMPTY);
		mapToAuditRequest.put("measure_id", null != hqmfId ? hqmfId : StringUtils.EMPTY);
		mapToAuditRequest.put("sub_id", null != subId ? subId : StringUtils.EMPTY);
		mapToAuditRequest.put("status", status);
		mapToAuditRequest.put("status_type", type);
		mapToAuditRequest.put("status_detail", null != statusDetail ? statusDetail : StringUtils.EMPTY);
		return mapToAuditRequest;
	}

	public static Map<String,Object> mapToAuditPatient(QSIGenericMessage item){
		Map patient = new HashMap<>();
		patient.put("patientId", item.getHeaders().get("patientId"));
		
		Map patientHeader = !ObjectUtils.isEmpty(item.getGridPayload().get("patient"))?(Map)item.getGridPayload().get("patient"):new HashMap<>();		
		Map gender = !ObjectUtils.isEmpty(patientHeader.get(GENDER))?(Map)patientHeader.get(GENDER):new HashMap<>();
		
		patient.put("birthdate", !ObjectUtils.isEmpty(patientHeader.get("birthDate"))?patientHeader.get("birthDate"):null);
		patient.put(GENDER, !ObjectUtils.isEmpty(gender.get("name"))?gender.get("name"):null);
		
		
		patient.put("firstName", item.getHeaders().get("firstName"));
		patient.put("lastName", item.getHeaders().get("lastName"));
		
		
		return patient;
	}
	*/
	/*
	public static Map<String, Object> getJobMessage(Object message, Map<String, Object> headers) {
		LOG.info(" headers : {}", headers);
		LOG.info(" message : {}", message);
		String jsonString;
		Map<String, Object> mapMessage;
		try {
			Message msg = (Message) message;
			jsonString = new String(msg.getBody());
			mapMessage = JsonUtils.deserialize(jsonString, Map.class);
		} catch (Exception e) {
			mapMessage = new HashMap<>();
			LOG.error(e.getMessage(), e);
		}
		return mapMessage;
	}*/
	
	public static void mapToScheduler(Map scheduleJobConfig,String dateReference){
		String subtractFrom = (String)scheduleJobConfig.get("subtractFrom");
		String subtractTo = (String)scheduleJobConfig.get("subtractTo");
		String timeType = (String)scheduleJobConfig.get("timeType");
		scheduleJobConfig.remove("subtractFrom");
		scheduleJobConfig.remove("subtractTo");
		scheduleJobConfig.remove("timeType");
		scheduleJobConfig.remove(DATE_REFERENCED);
		
		LocalDateTime localDateReference  = DateUtil.getDateTimeFromString(dateReference,DateUtil.PATTERN_LOCAL_DATE_TIME);							
		LocalDateTime fromDate = DateUtil.dateTimeMinusTime(localDateReference, new Long(subtractFrom), new Integer(timeType));							
		LocalDateTime toDate = DateUtil.dateTimeMinusTime(localDateReference, new Long(subtractTo), new Integer(timeType));							
		Instant instantFromDate = fromDate.atZone(ZoneId.systemDefault()).toInstant();
		Instant instantToDate = toDate.atZone(ZoneId.systemDefault()).toInstant();
		scheduleJobConfig.put(FROM_DATE,DateUtil.getDateStringFormatted(DateUtil.getStartOfDay(Date.from(instantFromDate)),DateUtil.PATTERN_LOCAL_DATE_TIME));
		scheduleJobConfig.put(TO_DATE,  DateUtil.getDateStringFormatted(DateUtil.getEndOfDay(Date.from(instantToDate)),DateUtil.PATTERN_LOCAL_DATE_TIME));
	}
	
}
