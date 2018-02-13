package com.arkin.kpi.quartz.dao;

import java.util.List;
import java.util.Map;

import com.arkin.kpi.quartz.model.to.DashboardKpiTo;
import com.arkin.kpi.quartz.model.to.SessionSocketTo;

public interface DashboardDao {
	
	List<DashboardKpiTo> getDashboardKpiByTable(Map<String,String> params);
	List<SessionSocketTo> getSessionSocket();	

}
