package com.arkin.kpi.socket.service;

import java.util.Map;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("rawtypes")
public interface IntegrationService {
	
	public int getIntegrationTableCount();
	
	public void saveSessionSocket(Map map) ;
	
	public void deleteSessionSocket( Map map) ;

}
