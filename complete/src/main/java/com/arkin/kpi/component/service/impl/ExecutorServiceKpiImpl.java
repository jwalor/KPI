package com.arkin.kpi.component.service.impl;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.arkin.kpi.component.service.ComponentNotification;
import com.arkin.kpi.component.service.ExecutorServiceKpi;
import com.arkin.kpi.quartz.service.NotificationsService;
import com.arkin.kpi.quartz.service.SessionDashboardService;
import com.arkin.kpi.socket.service.IntegrationService;
import com.arkin.kpi.socket.util.UtilException;


/**
 * 
 * @author jalor
 *
 */
@Component
@Transactional
@SuppressWarnings({"unchecked","rawtypes"})
public class ExecutorServiceKpiImpl implements ExecutorServiceKpi {

	private static final Logger LOGGER = LoggerFactory.getLogger(ExecutorServiceKpiImpl.class);
	
	@Autowired
	private SessionDashboardService sessionDashboardService;
	
	@Autowired
	private UtilException utilException;
	
	@Autowired
	IntegrationService integrationService;
	
	@Autowired
	ComponentNotification componentNotification;
	
	@Autowired
	NotificationsService notificationsService;
	
	@Async
	@Override
	public Map<String,Object> processKpiDashBoards(Map  mapEntity) {
		
		Map<String,Object> dashboards = null;
		
		try {
			
			dashboards = sessionDashboardService.getDashboardsSessions(mapEntity);
			
			LOGGER.debug(" Process streaming came to consume one service : " + dashboards);
			if ( !dashboards.isEmpty()) {
				for (Map.Entry<String, Object> entry : dashboards.entrySet()) {
					componentNotification.sentMessageForSessionId(entry.getKey().toString(),"/queue/search", entry.getValue());
				}
			}	
			
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
		
		return dashboards;
	}
	
	@Async
	@Override
	public void processNotificationsRules(Map  mapEntity) {
		notificationsService.getRulesNotificationsByEventsAndTable(mapEntity);
	}

	

}
