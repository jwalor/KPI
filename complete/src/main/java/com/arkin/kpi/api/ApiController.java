package com.arkin.kpi.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.arkin.kpi.quartz.service.SessionDashboardService;
import com.arkin.kpi.socket.service.IntegrationService;
import com.arkin.kpi.socket.util.JsonUtils;
import com.arkin.kpi.socket.util.UtilException;


@SuppressWarnings({"unchecked" , "rawtypes"})
@RestController
public class ApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);
	
	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;
	
	@Autowired
	private SessionDashboardService sessionDashboardService;
	
	@Autowired
	private UtilException utilException;
	
	@Autowired
	IntegrationService integrationService;
 
	@RequestMapping(value="/process/streaming",
			method=RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE , produces={MimeTypeUtils.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> processTuple(@RequestBody String input) throws IOException {
		Map  mapEntity	 = JsonUtils.jsonToMap((String)input);
		Map<String,Object> dashboards = null;
		
		try {
			dashboards = sessionDashboardService.getDashboardsSessions(mapEntity);
			
			LOGGER.debug(" Process streaming came to consume one service : " + dashboards);
			if ( !dashboards.isEmpty()) {
				
				for (Map.Entry<String, Object> entry : dashboards.entrySet()) {
					SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
					headerAccessor.setSessionId(entry.getKey().toString());
					headerAccessor.setLeaveMutable(true);
					this.brokerMessagingTemplate.convertAndSendToUser(entry.getKey().toString(), "/queue/search", 
							JsonUtils.toJson(entry.getValue()), headerAccessor.getMessageHeaders());
				}
			}	
			
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
		return new ResponseEntity<>(dashboards, HttpStatus.OK);
		
	}
}
