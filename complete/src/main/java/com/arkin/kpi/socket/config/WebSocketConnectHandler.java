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
import com.arkin.kpi.socket.util.Constantes;
import com.arkin.kpi.socket.util.DateUtil;
import com.arkin.kpi.socket.util.StringUtil;

/**
 * 
 * @author jalor
 *
 * @param <S>
 */
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
	    
	    String dashboardPath = (nativeHeader.get("dashboardPath") != null
				&& !nativeHeader.get("dashboardPath").toString().isEmpty()) ? nativeHeader.get("dashboardPath").toString()
						: "/";
		Map map =  new HashMap<String, Object>();
		map.put(Constantes.idSession, id);
		map.put(Constantes.dashBoard, StringUtil.getValueByPattern(dashboardPath));
		map.put(Constantes.userName, user.getName());
		map.put(Constantes.registerDate, DateUtil.getSystemTimestamp());
		
		integrationService.saveSessionSocket(map);
	}
}