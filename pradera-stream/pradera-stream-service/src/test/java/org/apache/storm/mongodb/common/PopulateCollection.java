package org.apache.storm.mongodb.common;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.junit.Before;

import com.google.common.collect.Maps;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.model.Filters;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"unchecked", "rawtypes"})
public abstract class PopulateCollection {
	
	protected	String server = "192.168.1.6";
	protected	int port = 27017;
	protected	String database = "storm_config";
	protected	String userName = "storm";
	protected	String password = "storm123";

	public	MongoClient mongoClient ;
	
	/**
	 *  target db
	 */
	public transient ConnectionProvider connectionProvider;

	@Before
	public void establishConnection() {
		
		MongoCredential credential = MongoCredential.createCredential(userName, database, password.toCharArray());
		this.mongoClient = new MongoClient(new ServerAddress(server, port), Arrays.asList(credential));
		//this.mongoClient = new MongoClient(new ServerAddress(server, port), new MongoClientOptions.Builder().build());;
	}
	

	
	public abstract void createExecutions() throws IOException  ;
	
	//@Test
	public void finding() {
		
		MongoDBClient mongoDBClient = new MongoDBClient(null,null,"storm_config","localhost",27017);

		Bson filter = Filters.eq("name", "settlementTopology");
		Document source	=	mongoDBClient.find(filter, "topology");
		
		filter = Filters.regex("name", "_Settlement");
		mongoDBClient.delete(filter, "process");
		System.out.println("");
	}
	
	public String getScript(String scriptName) throws IOException{
		StringBuilder result = new StringBuilder("");
		ClassLoader classLoader = getClass().getClassLoader();
		File file = new File(classLoader.getResource("scripts/"+scriptName).getFile());
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
	
	//@Test
	public void conectionPostgresqlTest() {
		Map map = Maps.newHashMap();
	    map.put("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
	    map.put("dataSource.url", "jdbc:postgresql://localhost:5432/postgres");
	    map.put("dataSource.user", "postgres");
	    map.put("dataSource.password", "admin");
		this.connectionProvider = new HikariCPConnectionProvider(map);
        connectionProvider.prepare();
        connectionProvider.getConnection();
		System.out.println("got connection");

		
	}
	
	
	protected abstract List<Map> getFieldsSpout() ;
	
	protected abstract List<Map> getFieldsComparator();

	protected abstract List<Map> getParametersSpout() ;
		
	
	protected abstract List<Map> getParametersComparator();
	
	 protected abstract List<Map> getColumnFieldNativesMapp() ;
	 
	 protected abstract List<Map> getColumnFieldsComparator() ;
}
