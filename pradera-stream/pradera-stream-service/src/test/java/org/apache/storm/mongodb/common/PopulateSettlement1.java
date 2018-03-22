package org.apache.storm.mongodb.common;

import java.io.IOException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import org.junit.Test;

import com.mongodb.DBRef;
import com.mongodb.client.model.Filters;
import com.pradera.stream.constant.Constant;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"unchecked", "rawtypes", "serial"})
public class PopulateSettlement1 extends PopulateCollection {
	
	
	@Test
	public void createTopology() {
		
		MongoDBClient mongoDBClient = new MongoDBClient(null,null,"storm_config2","localhost",27017);
		String _topologyName = "KPITopology";
		Map<String, Object> topologyMap = new HashMap<String, Object>();
	
		topologyMap.put(Constant.Fields.NAME, _topologyName);
		List streamIdList = new ArrayList<>();
		streamIdList.add("TB_GRAFICOS");
		streamIdList.add("TB_GRAFICOS2");
		topologyMap.put("streamIds", streamIdList);
		topologyMap.put("status","ACTIVE");
		
		Map numTask = new HashMap<String, Integer>();
		numTask.put("USER_SPOUT",1);
		numTask.put("LOOKUP_BOLT", 10);
		numTask.put("CONTROL1", 10);
		numTask.put("PERSISTANCE_BOLT", 10);
		numTask.put("LOGIC_BOLT", 10);
		numTask.put("CONTROL2", 10);
		numTask.put("UPDATE_BOLT", 10);
		numTask.put("INSERT_BOLT", 10);
		numTask.put("RES_API_BOLT1", 10);
		numTask.put("RES_API_BOLT2", 10);
		topologyMap.put("numTask", numTask);
		
		Map hazelCast = new HashMap<String, Integer>();
		hazelCast.put("username", "dev");
		hazelCast.put("credential", "dev-pass");
		topologyMap.put("hazelCast", hazelCast);
		
		topologyMap.put(Constant.Fields.DATASOURCE_NAME, "dbWari");
		topologyMap.put(Constant.Fields.DATASOURCE_T_NAME, "dbKpi");

		Document documentTopology = new Document(topologyMap );
		
		mongoDBClient.insert(documentTopology , "topology");
	}
	
	@Test
	public void createSettlementSpout() throws IOException {
		
		MongoDBClient mongoDBClient = new MongoDBClient(null,null,"storm_config2","localhost",27017);
		
		String _topologyName = "KPITopology";
		
		String folder	="settlement/";
		String raw		="settlement";
		
		Map<String, Object> spoutProcess = new HashMap<String, Object>();
		spoutProcess.put(Constant.Fields.NAME , Constant.StormComponent.JDBC_SPOUT);		
		String scriptQuerieSpout = this.getScript( folder + raw + "SpoutReader.sql"); 
		spoutProcess.put(Constant.Fields.SCRIPT , scriptQuerieSpout);
		spoutProcess.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		spoutProcess.put(Constant.Fields.TIME , 5000L);
		spoutProcess.put(Constant.Fields.TIMEOUTSEC, Integer.parseInt("30"));
		spoutProcess.put(Constant.Fields.DATASOURCE_NAME, "dbWari");
		
		Document documentSpout = new Document(spoutProcess);
		/** Seteo de fields y parametros del Spout**/
		documentSpout.put("fields", getFieldsSpout());
		documentSpout.put("parameters", getParametersSpout()); // Could have nothing value.
		
		///////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> preExecutionsMap = new HashMap<String, Object>();
		
		String beforeFolder = "before/";
		String script1 = this.getScript(folder + beforeFolder + "script1.sql");
		preExecutionsMap.put(Constant.Fields.SCRIPT+1, script1 );		
		String script2 = this.getScript(folder + beforeFolder + "script2.sql");
		preExecutionsMap.put(Constant.Fields.SCRIPT+2, script2);
		String script3 = this.getScript(folder + beforeFolder + "script3.sql");
		preExecutionsMap.put(Constant.Fields.SCRIPT+3, script3);
		String script4 = this.getScript(folder + beforeFolder + "script4.sql");
		preExecutionsMap.put(Constant.Fields.SCRIPT+4, script4);
		String script5 = this.getScript(folder + beforeFolder + "script5.sql");
		preExecutionsMap.put(Constant.Fields.SCRIPT+5, script5);
		
		documentSpout.put(Constant.Fields.DATASOURCE_T_NAME, "dbKpi");
		documentSpout.put("preExecutions", preExecutionsMap);
		documentSpout.put(Constant.Fields.INITIALIZE_SCRIPT, false);
		documentSpout.put("streamId", "TB_GRAFICOS");
		
		mongoDBClient.insert(documentSpout , "spout");
		
		Bson filter = Filters.eq("name",_topologyName);
		Document _topology	=	mongoDBClient.find(filter, "topology");
		List<DBRef> spouts		=	(List)_topology.get("spouts");
		if (spouts ==null || spouts.isEmpty()) {
			spouts = new ArrayList<DBRef>();
		}
		spouts.add(new DBRef("spout", (ObjectId) documentSpout.get("_id")));
		_topology.put("spouts", spouts);
		
		mongoDBClient.update(filter, _topology, true, false);
	}
	
	@Test
	public void createBolts() throws IOException {
		
		MongoDBClient mongoDBClient = new MongoDBClient(null,null,"storm_config2","localhost",27017);
		String _topologyName = "KPITopology";
		
		String folder	="settlement/";
		String raw		="settlement";
		
		Bson filter = Filters.eq("name",_topologyName);
		Document _topology	=	mongoDBClient.find(filter, "topology");
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> comparatorMap = new HashMap<String, Object>();
		
		Map<String, Object> comparatorProcess = new HashMap<String, Object>();
		String scriptComparator = this.getScript(folder + raw + "ComparatorReader.sql"); 
		comparatorProcess.put(Constant.Fields.SCRIPT , scriptComparator);
		comparatorProcess.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		comparatorProcess.put("outputFields", getFieldsComparator());
		comparatorProcess.put("paramsColumns", getParametersComparator());
		comparatorProcess.put("columnFieldsMap", getColumnFieldsComparator());
		comparatorProcess.put("streamName", "TB_GRAFICOS");
		comparatorProcess.put(Constant.Fields.DATASOURCE_NAME, "dbKpi");
		
		List streamList = new ArrayList<>();
		streamList.add(comparatorProcess);
		comparatorMap.put("streams", streamList);
		
		Document documentComparator = new Document(comparatorMap);
		documentComparator.put(Constant.Fields.NAME , Constant.StormComponent.COMPARATOR_BOLT);		
		mongoDBClient.insert(documentComparator , "bolt");
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> logicProcess = new HashMap<String, Object>();
		
		Map<String, Object> executors = new HashMap<String, Object>();
		Map<String, Object> executor = new HashMap<String, Object>();
		String scriptLogic = this.getScript(folder + raw + "LogicBolt.js"); 
		executor.put(Constant.OPERATION_TYPE , Constant.OPERATION_TYPE_TRANSFORMER );
		executor.put(Constant.IMPLEMENTATION_TYPE , Constant.IMPLEMENTATION_TRANSFORMER_TYPE_JAVASCRIPT );
		executor.put(Constant.Fields.SCRIPT , scriptLogic);
		executor.put(Constant.Fields.SCRIPT_TYPE , Constant.IMPLEMENTATION_TRANSFORMER_TYPE_JAVASCRIPT );
		int i=0;
		executors.put("executor"+i++, executor);
		logicProcess.put("executors", executors);
		logicProcess.put("streamName", "TB_GRAFICOS");
		
		streamList = new ArrayList<>();
		streamList.add(logicProcess);
		
		Map<String, Object> logicMaps = new HashMap<String, Object>();
		logicMaps.put("streams", streamList);
		Document documentLogic = new Document(logicMaps);
		documentLogic.put(Constant.Fields.NAME , Constant.StormComponent.LOGIC_BOLT);
		mongoDBClient.insert(documentLogic , "bolt");
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> insertNativeProcess = new HashMap<String, Object>();
		String scriptQuerieNative = this.getScript( folder + raw + "InsertNativeBolt.sql"); 
		insertNativeProcess.put(Constant.Fields.SCRIPT , scriptQuerieNative);
		insertNativeProcess.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		insertNativeProcess.put("columnFieldsMap", getColumnFieldNativesMapp());
		insertNativeProcess.put("streamName", "TB_GRAFICOS");
		insertNativeProcess.put(Constant.Fields.DATASOURCE_NAME, "dbKpi");
		
		Map<String, Object> insertNativeMaps = new HashMap<String, Object>();
		streamList = new ArrayList<>();
		streamList.add(insertNativeProcess);
		insertNativeMaps.put("streams", streamList);
		Document documentInsertNative = new Document(insertNativeMaps);
		documentInsertNative.put(Constant.Fields.NAME , Constant.StormComponent.INSERT_NATIVE_BOLT);	
		mongoDBClient.insert(documentInsertNative , "bolt");
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> insertProcess = new HashMap<String, Object>();
		
		Map<String, Object> insertExecutors = new HashMap<String, Object>();
		Map<String, Object> insertExecutor = new HashMap<String, Object>();
		String insertScript = this.getScript(folder + raw + "InsertBolt.sql"); 
		insertExecutor.put(Constant.Fields.SCRIPT , insertScript);
		insertExecutor.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		int j=0;
		insertExecutors.put("executor"+j++, insertExecutor);
		insertProcess.put("executors", insertExecutors);
		insertProcess.put("streamName", "TB_GRAFICOS");
		insertProcess.put(Constant.Fields.DATASOURCE_NAME, "dbKpi");

		
		streamList = new ArrayList<>();
		streamList.add(insertProcess);
		
		Map<String, Object> insertMaps = new HashMap<String, Object>();
		insertMaps.put("streams", streamList);
		Document documentInsert = new Document(insertMaps);
		documentInsert.put(Constant.Fields.NAME , Constant.StormComponent.INSERT_CUSTOM_BOLT);	
		mongoDBClient.insert(documentInsert , "bolt");
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> updateProcess = new HashMap<String, Object>();
	
		Map<String, Object> updateExecutors = new HashMap<String, Object>();
		Map<String, Object> updateExecutor = new HashMap<String, Object>();
		String updateScript = this.getScript(folder + raw + "UpdateBolt.sql"); 
		updateExecutor.put(Constant.Fields.SCRIPT , updateScript);
		updateExecutor.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		int k=0;
		updateExecutors.put("executor"+k++, updateExecutor);
		updateProcess.put("executors", updateExecutors);
		updateProcess.put("streamName", "TB_GRAFICOS");
		updateProcess.put(Constant.Fields.DATASOURCE_NAME, "dbKpi");

		streamList = new ArrayList<>();
		streamList.add(updateProcess);
		
		Map<String, Object> updateMaps = new HashMap<String, Object>();
		updateMaps.put("streams", streamList);
		Document documentUpdate = new Document(updateMaps);
		documentUpdate.put(Constant.Fields.NAME , Constant.StormComponent.UPDATE_CUSTOM_BOLT);
		mongoDBClient.insert(documentUpdate , "bolt");
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		Map<String, Object> writerProcess = new HashMap<String, Object>();
		Map<String, Object> writerExecutors = new HashMap<String, Object>();
		Map<String, Object> writerExecutor = new HashMap<String, Object>();
		writerExecutor.put(Constant.OPERATION_TYPE , Constant.OPERATION_TYPE_WRITER );
		writerExecutor.put(Constant.IMPLEMENTATION_TYPE , Constant.IMPLEMENTATION_WRITER_TYPE_WS);
		
		/**
		 *  parametros de conexion donde va a escribir
		 */
		Map communication1 = new HashMap<String, String>();		
		communication1.put(Constant.Fields.URL, "http://localhost:8080");
		communication1.put("uri", "/process/streaming/");
		communication1.put("method", "post");
		communication1.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
		communication1.put("Accept-Language", "en-US,en;q=0.5");
		communication1.put("Content-Type", "application/json");
		
		Map body = new HashMap<String, String>();
		body.put("tableTarget", "value1");
		body.put("streamId", "value2");
		communication1.put("body",body);
		writerExecutor.put(Constant.Fields.SETTING, communication1);
		
		int l=0;
		writerExecutors.put("executor"+l++, writerExecutor);
		writerProcess.put("executors", writerExecutors);
		Document documentWriter = new Document(writerProcess);
		
		documentWriter.put(Constant.Fields.NAME , Constant.StormComponent.WRITER_BOLT);
		mongoDBClient.insert(documentWriter , "bolt");
		
		
		_topology	=	mongoDBClient.find(filter, "topology");
		Object objectList		=	_topology.get("bolts");
		
		if (objectList == null) {
			objectList = new ArrayList<DBRef>();
		}
		List<DBRef> bolts		=	(List)objectList;
		bolts.add(new DBRef("bolt", (ObjectId) documentComparator.get("_id")));
		bolts.add(new DBRef("bolt", (ObjectId) documentLogic.get("_id")));
		bolts.add(new DBRef("bolt", (ObjectId) documentInsertNative.get("_id")));
		bolts.add(new DBRef("bolt", (ObjectId) documentInsert.get("_id")));
		bolts.add(new DBRef("bolt", (ObjectId) documentUpdate.get("_id")));
		bolts.add(new DBRef("bolt", (ObjectId) documentWriter.get("_id")));
		_topology.put("bolts", bolts);
		
		mongoDBClient.update(filter, _topology, true, false);
		
	}
	
	
	
	protected List<Map> getFieldsSpout() {
		
		final Map field8 = new HashMap<String, Object>();
		field8.put("name", "TOTAL"); 
		field8.put("type", Types.NUMERIC);
		
		final Map field9 = new HashMap<String, Object>();
		field9.put("name", "CODE"); 
		field9.put("type", Types.NUMERIC);
		
		final Map field1 = new HashMap<String, Object>();
		field1.put("name", "ID_HOLDER_ACCOUNT_OPERATION_PK"); 
		field1.put("type", Types.NUMERIC);
		
		final Map field2 = new HashMap<String, Object>();
		field2.put("name", "ID_MECHANISM_OPERATION_FK"); 
		field2.put("type", Types.NUMERIC);
		
		final Map field3 = new HashMap<String, Object>();
		field3.put("name", "ID_HOLDER_ACCOUNT_FK"); 
		field3.put("type", Types.NUMERIC);
		
		final Map field4 = new HashMap<String, Object>();
		field4.put("name", "OPERATION_PART"); 
		field4.put("type", Types.NUMERIC);
		
		final Map field5 = new HashMap<String, Object>();
		field5.put("name", "SETTLEMENT_AMOUNT"); 
		field5.put("type", Types.NUMERIC);
		
		final Map field6 = new HashMap<String, Object>();
		field6.put("name", "SETTLED_QUANTITY"); 
		field6.put("type", Types.NUMERIC);
		
		final Map field7 = new HashMap<String, Object>();
		field7.put("name", "LAST_MODIFY_DATE"); 
		field7.put("type", Types.TIMESTAMP);
		
		return new ArrayList<Map>()
				{{add(field8);
				  add(field9);
				  add(field1);
				  add(field2);
				  add(field3);
				  add(field4);
				  add(field5);
				  add(field6);
				  add(field7);
				}};
	}
	
	
	protected List<Map> getFieldsComparator() {
		
		final Map field8 = new HashMap<String, Object>();
		field8.put("name", "TOTAL"); 
		field8.put("type", Types.NUMERIC);
		
		final Map field9 = new HashMap<String, Object>();
		field9.put("name", "CODE"); 
		field9.put("type", Types.NUMERIC);
		
		final Map field1 = new HashMap<String, Object>();
		field1.put("name", "ID_HOLDER_ACCOUNT_OPERATION_PK"); 
		field1.put("type", Types.NUMERIC);
		
		final Map field2 = new HashMap<String, Object>();
		field2.put("name", "ID_MECHANISM_OPERATION_FK"); 
		field2.put("type", Types.NUMERIC);
		
		final Map field3 = new HashMap<String, Object>();
		field3.put("name", "ID_HOLDER_ACCOUNT_FK"); 
		field3.put("type", Types.NUMERIC);
		
		final Map field4 = new HashMap<String, Object>();
		field4.put("name", "OPERATION_PART"); 
		field4.put("type", Types.NUMERIC);
		
		final Map field5 = new HashMap<String, Object>();
		field5.put("name", "SETTLEMENT_AMOUNT"); 
		field5.put("type", Types.NUMERIC);
		
		final Map field6 = new HashMap<String, Object>();
		field6.put("name", "SETTLED_QUANTITY"); 
		field6.put("type", Types.NUMERIC);
		
		final Map field7 = new HashMap<String, Object>();
		field7.put("name", "LAST_MODIFY_DATE"); 
		field7.put("type", Types.TIMESTAMP);
				
		return new ArrayList<Map>()
				{{add(field8);
				  add(field9);
				  add(field1);
				  add(field2);
				  add(field3);
				  add(field4);
				  add(field5);
				  add(field6);
				  add(field7);
				}};
	}

	protected List<Map> getParametersSpout() {
		final Map parameter1 = new HashMap<String, Object>();
		parameter1.put("name", "YEAR"); 
		parameter1.put("value", "2015");
		parameter1.put("type", Types.VARCHAR);		
		
	   return new ArrayList<Map>(){{add(parameter1);}};
	}
		
	
	protected List<Map> getParametersComparator() {
		final Map parameter1 = new HashMap<String, Object>();
		parameter1.put("name", "ID_HOLDER_ACCOUNT_OPERATION_PK"); 
		parameter1.put("type", Types.NUMERIC);		
		
	   return new ArrayList<Map>(){{add(parameter1);}};
	}
	
	 protected List<Map> getColumnFieldNativesMapp() {

		 final Map columnField8 = new HashMap<String, Object>();
		    columnField8.put("name", "TOTAL"); 
		    columnField8.put("type", Types.NUMERIC);

			final Map columnField9 = new HashMap<String, Object>();
			columnField9.put("name", "CODE"); 
			columnField9.put("type", Types.NUMERIC);
		 	
			final Map columnField1 = new HashMap<String, Object>();
			columnField1.put("name", "ID_HOLDER_ACCOUNT_OPERATION_PK"); 
			columnField1.put("type", Types.NUMERIC);

			final Map columnField2 = new HashMap<String, Object>();
			columnField2.put("name", "SETTLEMENT_AMOUNT"); 
			columnField2.put("type", Types.NUMERIC);
			
			final Map columnField3 = new HashMap<String, Object>();
			columnField3.put("name", "SETTLED_QUANTITY"); 
			columnField3.put("type", Types.NUMERIC);
			
			return new ArrayList<Map>()
			{{add(columnField8);
			  add(columnField9);
			  add(columnField1);
			  add(columnField2);
			  add(columnField3);
			}};
	 }
	 
	 
	 protected List<Map> getColumnFieldsComparator() {
			
		    final Map columnField8 = new HashMap<String, Object>();
		    columnField8.put("name", "TOTAL"); 
		    columnField8.put("value", 11462L);
		    columnField8.put("type", Types.NUMERIC);

			final Map columnField9 = new HashMap<String, Object>();
			columnField9.put("name", "CODE"); 
			columnField9.put("value", 11462L);
			columnField9.put("type", Types.NUMERIC);
		 	
			final Map columnField1 = new HashMap<String, Object>();
			columnField1.put("name", "ID_HOLDER_ACCOUNT_OPERATION_PK"); 
			columnField1.put("value", 11462L);
			columnField1.put("type", Types.NUMERIC);

			final Map columnField2 = new HashMap<String, Object>();
			columnField2.put("name", "ID_MECHANISM_OPERATION_FK"); 
			columnField2.put("value", 11462L);
			columnField2.put("type", Types.NUMERIC);
			
			final Map columnField3 = new HashMap<String, Object>();
			columnField3.put("name", "ID_HOLDER_ACCOUNT_FK"); 
			columnField3.put("value", 11462L);
			columnField3.put("type", Types.NUMERIC);
			
			final Map columnField4 = new HashMap<String, Object>();
			columnField4.put("name", "OPERATION_PART"); 
			columnField4.put("value", 11462L);
			columnField4.put("type", Types.NUMERIC);
			
			final Map columnField5 = new HashMap<String, Object>();
			columnField5.put("name", "SETTLEMENT_AMOUNT"); 
			columnField5.put("value", 11462L);
			columnField5.put("type", Types.NUMERIC);
			columnField5.put("shouldCompare", Boolean.TRUE);
			
			final Map columnField6 = new HashMap<String, Object>();
			columnField6.put("name", "SETTLED_QUANTITY"); 
			columnField6.put("value", 11462L);
			columnField6.put("type", Types.NUMERIC);
			
			final Map columnField7 = new HashMap<String, Object>();
			columnField7.put("name", "LAST_MODIFY_DATE"); 
			columnField7.put("value", 11462L);
			columnField7.put("type", Types.TIMESTAMP);
			
			final Map columnField10 = new HashMap<String, Object>();
			columnField10.put("name", "ID"); 
			columnField10.put("type", Types.NUMERIC);

			return new ArrayList<Map>()
			{{add(columnField8);
			  add(columnField9);
			  add(columnField1);
			  add(columnField2);
			  add(columnField3);
			  add(columnField4);
			  add(columnField5);
			  add(columnField6);
			  add(columnField7);
			  add(columnField10);
			}};
			
		}

	@Override
	public void createExecutions() throws IOException {
		// TODO Auto-generated method stub
		
	}
}
