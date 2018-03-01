package com.arkin.kpi.socket.dao.config;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import com.zaxxer.hikari.HikariDataSource;

/**
 * 
 * @author jalor
 *
 */
@Configuration
public class ApplicationDaoConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDaoConfig.class);
	
	@Value("${init-db:false}")
	private String initDatabase;

	@Bean
    public NamedParameterJdbcTemplate  namedParameterJdbcTemplate (HikariDataSource dataSource) {
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        return jdbcTemplate;
    }
    
	
    @Bean(destroyMethod = "shutdown")
    public HikariDataSource dataSource(DataSourceProperties properties) {
    	
    	 if (properties.getUrl() == null && properties.determineUsername() == null) {
    		 LOGGER.error("Your database connection pool configuration is incorrect! The application" +
    	                " cannot start. Please check your Spring profile, current profiles are: {}",
    	                "Development Enviroment");

    	        throw new ApplicationContextException("Database connection pool is not configured correctly");
    	    }
    	 HikariDataSource  dataSource = (HikariDataSource) properties.initializeDataSourceBuilder()
                .type(HikariDataSource.class).build();

         return dataSource;
    }
    
    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }

}
