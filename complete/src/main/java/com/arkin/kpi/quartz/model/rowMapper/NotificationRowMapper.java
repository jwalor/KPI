package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.NotificationTo;


/**
 * @author Giancarlo
 *
 */
public class NotificationRowMapper implements RowMapper<NotificationTo> {

	@Override
	public NotificationTo mapRow(ResultSet rs, int rowNum) throws SQLException {
		// TODO Auto-generated method stub
		
		NotificationTo notification = new NotificationTo();  
		notification.setStrNotificationName(rs.getString("notification_name"));
		notification.setIntNotificationType(rs.getInt("notification_type"));
		notification.setIntAlertType(rs.getInt("alert_type"));
		notification.setStrNotificationSubject(rs.getString("notification_subject"));
		notification.setStrNotificationMessage(rs.getString("notification_message"));
		notification.setIntNotificationState(rs.getInt("notification_state"));
		notification.setScheduleTo(null);
		notification.setLngScheduleToPk(rs.getLong("id_schedule_process_fk"));
		notification.setIntNotificationSendType(rs.getInt("notification_send_type"));
		
		return notification;  
	}
	
	
	
}
