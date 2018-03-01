package com.arkin.kpi.socket.service.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arkin.kpi.socket.dao.IntegrationDao;
import com.arkin.kpi.socket.service.IntegrationService;

@Transactional
@Service
@CacheConfig(cacheNames = "basicCache")
public class IntegrationServiceImpl  implements IntegrationService {
	
	
	@Autowired
	IntegrationDao integrationDao;
	
	@Cacheable
	@Override
	public int getIntegrationTableCount() {
		int i = integrationDao.getIntegrationTableCount();
		System.out.println("---> Loading country with code '" + i + "'");
		return i;
	}

	public void saveSessionSocket(@SuppressWarnings("rawtypes") Map map) {
		integrationDao.saveSessionSocket(map);
	}
	
	public void deleteSessionSocket(@SuppressWarnings("rawtypes") Map map) {
		integrationDao.deleteSessionSocket(map);
	}
	
}
