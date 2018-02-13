package com.arkin.kpi.socket.config;

import java.security.Principal;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptorAdapter;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.session.ExpiringSession;
import org.springframework.session.web.socket.config.annotation.AbstractSessionWebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.server.HandshakeInterceptor;

import com.arkin.kpi.socket.util.StringUtil;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig extends AbstractSessionWebSocketMessageBrokerConfigurer<ExpiringSession>
		implements WebSocketConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfig.class);

	@SuppressWarnings("deprecation")
	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
		registration.setInterceptors(new ChannelInterceptorAdapter() {

			@SuppressWarnings("rawtypes")
			@Override
			public Message<?> preSend(Message<?> message, MessageChannel channel) {

				StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

				Map nativeHeader = accessor.toNativeHeaderMap();
				String userName = (nativeHeader.get("username") != null
						&& !nativeHeader.get("username").toString().isEmpty()) ? nativeHeader.get("username").toString()
								: "GENERIC";
				if (StompCommand.CONNECT.equals(accessor.getCommand())) {
					
					Principal user = new Principal() {
					String principalName =	StringUtil.getValueByPattern(userName);		
						@Override
						public String getName() {
							return principalName;
						}
					};
					accessor.setUser(user);
				}

				return message;
			}
		});
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry config) {
		config.enableStompBrokerRelay("/queue", "/topic");
		config.setApplicationDestinationPrefixes("/app");
		config.setUserDestinationPrefix("/user");
	}

	@Override
	public void configureStompEndpoints(StompEndpointRegistry registry) {

		registry.addEndpoint("/gs-guide-websocket").setAllowedOrigins("http://localhost:8080")
				.addInterceptors(new LoggerHandshakeInterceptor());

	}

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {

	}

	protected static class LoggerHandshakeInterceptor implements HandshakeInterceptor {

		@Override
		public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler,
				Exception exception) {

			LOG.info("WebSocket afterHandshake");
		}

		@Override
		public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
				WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
			LOG.info("WebSocket beforeHandshake");
			return true;
		}
	}

}