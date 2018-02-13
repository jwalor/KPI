package com.arkin.kpi.quartz.rest;

import java.util.HashMap;
import java.util.List;
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

import com.arkin.kpi.quartz.model.to.NotificationLoggerTo;
import com.arkin.kpi.quartz.service.NotificationsService;
import com.arkin.kpi.socket.util.UtilException;

@RestController
@RequestMapping("/events")
public class EventsRestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EventsRestController.class);
	
	@Autowired
	private NotificationsService notificationsService;
	
	@Autowired
	private UtilException utilException;
	
	@RequestMapping(value="getRulesNotificationsByEventsAndTable",method = RequestMethod.POST)
	private ResponseEntity<Map<String, Object>> getRulesNotificationsByEventsAndTable(@RequestBody Map<String, String> params){
		Map<String, Object> sessions = new HashMap<>();
		try {
			sessions = notificationsService.getRulesNotificationsByEventsAndTable(params);
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
		return new ResponseEntity<>(sessions, HttpStatus.OK);
	}
	
	@RequestMapping(value="getNotificationLoggerByUser",method = RequestMethod.POST)
	private ResponseEntity<Map<String,Object>> getNotificationLoggerByUser(@RequestBody Map<String, String> params){
		Map<String,Object> notificationsLogger = new HashMap<>();
		try {
			List<NotificationLoggerTo> notifications = notificationsService.getNotificationLoggerByUser(params);
			notificationsLogger.put("notifications", notifications);
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
		return new ResponseEntity<>(notificationsLogger, HttpStatus.OK);
	}
	
}
