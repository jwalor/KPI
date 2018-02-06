package com.pradera.stream.service;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.JSONObject;
import org.junit.Test;

import com.pradera.stream.util.VelocityUtils;

//import com.mongodb.BasicDBObject;
//import com.mongodb.DB;
//import com.mongodb.DBCollection;
//import com.mongodb.DBObject;
//import com.mongodb.Mongo;


@SuppressWarnings({ "deprecation", "unchecked" ,"rawtypes"})
public class RunConfig {
	
	String []ignoreDeleteTables= {"connection"};
	
	@Test
	public void runConfig() throws IOException, Exception  {
		
		String pathBase	="scripts/";
		String 	sourceName	="settlementUpdateBolt.vm";
		Map data		=	new HashMap();
		data.put("concept", "12345");
		String JsonMap 	= 	VelocityUtils.loadTemplateVM(getSourceString(pathBase, sourceName),data);
		
		System.out.println(JsonMap);
//		try {
//		String pathBase="statereporting/washington";
//		String sourceName="mapper.json.vm";
//		Map data=new HashMap();
//		String JsonMap=VelocityUtil.loadTemplateVM(getSourceString(pathBase, sourceName), data,data);
//		Map documentMap =new JSONObject(JsonMap).toMap();
//		Mongo mongo;
////			mongo = new Mongo("ndc-dv-mongo1.qsidc.com", 27017);
//			mongo = new Mongo("127.0.0.1", 27017);
//		
//		  DB db = mongo.getDB("qsi_srp");
////		  boolean auth = db.authenticate("qsi_srp", "Admin456".toCharArray());
////			if (auth) {
//				DBCollection collection = db.getCollection("mapper");
//				GenericMongoDAO genericMongoDAO = new GenericMongoDAOImpl();
//				BasicDBObject whereQuery = new BasicDBObject();
//				whereQuery.put("name", "StateReporting");
//				DBObject mapperDBwhereQuery=collection.findOne(whereQuery);
//				if(mapperDBwhereQuery!=null) {
//					Map mapperDB= genericMongoDAO.cloneDocument(db, mapperDBwhereQuery.toMap(), false);
//					genericMongoDAO.delete(db, mapperDB,ignoreDeleteTables);	
//				}
//				genericMongoDAO.save(db, documentMap);
////			}
//		
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	
	public String getSourceString(String pathBase,String sourceName) throws IOException{
		StringBuilder result = new StringBuilder("");
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource(pathBase+"/"+sourceName).getFile());
		try (Scanner scanner = new Scanner(file)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				result.append(line).append("\n");
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result.toString();
	}
	
	
}
