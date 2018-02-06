package com.pradera.stream.service;

import java.util.Map;
import com.mongodb.DB;
import com.mongodb.DBObject;


/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("rawtypes")
public interface GenericMongoDAO {

	DBObject save( DB db ,Map<String, Object> documentMap);

	DBObject delete(DB db ,Map<String, Object> documentMap,String []ignoreDeleteTables);

	Map cloneDocument(DB db ,Map documentMap, Boolean withoutId);

//	Boolean validateQuery(String query, Map<String, Object> header);
//
//	Object executeQuery(String query, Map<String, Object> header);
//	
//	<T> List<T> findAnyQuery(Map<String, Object> in,Class<T> entityClass);
//	
//	List<Object> findDocuments(Map<String, Object> request);

}
