package com.arkin.kpi.quartz.service.impl;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arkin.kpi.quartz.service.NotificationsService;
import com.arkin.kpi.socket.util.UtilException;


@Service
public class SomeService {

	@Autowired
	private NotificationsService notificationsService;

	@Autowired
	private UtilException utilException;

	private static final Logger LOGGER = LoggerFactory.getLogger(SomeService.class);

	public void SendNotifications(int idSchedule) {
		try {
			// hacer algo
			System.out.println("Enviar Notificacion para escala: " + idSchedule);

			Map<String, Integer> params = new HashMap<>();
			params.put("idSchedule", idSchedule);

			notificationsService.getRulesNotificationsBySchedule(params);
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
	}
}
