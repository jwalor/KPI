package com.arkin.kpi.socket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.session.ExpiringSession;

import com.arkin.kpi.socket.service.IntegrationService;

@Configuration
public class WebSocketHandlersConfig<S extends ExpiringSession> {
	
	
	@Bean
	public WebSocketConnectHandler<S> webSocketConnectHandler(SimpMessageSendingOperations messagingTemplate  , IntegrationService integrationService) {
		return new WebSocketConnectHandler<S>(messagingTemplate , integrationService);
	}

	@Bean
	public WebSocketDisconnectHandler<S> webSocketDisconnectHandler(SimpMessageSendingOperations messagingTemplate , IntegrationService integrationService) {
		return new WebSocketDisconnectHandler<S>(messagingTemplate , integrationService);
	}
}
