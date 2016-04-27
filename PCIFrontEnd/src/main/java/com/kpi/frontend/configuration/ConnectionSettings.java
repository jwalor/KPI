package com.kpi.frontend.configuration;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Configuration
@PropertySource("classpath:jwt.properties")
@Component
public  class ConnectionSettings {
	
	@Autowired
	private  Environment env;
	
	public  String getClientSecrect(){
		return env.getProperty("AUTH0_CLIENT_SECRET");
	}
	
	public  String getUserId(){
		return env.getProperty("AUTH0_CLIENT_ID");
	}
	
}
