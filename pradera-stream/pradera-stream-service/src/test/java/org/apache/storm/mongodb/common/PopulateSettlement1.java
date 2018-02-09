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
		streamIdList.add("settlementStream");
		streamIdList.add("settlement1Stream");
		topologyMap.put("setStreamIds", streamIdList);
		topologyMap.put("status","ACTIVE");
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
		documentSpout.put("preExecutions", preExecutionsMap);
		documentSpout.put(Constant.Fields.INITIALIZE_SCRIPT, false);
		documentSpout.put("streamId", "settlementStream");
		
		mongoDBClient.insert(documentSpout , raw + "Spout");
		
		Bson filter = Filters.eq("name",_topologyName);
		Document _topology	=	mongoDBClient.find(filter, "topology");
		List<DBRef> spouts		=	(List)_topology.get("spouts");
		if (spouts ==null || spouts.isEmpty()) {
			spouts = new ArrayList<DBRef>();
		}
		spouts.add(new DBRef("spout", (ObjectId) documentSpout.get("_id")));
		_topology.put("bolts", spouts);
		
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
		comparatorProcess.put(Constant.Fields.NAME , Constant.StormComponent.COMPARATOR_BOLT);		
		String scriptComparator = this.getScript(folder + raw + "ComparatorReader.sql"); 
		comparatorProcess.put(Constant.Fields.SCRIPT , scriptComparator);
		comparatorProcess.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		/** Seteo de fields y parametros del ComparatorBolt**/
		comparatorProcess.put("outputFields", getFieldsComparator());
		comparatorProcess.put("paramsColumns", getParametersComparator());
		comparatorProcess.put("columnFieldsMap", getColumnFieldsComparator());
		comparatorMap.put("settlementStream", comparatorProcess);
		
		Document documentComparator = new Document(comparatorMap);
		mongoDBClient.insert(documentComparator , "bolt");
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> logicProcess = new HashMap<String, Object>();
		logicProcess.put(Constant.Fields.NAME , Constant.StormComponent.LOGIC_BOLT);		
		
		Map<String, Object> executors = new HashMap<String, Object>();
		Map<String, Object> executor = new HashMap<String, Object>();
		String scriptLogic = this.getScript(folder + raw + "LogicBolt.js"); 
		executor.put(Constant.OPERATION_TYPE , Constant.OPERATION_TYPE_TRANSFORMER );
		executor.put(Constant.IMPLEMENTATION_TYPE , Constant.IMPLEMENTATION_TRANSFORMER_TYPE_JAVASCRIPT );
		executor.put(Constant.Fields.SCRIPT , scriptLogic);
		executor.put(Constant.Fields.SCRIPT_TYPE , Constant.IMPLEMENTATION_TRANSFORMER_TYPE_JAVASCRIPT );
		int i=0;
		executors.put("executor"+i++, executor);
		logicProcess.put("settlementStream", executors);
		Document documentLogic = new Document(logicProcess);
		mongoDBClient.insert(documentLogic , "bolt");
		
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> insertNativeProcess = new HashMap<String, Object>();
		insertNativeProcess.put(Constant.Fields.NAME , Constant.StormComponent.INSERT_NATIVE_BOLT);	
		String scriptQuerieNative = this.getScript( folder + raw + "InsertNativeBolt.sql"); 
		insertNativeProcess.put(Constant.Fields.SCRIPT , scriptQuerieNative);
		insertNativeProcess.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		
		/** Seteo de fields y parametros del Spout**/
		insertNativeProcess.put("columnFieldsMapp", getColumnFieldNativesMapp());
		
		Map<String, Object> insertNativeMaps = new HashMap<String, Object>();
		insertNativeMaps.put("settlementStream", insertNativeProcess);
		Document documentInsertNative = new Document(insertNativeMaps);
		mongoDBClient.insert(documentInsertNative , "bolt");
		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> insertProcess = new HashMap<String, Object>();
		insertProcess.put(Constant.Fields.NAME , Constant.StormComponent.INSERT_CUSTOM_BOLT);	
		
		Map<String, Object> insertExecutors = new HashMap<String, Object>();
		Map<String, Object> insertExecutor = new HashMap<String, Object>();
		String insertScript = this.getScript(folder + raw + "InsertBolt.sql"); 
		insertExecutor.put(Constant.Fields.SCRIPT , insertScript);
		insertExecutor.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		int j=0;
		insertExecutors.put("executor"+j++, insertExecutor);
		insertProcess.put("settlementStream", insertExecutors);
		Document documentInsert = new Document(insertProcess);
		mongoDBClient.insert(documentInsert , "bolt");
		
		////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> updateProcess = new HashMap<String, Object>();
		updateProcess.put(Constant.Fields.NAME , Constant.StormComponent.UPDATE_CUSTOM_BOLT);		
	
		Map<String, Object> updateExecutors = new HashMap<String, Object>();
		Map<String, Object> updateExecutor = new HashMap<String, Object>();
		String updateScript = this.getScript(folder + raw + "UpdateBolt.sql"); 
		updateExecutor.put(Constant.Fields.SCRIPT , updateScript);
		updateExecutor.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		int k=0;
		updateExecutors.put("executor"+k++, updateExecutor);
		updateProcess.put("settlementStream", updateExecutors);
		Document documentUpdate = new Document(updateProcess);
		mongoDBClient.insert(documentUpdate , "bolt");
		////////////////////////////////////////////////////////////////////////////////////////////////////
		
		Map<String, Object> writerProcess = new HashMap<String, Object>();
		writerProcess.put(Constant.Fields.NAME , Constant.StormComponent.WRITER_BOLT);
		
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
		writerExecutor.put(Constant.Fields.SETTING, communication1);
		int l=0;
		writerExecutors.put("executor"+l++, writerExecutor);
		
		writerProcess.put("settlementStream", writerExecutors);
		Document documentWriter = new Document(writerProcess);
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
	
	
	@Test
	public void createExecutions() throws IOException  {
		
		MongoDBClient mongoDBClient = new MongoDBClient(null,null,"storm_config2","localhost",27017);
		
		String folder	="settlement1/";
		String raw		="settlement1";
		
		String _topologyName = "settlement1Topology";
		String _table = "SETTLEMENT";
		
		
		/**
		 *  Delete executions 
//		 */
		Bson filter = null;
		
		// started
		Map<String, Object> spoutProcess = new HashMap<String, Object>();
		spoutProcess.put(Constant.Fields.NAME , Constant.StormComponent.JDBC_SPOUT);		
		String scriptQuerieSpout = this.getScript( folder + raw + "SpoutReader.sql"); 
		spoutProcess.put(Constant.Fields.SCRIPT , scriptQuerieSpout);
		spoutProcess.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		spoutProcess.put(Constant.Fields.TIME , 5000L);
		Document documentSpout = new Document(spoutProcess);
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		Map<String, Object> comparatorProcess = new HashMap<String, Object>();
		comparatorProcess.put(Constant.Fields.NAME , Constant.StormComponent.COMPARATOR_BOLT);		
		String scriptComparator = this.getScript(folder + raw + "ComparatorReader.sql"); 
		comparatorProcess.put(Constant.Fields.SCRIPT , scriptComparator);
		comparatorProcess.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		Document documentComparator = new Document(comparatorProcess);
		
        /////////////////////////////////////////////////////////////////////////////////////////////////////
		
		Map<String, Object> logicProcess = new HashMap<String, Object>();
		logicProcess.put(Constant.Fields.NAME , Constant.StormComponent.LOGIC_BOLT);		
		
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
		Document documentLogic = new Document(logicProcess);
		
        /////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		Map<String, Object> insertNativeProcess = new HashMap<String, Object>();
		insertNativeProcess.put(Constant.Fields.NAME , Constant.StormComponent.INSERT_NATIVE_BOLT);	
		String scriptQuerieNative = this.getScript( folder + raw + "InsertNativeBolt.sql"); 
		insertNativeProcess.put(Constant.Fields.SCRIPT , scriptQuerieNative);
		insertNativeProcess.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		
		/** Seteo de fields y parametros del Spout**/
		insertNativeProcess.put("columnFieldsMapp", getColumnFieldNativesMapp());
		Document documentInsertNative = new Document(insertNativeProcess);
		
        /////////////////////////////////////////////////////////////////////////////////////////////////////

		Map<String, Object> insertProcess = new HashMap<String, Object>();
		insertProcess.put(Constant.Fields.NAME , Constant.StormComponent.INSERT_CUSTOM_BOLT);	
		
		Map<String, Object> insertExecutors = new HashMap<String, Object>();
		Map<String, Object> insertExecutor = new HashMap<String, Object>();
		String insertScript = this.getScript(folder + raw + "InsertBolt.sql"); 
		insertExecutor.put(Constant.Fields.SCRIPT , insertScript);
		insertExecutor.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		int j=0;
		insertExecutors.put("executor"+j++, insertExecutor);
		insertProcess.put("executors", insertExecutors);
		Document documentInsert = new Document(insertProcess);
		
		
		 /////////////////////////////////////////////////////////////////////////////////////////////////////
		Map<String, Object> updateProcess = new HashMap<String, Object>();
		updateProcess.put(Constant.Fields.NAME , Constant.StormComponent.UPDATE_CUSTOM_BOLT);		
	
		Map<String, Object> updateExecutors = new HashMap<String, Object>();
		Map<String, Object> updateExecutor = new HashMap<String, Object>();
		String updateScript = this.getScript(folder + raw + "UpdateBolt.sql"); 
		updateExecutor.put(Constant.Fields.SCRIPT , updateScript);
		updateExecutor.put(Constant.Fields.SCRIPT_TYPE , Constant.Fields.SQL);
		int k=0;
		updateExecutors.put("executor"+k++, updateExecutor);
		updateProcess.put("executors", updateExecutors);
		Document documentUpdate = new Document(updateProcess);
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		Map<String, Object> writerProcess = new HashMap<String, Object>();
		writerProcess.put(Constant.Fields.NAME , Constant.StormComponent.WRITER_BOLT);
		
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
		writerExecutor.put(Constant.Fields.SETTING, communication1);
		
		int l=0;
		writerExecutors.put("executor"+l++, writerExecutor);
		writerProcess.put("executors", writerExecutors);
		Document documentWriter = new Document(writerProcess);
		
		/////////////////////////////////////////////////////////////////////////////////////////////////////
		
		
		/** Seteo de fields y parametros del Spout**/
		documentSpout.put("fields", getFieldsSpout());
		documentSpout.put("parameters", getParametersSpout()); // Could have nothing value.
		mongoDBClient.insert(documentSpout , "process");
		
		/** Seteo de fields y parametros del ComparatorBolt**/
		documentComparator.put("outputFields", getFieldsComparator());
		documentComparator.put("paramsColumns", getParametersComparator());
		documentComparator.put("columnFieldsMap", getColumnFieldsComparator());
		
		mongoDBClient.insert(documentComparator , "process");
		mongoDBClient.insert(documentLogic , "process");
		mongoDBClient.insert(documentInsertNative , "process");
		mongoDBClient.insert(documentInsert , "process");
		mongoDBClient.insert(documentUpdate , "process");
		mongoDBClient.insert(documentWriter , "process");		
		
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
		List<DBRef> processes = new ArrayList<DBRef>();
		processes.add(new DBRef("process", (ObjectId) documentSpout.get("_id")));
		processes.add(new DBRef("process", (ObjectId) documentComparator.get("_id")));
		processes.add(new DBRef("process", (ObjectId) documentInsertNative.get("_id")));
		processes.add(new DBRef("process", (ObjectId) documentLogic.get("_id")));
		processes.add(new DBRef("process", (ObjectId) documentInsert.get("_id")));
		processes.add(new DBRef("process", (ObjectId) documentUpdate.get("_id")));
		processes.add(new DBRef("process", (ObjectId) documentWriter.get("_id")));
		////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

		
		List<DBRef> communications = new ArrayList<DBRef>();
		filter = Filters.eq("origin", "source");
		Document source	=	mongoDBClient.find(filter, "communication");
		communications.add(new DBRef("communication", (ObjectId) source.get("_id")));
		
		filter = Filters.eq("origin", "target");
		Document target	=	mongoDBClient.find(filter, "communication");
		communications.add(new DBRef("communication", (ObjectId) target.get("_id")));
		
		Map<String, Object> topologyMap = new HashMap<String, Object>();
		
		topologyMap.put(Constant.Fields.NAME, _topologyName);
		topologyMap.put(Constant.Fields.TABLE_TARGET, _table);
		topologyMap.put(Constant.Fields.PROCESSES, processes);
		topologyMap.put(Constant.Fields.COMMUNICATIONS, communications);
		topologyMap.put(Constant.Fields.COMMUNICATIONS, communications);
		topologyMap.put(Constant.Fields.PROCESS_EXECUTING, false);
		topologyMap.put(Constant.Fields.INITIALIZE_SCRIPT, false);
		
		
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
		topologyMap.put("preExecutions", preExecutionsMap);
		
		Document documentTopology = new Document(topologyMap );
		mongoDBClient.insert(documentTopology , "topology");

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
}
