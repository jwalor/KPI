package com.arkin.kpi.quartz.service;

import java.util.List;
import java.util.Map;

import com.arkin.kpi.quartz.job.config.JobProperties;
import com.arkin.kpi.quartz.model.to.NotificationLoggerTo;
import com.arkin.kpi.quartz.model.to.NotificationTo;
import com.arkin.kpi.quartz.model.to.ScheduleTo;
import com.arkin.kpi.quartz.model.to.SessionSocketTo;
import com.arkin.kpi.quartz.model.to.UsersDestinationsTo;


public interface NotificationsService {
	List<NotificationTo> getNotifications(Map<String, Integer> params);
	List<ScheduleTo> getSchedules(Map<String, Integer> params);
	List<JobProperties> getJobs();
	Map<String, Object> getRulesNotificationsByEventsAndTable(Map<String, String> params);
	List<UsersDestinationsTo> getUsersDestinationsByNotifications(Map<String, Long> params);
	List<SessionSocketTo> getSessionSocketByUser(Map<String,String> params);
	List<NotificationLoggerTo> getNotificationLoggerByUser(Map<String, String> params);
	Map<String, Object> getRulesNotificationsBySchedule(Map<String, Integer> params);
}
