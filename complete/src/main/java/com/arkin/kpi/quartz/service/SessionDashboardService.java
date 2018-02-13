package com.arkin.kpi.quartz.service;

import java.util.Map;

public interface SessionDashboardService {
	
	Map<String , Object> getDashboardsSessions(Map<String,String> params);

}
