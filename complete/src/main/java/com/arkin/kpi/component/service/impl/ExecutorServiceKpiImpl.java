package com.arkin.kpi.component.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.arkin.kpi.component.service.ComponentNotification;
import com.arkin.kpi.component.service.ExecutorServiceKpi;
import com.arkin.kpi.quartz.dao.NotificationsDao;
import com.arkin.kpi.quartz.model.to.EmailTemplateTo;
import com.arkin.kpi.quartz.model.to.RulesNotificationsTo;
import com.arkin.kpi.quartz.model.to.RulesResultTo;
import com.arkin.kpi.quartz.model.to.UsersDestinationsTo;
import com.arkin.kpi.quartz.model.type.NotificationType;
import com.arkin.kpi.quartz.service.EmailService;
import com.arkin.kpi.quartz.service.NotificationsService;
import com.arkin.kpi.quartz.service.SessionDashboardService;
import com.arkin.kpi.socket.service.IntegrationService;
import com.arkin.kpi.socket.util.Constantes;
import com.arkin.kpi.socket.util.DateUtil;
import com.arkin.kpi.socket.util.JsonUtils;
import com.arkin.kpi.socket.util.StringUtil;
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
