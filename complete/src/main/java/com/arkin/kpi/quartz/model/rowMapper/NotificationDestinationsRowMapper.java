package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.NotificationDestinationsTo;


public class NotificationDestinationsRowMapper implements RowMapper<NotificationDestinationsTo> {

	@Override
	public NotificationDestinationsTo mapRow(ResultSet rs, int rowNum)
			throws SQLException {
		
		NotificationDestinationsTo notificationDestinations = new NotificationDestinationsTo();
		notificationDestinations.setIdNotificationDestination(rs.getLong("id_notification_destination_pk"));
		notificationDestinations.setIdNotificationProcess(rs.getLong("id_notification_process_fk"));
		notificationDestinations.setDestinationType(rs.getInt("destination_type"));
		notificationDestinations.setIdUserPk(rs.getLong("id_user_fk"));
		notificationDestinations.setIdProfilePk(rs.getLong("id_profile_fk"));
				
		return notificationDestinations;
	}

	

}
