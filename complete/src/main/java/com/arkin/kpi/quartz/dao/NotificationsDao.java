package com.arkin.kpi.quartz.dao;



import java.util.List;
import java.util.Map;

import com.arkin.kpi.quartz.model.to.NotificationDestinationsTo;
import com.arkin.kpi.quartz.model.to.NotificationLoggerTo;
import com.arkin.kpi.quartz.model.to.NotificationTo;
import com.arkin.kpi.quartz.model.to.RulesNotificationsTo;
import com.arkin.kpi.quartz.model.to.RulesResultTo;
import com.arkin.kpi.quartz.model.to.ScheduleTo;
import com.arkin.kpi.quartz.model.to.SessionSocketTo;
import com.arkin.kpi.quartz.model.to.UsersDestinationsTo;

public interface NotificationsDao {
	List<NotificationTo> getNotifications(Map<String, Integer> params);
	List<ScheduleTo> getSchedules(Map<String, Integer> params);
	List<RulesNotificationsTo> getRulesNotificationsByEvents();
	List<RulesResultTo> getRulesResult(String query);
	List<NotificationDestinationsTo> getNotificationDestinations(Map<String, Long> params);
	List<RulesNotificationsTo> getRulesNotificationsByEventsAndTable(Map<String, String> params);
	void saveNotificationLogger(Map map);
	List<UsersDestinationsTo> getUsersDestinationsByNotifications(Map<String, Long> params);
	List<SessionSocketTo> getSessionSocketByUser(Map<String,String> params);
	List<NotificationLoggerTo> getNotificationLoggerByUser(Map<String,String> params);
	List<RulesNotificationsTo> getRulesNotificationsBySchedule(Map<String, Integer> params);
	
}
