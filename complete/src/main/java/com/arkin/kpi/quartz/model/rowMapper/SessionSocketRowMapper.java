package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.SessionSocketTo;


public class SessionSocketRowMapper implements RowMapper<SessionSocketTo> {

	@Override
	public SessionSocketTo mapRow(ResultSet rs, int rowNum) throws SQLException {
		SessionSocketTo sessionSocket = new SessionSocketTo();
		
		sessionSocket.setId(rs.getLong("id"));
		sessionSocket.setUserName(rs.getString("user_name"));
		sessionSocket.setIdSession(rs.getString("id_session"));
		sessionSocket.setDashboardPath(rs.getString("dashboard_path"));
		sessionSocket.setRegisterDate(rs.getString("register_date"));
		String updateDate = rs.getString("update_date") != null ? rs.getString("update_date") : "";
		sessionSocket.setUpdateDate(updateDate);
		sessionSocket.setState(rs.getInt("state"));
		
		return sessionSocket;
				
	}

}
