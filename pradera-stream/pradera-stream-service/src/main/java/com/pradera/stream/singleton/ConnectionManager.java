package com.pradera.stream.singleton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import com.pradera.stream.constant.Constant;
import com.pradera.stream.util.StringUtil;

/**
 * 
 * @author jalor
 *
 */
public class ConnectionManager {
	
	private static final Log LOG = LogFactory.getLog(ConnectionManager.class);
	private static List<HikariCPConnectionSingletonSource> connectionsConfigured = new ArrayList<>();
	
	public synchronized static void loadDataSource(Map<String, Object> dataSourceMap ) {
		HikariCPConnectionSingletonSource hikariCPConnectionSingletonSource = null;
		hikariCPConnectionSingletonSource = new HikariCPConnectionSingletonSource(dataSourceMap , (String) dataSourceMap.get("poolName"));
		connectionsConfigured.add(hikariCPConnectionSingletonSource);
		LOG.debug("Creating a new instance client on DB  " + dataSourceMap.get("poolName"));
	}
	
	public synchronized static void loadDataSources(List<Map<String, Object>> dataSourcesListMap ) {
		
		HikariCPConnectionProvider hikariCPConnectionSingletonSource = null;
		for (Map<String, Object> hikariCPConfigMap : dataSourcesListMap) {
			
			String nameDataSource = (String) hikariCPConfigMap.get("poolName");
			
			hikariCPConnectionSingletonSource = getHikariCPConnectionProvider( nameDataSource) ;
			if (hikariCPConnectionSingletonSource == null) {
				
				String _schema	= StringUtil.EMPTY;
				if (hikariCPConfigMap.get(Constant.Fields.SCHEMA)!=null) {
					_schema	= (String) hikariCPConfigMap.get(Constant.Fields.SCHEMA);
					hikariCPConfigMap.remove(Constant.Fields.SCHEMA);
				}
				hikariCPConnectionSingletonSource = new HikariCPConnectionSingletonSource(hikariCPConfigMap , (String) hikariCPConfigMap.get("poolName"));
				((HikariCPConnectionSingletonSource) hikariCPConnectionSingletonSource).setSchema(_schema);
				connectionsConfigured.add((HikariCPConnectionSingletonSource) hikariCPConnectionSingletonSource);
				LOG.debug("Creating a new instance client on DB  " + hikariCPConfigMap.get("poolName"));
				
			}
		}
		
	}
	
	public static HikariCPConnectionProvider getHikariCPConnectionProvider(String nameDataSource) {
		HikariCPConnectionProvider currentHikariDataSource = null;
		
		for (HikariCPConnectionSingletonSource hikariDataConnectionProvider : connectionsConfigured) {
			if (hikariDataConnectionProvider.getDataSourceName().equalsIgnoreCase(nameDataSource)) {
				currentHikariDataSource = hikariDataConnectionProvider;
				break;
			}
		}
		
		return currentHikariDataSource;
	}
	
	public synchronized static Boolean lostReference(String nameDataSource) {
		Boolean lostReference = true;
		for (HikariCPConnectionSingletonSource hikariDataConnectionProvider : connectionsConfigured) {
			if (  hikariDataConnectionProvider.getDataSourceName().equalsIgnoreCase(nameDataSource)
					&& 	!hikariDataConnectionProvider.lostReference()) {
				lostReference = false;
				break;
			}
		}
		return lostReference;
	}
	
}
