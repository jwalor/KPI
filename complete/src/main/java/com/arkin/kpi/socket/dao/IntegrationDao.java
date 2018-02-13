package com.arkin.kpi.socket.dao;

import java.util.Map;

@SuppressWarnings("rawtypes")
public interface IntegrationDao {
	
	
	public int getIntegrationTableCount();
	public void saveSessionSocket(Map map) ;
	public void deleteSessionSocket(Map map);

}
