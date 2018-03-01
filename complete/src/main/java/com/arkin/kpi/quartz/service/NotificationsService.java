package com.arkin.kpi.quartz.service;

import java.util.List;
import java.util.Map;

import com.arkin.kpi.quartz.job.config.JobProperties;
import com.arkin.kpi.quartz.model.to.NotificationLoggerTo;
import com.arkin.kpi.quartz.model.to.NotificationTo;
import com.arkin.kpi.quartz.model.to.ScheduleTo;


public interface NotificationsService {
	List<NotificationTo> getNotifications(Map<String, Integer> params);
	List<ScheduleTo> getSchedules(Map<String, Integer> params);
	List<JobProperties> getJobs();
	void getRulesNotificationsByEventsAndTable(Map<String, String> params);
	List<NotificationLoggerTo> getNotificationLoggerByUser(Map<String, String> params);
	void getRulesNotificationsBySchedule(Map<String, Integer> params);
	
}
