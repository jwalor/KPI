package org.apache.storm.topologies;

import java.sql.Connection;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.storm.Config;
import org.apache.storm.LocalCluster;
import org.apache.storm.generated.StormTopology;
import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.mapper.JdbcLookupMapper;
import org.apache.storm.jdbc.mapper.JdbcMapper;
import org.apache.storm.jdbc.mapper.SimpleJdbcLookupMapper;
import org.apache.storm.jdbc.spout.KpiSpout;
import org.apache.storm.mongodb.common.MongoDBClient;
import org.apache.storm.tuple.Fields;
import org.bson.Document;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.zaxxer.hikari.HikariDataSource;


/**
 * 
 * @author jalor
 *
 */
public abstract class AbstractKpiTopologyTest  {

    private static final List<String> setupSqls = Lists.newArrayList(
            "SET search_path = kpi_stream",
            "DROP TABLE IF  EXISTS settlement",
            "DROP SEQUENCE settlement_id_seq",
            "create sequence settlement_id_seq  INCREMENT by 1 ",
            "CREATE TABLE IF NOT EXISTS  settlement (id  integer NOT NULL  DEFAULT NEXTVAL('settlement_id_seq'),"
            + "ID_HOLDER_ACCOUNT_OPERATION_PK       integer,SETTLEMENT_AMOUNT         		integer,"
            + "SETTLED_QUANTITY   			integer,CONSTRAINT id_pk PRIMARY KEY(id));"
            
    );
    protected KpiSpout        kpiSpout;
    protected JdbcMapper 	   jdbcMapper;
    protected JdbcLookupMapper jdbcLookupMapper;
    protected ConnectionProvider connectionProviderTarget;
    protected ConnectionProvider connectionProviderSource;
    protected	MongoDBClient mongoDbClient ;
    protected static final String TABLE_NAME = "holder_account_operation";
    protected static final String JDBC_CONF = "jdbc.conf";
    
    protected static final String SELECT_QUERY_EXAMPLE = "select *  from (  select rownum as rownumber, T.* from(" +
    		 " select ID_HOLDER_ACCOUNT_OPERATION_PK,ID_MECHANISM_OPERATION_FK,ID_HOLDER_ACCOUNT_FK , OPERATION_PART,SETTLEMENT_AMOUNT,SETTLED_QUANTITY,LAST_MODIFY_DATE from holder_account_operation"+
    		 " where ID_HOLDER_ACCOUNT_OPERATION_PK = ? order by LAST_MODIFY_DATE desc ) T )R where rownumber < 2";
    
    protected static final String SELECT_QUERY_EXAMPLE2 = "select * from kpi_stream.settlement where ID_HOLDER_ACCOUNT_OPERATION_PK = ?";
    
    public void execute(String[] args) throws Exception {
        if (args != null) {
        	if (args.length < 8 ) {
	        	System.out.println("Usage: " + this.getClass().getSimpleName() + " <dataSourceClassName> <dataSource.url> "
	                    + "<user> <password> [topology name]");
	            System.exit(-1);
        	}else{
        		 callExecute(args);
        	}
        }
        
        
    }
    
    /**
     * Method only for test
     * @return
     */
    protected   List<Column> getColumnSchema() {
        Connection connection = null;
        List<Column> columns = new ArrayList<Column>();
        columns.add(new Column("ID_HOLDER_ACCOUNT_OPERATION_PK", Types.NUMERIC));
        columns.add(new Column("SETTLEMENT_AMOUNT", Types.NUMERIC));
        columns.add(new Column("SETTLED_QUANTITY", Types.NUMERIC));
        
        return columns;
    }
    public abstract StormTopology getTopology();
    
    
    protected void callExecute(String[] args){
    	
    	 if (args.length != 8) {
             System.out.println("Usage: " + this.getClass().getSimpleName() + " <dataSourceClassName> <dataSource.url> "
                     + "<user> <password> [topology name]");
             System.exit(-1);
         }
         
         /**
          *  Loading properties to be used in HikariFactory or HikariProvider
          */
         Map<String, Object> mapSource = Maps.newHashMap();
         mapSource.put("dataSourceClassName", args[0]);
         mapSource.put("dataSource.url", args[1]);
         mapSource.put("dataSource.user", args[2]);
         mapSource.put("dataSource.password", args[3]);
         
         Map<String, Object> mapTarget = Maps.newHashMap();
         mapTarget.put("dataSourceClassName", args[4]);
         mapTarget.put("dataSource.url", args[5]);
         mapTarget.put("dataSource.user", args[6]);
         mapTarget.put("dataSource.password", args[7]);
         
         Config config = new Config();
         config.put(JDBC_CONF, mapSource);
         //config.setNumWorkers(workers);
         //config.put(Config.TOPOLOGY_SLEEP_SPOUT_WAIT_STRATEGY_TIME_MS, 100000); 
         config.put(Config.TOPOLOGY_DEBUG, true);
         //config.put(Config.TOPOLOGY_SPOUT_WAIT_STRATEGY, "");
         
         
         connectionProviderSource = new HikariCPConnectionProvider(mapSource);
         connectionProviderTarget = new HikariCPConnectionProvider(mapTarget);
         connectionProviderTarget.prepare();
         
         int queryTimeoutSecs = 60;
         JdbcClient jdbcClient = new JdbcClient(connectionProviderTarget, queryTimeoutSecs);
         for (String sql : setupSqls) {
             try {
             	jdbcClient.executeSql(sql);
 			} catch (Exception e) {
 				System.out.println(" error mangment , dont worry");
 			}
         }
         connectionProviderTarget.cleanup();
         
         this.kpiSpout = new KpiSpout();
         Fields outputFields = new Fields("ID_HOLDER_ACCOUNT_OPERATION_PK", "ID_MECHANISM_OPERATION_FK", "ID_HOLDER_ACCOUNT_FK", "OPERATION_PART",
         		"SETTLEMENT_AMOUNT", "SETTLED_QUANTITY", "LAST_MODIFY_DATE");
         
         this.kpiSpout.setOutputFields(outputFields);
         this.kpiSpout.setSelectQuery(SELECT_QUERY_EXAMPLE);
         this.kpiSpout.setQueryTimeoutSecs(queryTimeoutSecs);
       //  this.kpiSpout.setConnectionProvider(connectionProviderSource);
         
         /**No usar */
//         this.jdbcMapper = new SimpleJdbcMapper(TABLE_NAME, connectionProviderTarget);
//         ((SimpleJdbcMapper)this.jdbcMapper).setSchemaColumns(getColumnSchema());
//         connectionProviderTarget.cleanup();
         
         List<Column> queryParamColumns = Lists.newArrayList(new Column("ID_HOLDER_ACCOUNT_OPERATION_PK", Types.NUMERIC));
         
         this.jdbcLookupMapper = new SimpleJdbcLookupMapper(outputFields, queryParamColumns);
         
         String topoName = "settlement_topology";
         
         LocalCluster cluster = new LocalCluster();
         cluster.submitTopology(topoName, config, getTopology());
         //StormSubmitter.submitTopology(topoName, config, getTopology());
    }
    
}
