package com.pradera.stream.constant;

/**
 * Created by jalor
 */
public class Constant {
	
	public static class StormComponent {
		
		public static final String NOTHING 				= "NOTHING";
		public static final String COMPARATOR_BOLT  	= "comparatorBolt";
		public static final String LOGIC_BOLT			= "logicBolt";
		public static final String JDBC_SPOUT			= "jdbcSpout";
		public static final String INSERT_NATIVE_BOLT	= "insertNativeBolt";
		public static final String UPDATE_CUSTOM_BOLT	= "updateCustomBolt";
		public static final String INSERT_CUSTOM_BOLT 	= "insertCustomBolt";
		public static final String WRITER_BOLT 			= "writerBolt";
	}
	
	public static class Fields {
		
		public static final String DATASOURCES 			= "dataSources";
		public static final String DATASOURCE_NAME 		= "dataSourceName";
		public static final String DATASOURCE_T_NAME 	= "dataSourceTargetName";
		public static final String NAME 				= "name";
		public static final String TABLE_TARGET 		= "tableTarget";
		public static final String CONNECTION_TYPE 		= "connectionType";
		public static final String SCRIPT_TYPE 			= "scriptType";
		public static final String OPERATION_TYPE 		= "operationType";
		public static final String SQL 					= "sql";
		public static final String WS 					= "ws";
		public static final String JS 					= "js";
		public static final String SETTING 				= "setting";
		public static final String ORIGIN 				= "origin";
		public static final String SOURCE 				= "source";
		public static final String TARGET 				= "target";
		public static final String DRIVER 				= "dataSourceClassName";
		public static final String URL 					= "url";
		public static final String USER 				= "user";
		public static final String PASSWORD 			= "password";
		public static final String DATABASENAME 		= "databaseName";
		public static final String SCHEMA 				= "schema";
		public static final String MINIMUMIDLE 			= "minimumIdle";
		public static final String MAXIMUM_POOL_SIZE 	= "maximumPoolSize";
		public static final String CONNECION_TIMEOUT 	= "connectionTimeout";
		public static final String SCRIPT 			    = "script";
		public static final String EXECUTIONS 			= "executions";
		public static final String PROCESSES 			= "process";
		public static final String SPOUTS 				= "spouts";
		public static final String BOLTS 				= "bolts";
		public static final String INITIALIZE_SCRIPT 	= "initializeScript";
		public static final String PROCESS_EXECUTING 	= "processExecuting";
		public static final String COMMUNICATIONS 		= "communications";
		public static final String TIME 				= "time";
		public static final String TIMEOUTSEC			= "timeoutSec";
	}
	
     public static class SettingMongo {

    	public static final String USER 				= "USER";
		public static final String PASSWORD 			= "PASSWORD";
		public static final String DB 					= "DB";
		public static final String HOST 				= "HOST";
		public static final String PORT 				= "PORT";
		
	}
	
    public final static String PAYLOAD = "payload";
 	public final static String MAPPER_CONFIG = "mapperConfig";
 	public final static String ID = "id";
 	public final static String ID_GENERATED = "idGenerated";
 	public final static String _ID = "_id";
 	public final static String VALUE_ORIGINAL = "valueOriginal";
 	public final static String NAME = "name";

 	
 	public final static String TYPE = "type";
 	public final static String IMPLEMENTATION = "implementation";
 	public final static String IMPLEMENTATIONS = "implementations";
 	
 	
 	public final static String STATUS_ACTIVE = "active";
 	public final static String STATUS_INACTIVE = "inactive";
 	
 	public final static String VALUE = "VALUE";
 	public final static String RESULT_SET = "RESULT_SET";
 	
 	
 	public final static String EXECUTOR_RESULT_TYPE = "resultType";
 	public final static String EXECUTOR_SCRIPT = "script";
 	
 	public final static String OPERATION_TYPE="OPERATION_TYPE";
 	public final static String IMPLEMENTATION_TYPE="IMPLEMENTATION_TYPE";

 	public final static String OPERATION_TYPE_ENRICHER = "ENRICHER";
 	public final static String OPERATION_TYPE_TRANSFORMER = "TRANSFORMER";
 	public final static String OPERATION_TYPE_WRITER = "WRITER";
 	
 	public final static String IMPLEMENTATION_ENRICHER_TYPE_SQL = "SQL";
 	public final static String IMPLEMENTATION_ENRICHER_TYPE_NOSQL = "NOSQL";
 	public final static String IMPLEMENTATION_ENRICHER_TYPE_WS = "WS";
 	public final static String IMPLEMENTATION_ENRICHER_TYPE_QUEUE = "QUEQE";
 	public final static String IMPLEMENTATION_TRANSFORMER_TYPE_JAVASCRIPT = "JS";
 	public final static String IMPLEMENTATION_TRANSFORMER_TYPE_GROOVY = "GROOVY";
 	public final static String IMPLEMENTATION_WRITER_TYPE_SQL = "SQL";
 	public final static String IMPLEMENTATION_WRITER_TYPE_NOSQL = "NOSQL";
 	public final static String IMPLEMENTATION_WRITER_TYPE_WS = "WS";
 	public final static String IMPLEMENTATION_WRITER_TYPE_QUEUE = "QUEUE";

    public final static String JAVASCRIPT_FUNCTION_NAME_DEFAULT="process";
 	public static final String ENGINE_NASHORN="nashorn";
 	
 	
 	public static final String ACTION_RESULT 	= "ACTION_RESULT";
 	public static final String NEXT_BOLT 		= "NEXT_BOLT";
 	public static final String NOTHING 			= "NOTHING";
 	public static final String UPSERT_BOLT 		= "UPSERT_BOLT";
 	
 	public static final String UPDATE_BOLT 		= "UPDATE_BOLT";
 	public static final String INSERT_BOLT 		= "INSERT_BOLT";
 	public static final String COMPARATOR_BOLT  = "COMPARATOR_BOLT";
 	public static final String LOGIC_BOLT		= "LOGIC_BOLT";
 	public static final String JDBC_SPOUT		= "JDBC_SPOUT";
}
