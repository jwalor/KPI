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
	public  static HikariDataSource dataSource;
	private static final Logger LOG = LoggerFactory.getLogger(HikariCPConnectionSingletonSource.class);

	private String info = "Initial info class";
	private static Map<String, Object> hikariCPConfigMap;

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

	public static void setHikariCPConfigMap(Map<String, Object> hikariCPConfigMap) {
		HikariCPConnectionSingletonSource.hikariCPConfigMap = hikariCPConfigMap;
	}
	
	public synchronized static Boolean lostReferences() {
		if (INSTANCE == null && HikariCPConnectionSingletonSource.hikariCPConfigMap == null) {
			 return true;
		}
		
		return false;
	}
	
//	public synchronized void prepare() {
//		if (dataSource == null) {
//			Properties properties = new Properties();
//			properties.putAll(HikariCPConnectionSingletonSource.hikariCPConfigMap);
//			HikariConfig config = new HikariConfig(properties);
//			if (properties.containsKey("dataSource.url")) {
//				LOG.info("DataSource Url: " + properties.getProperty("dataSource.url"));
//			} else if (config.getJdbcUrl() != null) {
//				LOG.info("JDBC Url: " + config.getJdbcUrl());
//			}
//			dataSource = new HikariDataSource(config);
//			dataSource.setAutoCommit(false);
//		}
//	}
	
//	public  synchronized void prepare1() {
//		if (dataSource1 == null) {
//			Properties properties = new Properties();
//			properties.putAll(HikariCPConnectionSingletonSource.hikariCPConfigMap);
//			HikariConfig config = new HikariConfig(properties);
//			if (properties.containsKey("dataSource.url")) {
//				LOG.info("DataSource Url: " + properties.getProperty("dataSource.url"));
//			} else if (config.getJdbcUrl() != null) {
//				LOG.info("JDBC Url: " + config.getJdbcUrl());
//			}
//			dataSource1 = new HikariDataSource1(config);
//			dataSource1.setAutoCommit(false);
//		}
//	}

//	@Override
//	public Connection getConnection() {
//		try {
//			if (StringUtils.isEmpty(dataSource1.getDataSourceClassName())) {
//				dataSource1.setDataSourceClassName("oracle.jdbc.pool.OracleDataSource");
//				dataSource1.addDataSourceProperty("user", "CSDCORE_BVL");
//				dataSource1.addDataSourceProperty("url", "jdbc:oracle:thin:@52.39.34.204:1521:xe");
//				dataSource1.addDataSourceProperty("password", "CSDCORE_BVL");
//			}
//			return dataSource1.getConnection();
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	@Override
//	public void cleanup() {
//		if (dataSource1 != null) {
//			dataSource1.close();
//		}
//	}

}
