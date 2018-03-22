package com.arkin.kpi.socket.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import com.dslplatform.json.DslJson;
//import com.dslplatform.json.JsonWriter;

;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("unchecked")
public final class MapperUtil {
	
	public static final String GENDER = "gender";
	public static final String FROM_DATE = "fromDate";		
	public static final String TO_DATE = "toDate";
	public static final String DATE_REFERENCED = "dateReference";
	
	private MapperUtil() {
	}
	
	
	
	public static Map<String, Object> entrySetToMap(Set<Entry<String, Object>> entrySet) {
		Map<String, Object> parameters = new HashMap<>();
		for (Entry<String, Object> entry : entrySet) {
			parameters =  (Map<String, Object>) entry.getValue();
		}
		return parameters;
	}
	

}
