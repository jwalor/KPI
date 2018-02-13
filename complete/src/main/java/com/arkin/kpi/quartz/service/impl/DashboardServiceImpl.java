package com.arkin.kpi.quartz.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arkin.kpi.quartz.dao.DashboardDao;
import com.arkin.kpi.quartz.model.to.DashboardKpiTo;
import com.arkin.kpi.quartz.service.DashboardService;



@Transactional
@Service
@CacheConfig(cacheNames = "basicCache")
public class DashboardServiceImpl implements DashboardService {

	@Autowired
	private DashboardDao dashboardDao;	
		
	@Cacheable
	@Override
	public List<DashboardKpiTo> getDashboardKpiByTable (Map<String, String> params) {
		return dashboardDao.getDashboardKpiByTable(params);
	}
	

}
