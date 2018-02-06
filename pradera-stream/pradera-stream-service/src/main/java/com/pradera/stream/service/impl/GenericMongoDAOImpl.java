package com.pradera.stream.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.pradera.stream.service.GenericMongoDAO;



public class GenericMongoDAOImpl implements GenericMongoDAO {

	private static final Log LOG = LogFactory.getLog(GenericMongoDAOImpl.class);

	

	public static final String ID = "_id";
	public static final String RESULT_QUERY = "retval";
	public static final String RECORD_TYPE = "recordType";
	public static final String COLLECTION_NAME = "collectionName";

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DBObject save( DB db ,Map<String, Object> documentMap) {

		DBObject dBObjectResult = new BasicDBObject();
		if (documentMap == null) {
			return null;
		}
		if (!documentMap.containsKey(COLLECTION_NAME)) {
			return new BasicDBObject(documentMap);
		}
		String collectionName = documentMap.get(COLLECTION_NAME).toString();
		LOG.debug("---- ---- ----- IN [" + collectionName + "]");
		documentMap.forEach((key, value) -> {
			if (value instanceof List) {
				List<Object> listRef = new ArrayList<>();
				for (Object element : (List<Object>) value) {
					if (element instanceof Map) {

						String collectionNameRef = key.substring(0, key.length() - 1);
						DBObject o = save(db,(Map) element);
						if (o != null) {
							if (((Map) o).containsKey(COLLECTION_NAME)) {
								ObjectId oi = (ObjectId) o.get(ID);
								DBRef dr = new DBRef(o.get(COLLECTION_NAME).toString(), oi);
								listRef.add(dr);
							} else {
								listRef.add(o);
							}
						}
					} else {
						listRef.add(element);
					}

				}
				documentMap.put(key, listRef);
			}

			if (value instanceof Map) {

				DBObject o = save(db,(Map) documentMap.get(key));
				if (o != null) {
					if (((Map) value).containsKey(COLLECTION_NAME)) {
						ObjectId oi = (ObjectId) o.get(ID);
						DBRef dr = new DBRef((String) ((Map) value).get(COLLECTION_NAME), oi);
						documentMap.put(key, dr);
					} else {
						documentMap.put(key, o);
					}
				}

			}
		});
		if (documentMap.containsKey(ID)) {
//			Query query = new Query(Criteria.where(ID).is(documentMap.get(ID).toString()));
//			Update update = new Update();
			DBCollection collection = db.getCollection(collectionName);
			 BasicDBObject query = new BasicDBObject();
			 BasicDBObject update = new BasicDBObject();
			 ObjectId oi = new ObjectId(""+documentMap.get(ID));
			 query.put(ID,oi);
			documentMap.forEach((k, v) -> {
				if (!k.equals(ID) && !k.equals(COLLECTION_NAME)) {
					update.put(k, v);
				}
			});

			update.put("modifiedDate",new Date() );
			dBObjectResult = collection.findAndModify(query, update);
			LOG.debug("---- ---- Updated [" + collectionName + " - " + documentMap.get(ID) + "]");

		} else {
			DBCollection collection = db.getCollection(collectionName);
			documentMap.put("createdDate", new Date());
			dBObjectResult = new BasicDBObject(documentMap);
			LOG.debug("---- ----  [" + collectionName + " ");
			collection.save(dBObjectResult);
			LOG.debug("---- ---- Inserted [" + collectionName + " - " + dBObjectResult.get(ID) + "]");
		}

		return dBObjectResult;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DBObject delete( DB db ,Map<String, Object> documentMap,String []ignoreDeleteTables) {
		
		if (documentMap == null) {
			return null;
		}
		if (!documentMap.containsKey(COLLECTION_NAME)) {
			return new BasicDBObject(documentMap);
		}
		
		for (Object keyObj : documentMap.keySet()) {
			String key=keyObj.toString();
			Object value=documentMap.get(key);
			if (value instanceof List) {
//				List<Object> listRef = new ArrayList<>();
				for (Object element : (List<Object>) value) {
					if (element instanceof Map) {

						String collectionNameRef = key.substring(0, key.length() - 1);
//						DBObject o = delete(db,(Map) element,ignoreDeleteTables);
						Map o=(Map) element;
						if (o != null && ((Map) o).containsKey(COLLECTION_NAME)) {
//							ObjectId oi= new ObjectId(""+o.get(ID));//oi = (ObjectId) o.get(ID);
//							DBRef dr = new DBRef(o.get(COLLECTION_NAME).toString(), oi);
//							listRef.add(dr);
							delete(db,(Map) element,ignoreDeleteTables);
						}
						
					} else {
//						listRef.add(element);
					}

				}
//				documentMap.put(key, listRef);
			}

			if (value instanceof Map) {

				DBObject o = delete(db,(Map) documentMap.get(key),ignoreDeleteTables);
				if (o != null) {
					if (((Map) value).containsKey(COLLECTION_NAME)) {
						ObjectId oi = (ObjectId) o.get(ID);
						DBRef dr = new DBRef((String) ((Map) value).get(COLLECTION_NAME), oi);
						documentMap.put(key, dr);
					} else {
						documentMap.put(key, o);
					}
				}

			}
		}
//		});
		String collectionName = documentMap.get(COLLECTION_NAME).toString();
		if (documentMap.containsKey(ID)) {
//			Query query = new Query(Criteria.where(ID).is(documentMap.get(ID).toString()));
//			mongoTemplate.remove(query, collectionName);
			DBCollection collection = db.getCollection(collectionName);
			 BasicDBObject query = new BasicDBObject();
			 ObjectId oi = new ObjectId(""+documentMap.get(ID));
			 query.put(ID,oi);
			 Boolean canDelete=true;
			 for (String ignoreTable : ignoreDeleteTables) {
				 if(ignoreTable.equalsIgnoreCase(collectionName))
					 canDelete=false;
			}
			 if(canDelete)
			 collection.remove(query);
		}

		return null;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Map cloneDocument( DB db ,Map documentMap, Boolean withoutId) {

		for (Iterator<Map.Entry<String, Object>> it = documentMap.entrySet().iterator(); it.hasNext();) {
			Map.Entry<String, Object> entry = it.next();
			String key = entry.getKey();
			Object value = entry.getValue();

			if (value instanceof List) {
				List<Object> list = new ArrayList<>();

				for (Object element : (List<Object>) value) {
					if (element instanceof Map) {
						list.add(cloneDocument(db,(Map) element, withoutId));
					} else if (element instanceof DBRef) {
						DBRef dBRef = (DBRef) element;
						DBCollection collection = db.getCollection(dBRef.getCollectionName());
						BasicDBObject query = new BasicDBObject();
						 ObjectId oi = new ObjectId(""+dBRef.getId());
						 query.put(ID,oi);
						DBObject obj = collection.findOne(query);
						obj.put(COLLECTION_NAME, dBRef.getCollectionName());
						list.add(cloneDocument(db,obj.toMap(), withoutId));
					}

				}
				entry.setValue(list);
			} else if (value instanceof DBRef) {
				DBRef dBRef = (DBRef) value;
//				Query query = new Query(Criteria.where(ID).is(dBRef.getId()));
//				DBObject obj = mongoTemplate.findOne(query, DBObject.class, dBRef.getCollectionName());
				DBCollection collection = db.getCollection(dBRef.getCollectionName());
				 BasicDBObject query = new BasicDBObject();
				 ObjectId oi = new ObjectId(""+dBRef.getId());
				 query.put(ID,oi);
				DBObject obj = collection.findOne(query);
				
				
				obj.put(COLLECTION_NAME, dBRef.getCollectionName());
				documentMap.put(key, cloneDocument(db,obj.toMap(), withoutId));
			} else if (key.equalsIgnoreCase(ID)) {
				if (withoutId)
					it.remove();
				else {
					entry.setValue(((ObjectId) value).toHexString());

				}
			}

		}

		documentMap.put(RECORD_TYPE, "CLONE");
		return documentMap;

	}

}
