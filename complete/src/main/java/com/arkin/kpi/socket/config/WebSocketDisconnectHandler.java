package com.arkin.kpi.socket.config;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import com.arkin.kpi.socket.service.IntegrationService;
import com.arkin.kpi.socket.util.Constant;
import com.arkin.kpi.socket.util.DateUtil;


public class WebSocketDisconnectHandler<S> implements ApplicationListener<SessionDisconnectEvent> {

	private SimpMessageSendingOperations messagingTemplate;
	private	IntegrationService integrationService;
	
	private static final Logger logger = LoggerFactory.getLogger(WebSocketDisconnectHandler.class);
	
	public WebSocketDisconnectHandler(SimpMessageSendingOperations messagingTemplate , IntegrationService integrationService) {
		super();
		this.messagingTemplate = messagingTemplate;
		this.integrationService = integrationService;
	}

	public void onApplicationEvent(SessionDisconnectEvent event) {
		String id = event.getSessionId();
		if(id == null) {
			return;
		}
		
		StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
		logger.info(headerAccessor.toString());
		
		Map map =  new HashMap<String, Object>();
		map.put(Constant.idSession, id);
		map.put(Constant.state, 0); //disabled
		map.put(Constant.userName, headerAccessor.getUser().getName());
		map.put(Constant.updateDate, DateUtil.getSystemTimestamp());
		
		integrationService.deleteSessionSocket(map);
		
	}
}