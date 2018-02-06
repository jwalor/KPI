package com.pradera.stream.singleton;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.Validate;
import org.apache.commons.lang3.SerializationUtils;
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
public  class HikariCPConnectionSingletonTarget extends  HikariCPConnectionProvider implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5006266166721749291L;
	private static HikariCPConnectionSingletonTarget INSTANCE;
	public  transient HikariDataSource dataSource;
	private String info = "Initial info class";
	private static Map<String, Object> hikariCPConfigMap;
	
    private static final Logger LOG = LoggerFactory.getLogger(HikariCPConnectionSingletonTarget.class);

    
	public HikariCPConnectionSingletonTarget(Map<String, Object> hikariCPConfigMap) {
		super(hikariCPConfigMap);
	}

	public static HikariCPConnectionSingletonTarget getInstance() {
		if (INSTANCE == null) {
			Validate.notNull(HikariCPConnectionSingletonTarget.hikariCPConfigMap);
			INSTANCE = new HikariCPConnectionSingletonTarget(HikariCPConnectionSingletonTarget.hikariCPConfigMap);
		}
		return INSTANCE;
    }
	
	public static Map<String, Object> getHikariCPConfigMap() {
		return hikariCPConfigMap;
	}

	public static  void setHikariCPConfigMap(Map<String, Object> hikariCPConfigMap) {
		HikariCPConnectionSingletonTarget.hikariCPConfigMap = hikariCPConfigMap;
	}

	public HikariDataSource getDataSource() {
		return dataSource;
	}

	public static Boolean lostReferences() {
		if (INSTANCE == null && HikariCPConnectionSingletonTarget.hikariCPConfigMap == null) {
			 return true;
		}
		
		return false;
	}
	
//  public static  synchronized void prepare(HikariCPConnectionSingletonTarget hikariCPConnectionSingletonTarget) {
//    if(hikariCPConnectionSingletonTarget.dataSource == null) {
//        Properties properties = new Properties();
//        properties.putAll(HikariCPConnectionSingletonTarget.hikariCPConfigMap);
//        HikariConfig config = new HikariConfig(properties);
//        if(properties.containsKey("dataSource.url")) {
//            LOG.info("DataSource Url: " + properties.getProperty("dataSource.url"));
//        }
//        else if (config.getJdbcUrl() != null) {
//            LOG.info("JDBC Url: " + config.getJdbcUrl());
//        }
//        hikariCPConnectionSingletonTarget.dataSource = new HikariDataSource(config);
//        hikariCPConnectionSingletonTarget.dataSource.setAutoCommit(false);
//    }
// }
//	
//
//	@Override
//	public Connection getConnection() {
//		try {
//			return dataSource.getConnection();
//		} catch (SQLException e) {
//			throw new RuntimeException(e);
//		}
//	}
//
//	@Override
//	public void cleanup() {
//		if (dataSource != null) {
//			dataSource.close();
//		}
//	}

	
}
