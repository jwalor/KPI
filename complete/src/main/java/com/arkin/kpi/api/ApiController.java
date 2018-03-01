package com.arkin.kpi.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.arkin.kpi.component.service.ComponentNotification;
import com.arkin.kpi.component.service.ExecutorServiceKpi;
import com.arkin.kpi.quartz.service.SessionDashboardService;
import com.arkin.kpi.socket.service.IntegrationService;
import com.arkin.kpi.socket.util.JsonUtils;
import com.arkin.kpi.socket.util.UtilException;


@SuppressWarnings({"unchecked" , "rawtypes"})
@RestController
public class ApiController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ApiController.class);
	
	
	@Autowired
	private UtilException utilException;
	
	@Autowired
	IntegrationService integrationService;
	
	@Autowired
	ComponentNotification componentNotification;
	
	@Autowired
	ExecutorServiceKpi	executorServiceKpi;
	
	@RequestMapping(value="/process/streaming",
			method=RequestMethod.POST,  consumes = MediaType.APPLICATION_JSON_VALUE , produces={MimeTypeUtils.APPLICATION_JSON_VALUE})
	public ResponseEntity<?> processTuple(@RequestBody String input) throws IOException {
	
		Map<String,Object> dashboards = new HashMap<>();
		try {
			
			Map  mapEntity	 = JsonUtils.jsonToMap((String)input);
			
			///////////////////////////////////////////////////////////////////////////////////
			/**
			 *  Processing async about Rules and notifications.
			 */
			executorServiceKpi.processNotificationsRules(mapEntity);
			
			///////////////////////////////////////////////////////////////////////////////////
			/**
			 *   Processing async about dashboard's kpis.
			 */
			
			dashboards = executorServiceKpi.processKpiDashBoards(mapEntity);
			
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
		return new ResponseEntity<>(dashboards, HttpStatus.OK);
		
	}
}
