
package com.arkin.kpi.quartz.rest;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.arkin.kpi.quartz.service.SessionDashboardService;
import com.arkin.kpi.socket.util.UtilException;

@RestController
@RequestMapping("/dashboard")
public class DashboardRestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EventsRestController.class);
	
	@Autowired
	private SessionDashboardService sessionDashboardService;
	
	@Autowired
	private UtilException utilException;
	
	@RequestMapping(value="getDashboardKpiByTable",method = RequestMethod.POST)
	private ResponseEntity<Map<String,Object>> getDashboardKpiByTable(@RequestBody Map<String, String> params){
		Map<String,Object> dashboards = null;
		try {
			dashboards = sessionDashboardService.getDashboardsSessions(params);
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
		return new ResponseEntity<>(dashboards, HttpStatus.OK);
	}
	
}

