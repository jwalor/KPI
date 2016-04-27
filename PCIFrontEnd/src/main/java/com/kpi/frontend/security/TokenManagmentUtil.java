package com.kpi.frontend.security;

import org.springframework.stereotype.Component;


@Component
public abstract class TokenManagmentUtil {
	
	//inject properties temporally
	
	protected String generateToken(String userId){
		return new String(userId);
	}
	
	protected String updateTokenByExp(long timeMills){
		return new String();
	}
	
	
}
