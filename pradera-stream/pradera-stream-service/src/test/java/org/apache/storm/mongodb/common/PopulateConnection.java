package org.apache.storm.mongodb.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;

import com.pradera.stream.constant.Constant;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public class PopulateConnection  {
	
	@Test
	public void createCommunication() {
		
		MongoDBClient mongoDBClient = new MongoDBClient(null,null,"storm_config","localhost",27017);
		Document document = new Document();

		document.put(Constant.Fields.CONNECTION_TYPE, Constant.Fields.SQL);
		document.put(Constant.Fields.ORIGIN, Constant.Fields.SOURCE);
		document.put(Constant.Fields.NAME, "dbWari");
		
		Map setting = new HashMap<String, String>();
		setting.put(Constant.Fields.DRIVER, "oracle.jdbc.pool.OracleDataSource" );
		setting.put(Constant.Fields.URL, "jdbc:oracle:thin:@52.39.34.204:1521:xe");
		setting.put(Constant.Fields.USER, "CSDCORE_BVL");
		setting.put(Constant.Fields.PASSWORD, "CSDCORE_BVL" );
		document.put(Constant.Fields.SETTING, setting);
		
		mongoDBClient.setCollection( mongoDBClient.getCollecion("communication"));
		
		List<Document> executions = new ArrayList<Document>();
		executions.add(document);
		mongoDBClient.insert(executions, true);
		
		Assert.assertNotNull(executions.get(0).get("_id"));
	}
	
	@Test
	public void createCommunication2() {
		
		MongoDBClient mongoDBClient = new MongoDBClient(null,null,"storm_config","localhost",27017);
		Document document = new Document();

		document.put(Constant.Fields.CONNECTION_TYPE, Constant.Fields.SQL);
		document.put(Constant.Fields.ORIGIN, Constant.Fields.TARGET);
		document.put(Constant.Fields.NAME, "dbKpi");
		
		Map setting = new HashMap<String, String>();
		setting.put(Constant.Fields.DRIVER, "org.postgresql.ds.PGSimpleDataSource" );
		setting.put(Constant.Fields.URL, "jdbc:postgresql://localhost:5432/postgres");
		setting.put(Constant.Fields.USER, "postgres");
		setting.put(Constant.Fields.PASSWORD, "@dmin123" );
		document.put(Constant.Fields.SETTING, setting);
		
		mongoDBClient.setCollection( mongoDBClient.getCollecion("communication"));
		List<Document> executions = new ArrayList<Document>();
		executions.add(document);
		mongoDBClient.insert(executions, true);
		
		Assert.assertNotNull(executions.get(0).get("_id"));
	}
}
