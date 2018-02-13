package com.arkin.kpi.quartz.service;

import java.util.List;
import java.util.Map;

import com.arkin.kpi.quartz.model.to.DashboardKpiTo;

public interface DashboardService {

	List<DashboardKpiTo> getDashboardKpiByTable (Map<String, String> params);
	
}
