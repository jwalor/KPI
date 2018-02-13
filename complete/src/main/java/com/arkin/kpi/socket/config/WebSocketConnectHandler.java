package com.arkin.kpi.socket.config;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationListener;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.web.socket.messaging.SessionConnectEvent;

import com.arkin.kpi.socket.service.IntegrationService;
import com.arkin.kpi.socket.util.Constant;
import com.arkin.kpi.socket.util.DateUtil;
import com.arkin.kpi.socket.util.StringUtil;

@SuppressWarnings({"unchecked","rawtypes"})
public class WebSocketConnectHandler<S> implements ApplicationListener<SessionConnectEvent> {
	
	private SimpMessageSendingOperations messagingTemplate;
	private	IntegrationService integrationService;

	public WebSocketConnectHandler(SimpMessageSendingOperations messagingTemplate , IntegrationService integrationService) {
		super();
		this.messagingTemplate = messagingTemplate;
		this.integrationService = integrationService;
	}

	public void onApplicationEvent(SessionConnectEvent event) {
		MessageHeaders headers = event.getMessage().getHeaders();
		   StompHeaderAccessor accessor =
	                MessageHeaderAccessor.getAccessor(event.getMessage(), StompHeaderAccessor.class);
		
		Principal user = SimpMessageHeaderAccessor.getUser(headers);
		if(user == null) {
			return;
		}
		
		/**
		 *  Create MAP
		 */
	    String id = SimpMessageHeaderAccessor.getSessionId(headers);
	    
	    Map nativeHeader = accessor.toNativeHeaderMap();
	    
	    String dashboardPath = (nativeHeader.get("dashboardPat") != null
				&& !nativeHeader.get("dashboardPat").toString().isEmpty()) ? nativeHeader.get("dashboardPat").toString()
						: "/";
		Map map =  new HashMap<String, Object>();
		map.put(Constant.idSession, id);
		map.put(Constant.dashBoard, StringUtil.getValueByPattern(dashboardPath));
		map.put(Constant.userName, user.getName());
		map.put(Constant.registerDate, DateUtil.getSystemTimestamp());
		
		integrationService.saveSessionSocket(map);
	}
}