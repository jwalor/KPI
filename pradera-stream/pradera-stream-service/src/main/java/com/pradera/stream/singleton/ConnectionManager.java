package com.pradera.stream.singleton;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;

import com.pradera.stream.constant.Constant;

/**
 * 
 * @author jalor
 *
 */
public class ConnectionManager {
	
	private static final Log LOG = LogFactory.getLog(ConnectionManager.class);
	private static List<HikariCPConnectionSingletonSource> connectionsConfigured;
	
	
	public synchronized static void loadDataSources(List<Map<String, Object>> dataSourcesListMap ) {
		
		HikariCPConnectionSingletonSource hikariCPConnectionSingletonSource = null;
		for (Map<String, Object> hikariCPConfigMap : dataSourcesListMap) {
			
			hikariCPConnectionSingletonSource = new HikariCPConnectionSingletonSource(hikariCPConfigMap);
			hikariCPConnectionSingletonSource.setDataSourceName((String) hikariCPConfigMap.get(Constant.Fields.DATASOURCE_NAME));
			connectionsConfigured.add(hikariCPConnectionSingletonSource);
			LOG.debug("Creating a new instance client on DB  " + hikariCPConfigMap.get(Constant.Fields.DATASOURCE_NAME));
		}
		
	}
	
	
	public static HikariCPConnectionProvider getHikariCPConnectionProvider(String nameDataSource) throws SQLException {
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
		Boolean lostReference = false;
		for (HikariCPConnectionSingletonSource hikariDataConnectionProvider : connectionsConfigured) {
			if ( hikariDataConnectionProvider != null && hikariDataConnectionProvider.getDataSourceName().equalsIgnoreCase(nameDataSource)) {
				if ( HikariCPConnectionSingletonSource.hikariCPConfigMap == null) {
					lostReference = true;
					break;
				}
			}
			lostReference = true;
		}
		return lostReference;
	}
	
	
	
}
