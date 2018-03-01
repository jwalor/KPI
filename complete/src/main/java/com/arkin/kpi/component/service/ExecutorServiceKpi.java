package com.arkin.kpi.component.service;

import java.util.Map;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"rawtypes" })
public interface ExecutorServiceKpi {
	
	public Map<String,Object> processKpiDashBoards( Map  mapEntity);
	public void processNotificationsRules(Map  mapEntity);
	
}
