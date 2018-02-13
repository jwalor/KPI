package com.arkin.kpi.socket.util;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public final class JsonUtils {

	private static final Log logger = LogFactory.getLog(JsonUtils.class);

	private static Gson gson = new GsonBuilder().setPrettyPrinting().create();

	private static final String BLANCK_CHARACTER = "";

	private static final String UNICODE_CHARACTER_EXPRESSION = "\\r|\\t|\\n";
	
	private JsonUtils() {
		
	}

	@SuppressWarnings(JsonFields.UNCHECKED)
	public static <T> T fromJson(String json, Class<?> clazz) {
		return (T) gson.fromJson(json, clazz);
	}

	public static String toJson(Object obj) {
		return gson.toJson(obj);
	}

	@SuppressWarnings(JsonFields.UNCHECKED)
	public static Map<String, Object> jsonToMap(String json) throws IOException {
		return new ObjectMapper().readValue(json.replaceAll(UNICODE_CHARACTER_EXPRESSION, BLANCK_CHARACTER), Map.class);
	}

	@SuppressWarnings(JsonFields.UNCHECKED)
	public static List<Object> jsonToList(String json) throws IOException {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);		
		return mapper.readValue(json.replaceAll(UNICODE_CHARACTER_EXPRESSION, BLANCK_CHARACTER), List.class);
	}

	@SuppressWarnings(JsonFields.UNCHECKED)
	public static <T> T jsonToJavaObject(String json, Class<?> obj) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		T output = null;
		try {
			output = (T) mapper.readValue(json.replaceAll(UNICODE_CHARACTER_EXPRESSION, BLANCK_CHARACTER), obj);
		}
		catch (JsonGenerationException e) {
			logger.error(e.getMessage());
		}
		catch (JsonMappingException e) {
			logger.error(e.getMessage());
		}
		catch (IOException e) {
			logger.error(e.getMessage());
		}
		return output;
	}

	@SuppressWarnings(JsonFields.DEPRECATION)
	public static String javaObjectToJson(Object obj) {
		ObjectMapper mapper = new ObjectMapper();
		String output = null;
		try {
			output = mapper.defaultPrettyPrintingWriter().writeValueAsString(obj);
		}
		catch (JsonGenerationException e) {
			logger.error(e.getMessage());
		}
		catch (JsonMappingException e) {
			logger.error(e.getMessage());
		}
		catch (IOException e) {
			logger.error(e.getMessage());
		}
		return output;
	}

	public static String formatterJson(String json) {
		String newJson = null;
		Object object = null;

		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		try {
			object = mapper.readValue(json.replaceAll(UNICODE_CHARACTER_EXPRESSION, BLANCK_CHARACTER), Object.class);
			newJson = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(object);
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		logger.debug("jsonFormatted = " + newJson);
		return newJson;
	}

	public static String blobToString(Blob blob) {
		byte[] bytes = null;
		try {
			if (blob.length() > 0){
				bytes = blob.getBytes(1, (int) blob.length());
			}
		}
		catch (SQLException e) {
			logger.error(e.getMessage());
		}
		String string = new String(bytes);
		if (!string.startsWith("{")) {
			string = string.substring(string.indexOf('{'), string.length());
		}
		return string;
	}

	public static Blob stringToBlob(String string) {
		Blob blob = null;
		byte[] bytes = string.getBytes();
		try {
			blob = new SerialBlob(bytes);
		}
		catch (Exception e) {
			logger.error(e.getMessage());
		}
		return blob;

	}

}
