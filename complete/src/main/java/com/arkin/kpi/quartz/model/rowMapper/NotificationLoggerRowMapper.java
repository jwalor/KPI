package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.NotificationLoggerTo;


public class NotificationLoggerRowMapper implements RowMapper<NotificationLoggerTo> {

	@Override
	public NotificationLoggerTo mapRow(ResultSet rs, int rowNum) throws SQLException {
		NotificationLoggerTo notificationLogger = new NotificationLoggerTo();
		
		notificationLogger.setIdNotificationProcess(rs.getLong("id_notification_process_fk"));
		notificationLogger.setNotificationName(rs.getString("notification_name"));
		notificationLogger.setNotificationSubject(rs.getString("notification_subject"));
		notificationLogger.setNotification_message(rs.getString("notification_message"));
		notificationLogger.setNotificationType(rs.getInt("notification_type"));
		notificationLogger.setNotificationDate(rs.getString("notification_date"));
		notificationLogger.setDestinationType(rs.getInt("destination_type"));
		notificationLogger.setAlertType(rs.getInt("alert_type"));
		notificationLogger.setNotificationState(rs.getInt("notification_state"));
		
		return notificationLogger;
	}

}
