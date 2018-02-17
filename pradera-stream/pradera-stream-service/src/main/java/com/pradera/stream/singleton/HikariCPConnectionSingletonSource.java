package com.pradera.stream.singleton;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.storm.jdbc.common.HikariCPConnectionProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("unused")
public class HikariCPConnectionSingletonSource extends HikariCPConnectionProvider implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5006266166721749291L;
	private static HikariCPConnectionSingletonSource INSTANCE;
	private static final Logger LOG = LoggerFactory.getLogger(HikariCPConnectionSingletonSource.class);

	private String info = "Initial info class";
	public static Map<String, Object> hikariCPConfigMap;
	private String dataSourceName;
	
	public HikariCPConnectionSingletonSource() {
		super(hikariCPConfigMap);
	}
	
	public HikariCPConnectionSingletonSource(Map<String, Object> hikariCPConfigMap) {
		super(hikariCPConfigMap);
	}

	public synchronized static HikariCPConnectionSingletonSource getInstance() {
		if (INSTANCE == null) {
			Validate.notNull(HikariCPConnectionSingletonSource.hikariCPConfigMap);
			INSTANCE = new HikariCPConnectionSingletonSource(HikariCPConnectionSingletonSource.hikariCPConfigMap);
		}
		return INSTANCE;
	}

	public static Map<String, Object> getHikariCPConfigMap() {
		return hikariCPConfigMap;
	}
	
	public String getDataSourceName() {
		return dataSourceName;
	}

	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}

	public static void setHikariCPConfigMap(Map<String, Object> hikariCPConfigMap) {
		HikariCPConnectionSingletonSource.hikariCPConfigMap = hikariCPConfigMap;
	}
	
	public synchronized static Boolean lostReferences() {
		if (INSTANCE == null && HikariCPConnectionSingletonSource.hikariCPConfigMap == null) {
			 return true;
		}
		
		return false;
	}
	
}
