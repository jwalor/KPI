package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.RulesNotificationsTo;


public class RulesNotificationsRowMapper implements RowMapper<RulesNotificationsTo>{

	@Override
	public RulesNotificationsTo mapRow(ResultSet rs, int rowNum)
			throws SQLException {

		RulesNotificationsTo rulesNotifications = new RulesNotificationsTo();
		rulesNotifications.setLngNotificationProcessPk(rs.getLong("id_notification_process_pk"));
		rulesNotifications.setTableName(rs.getString("table_name"));
		rulesNotifications.setRuleFormule(rs.getString("rule_formule"));
		rulesNotifications.setColumnName(rs.getString("column_name"));
		rulesNotifications.setComparatorRef(rs.getString("comparator_ref"));
		rulesNotifications.setValueReturnRef(rs.getString("value_return_ref"));
		rulesNotifications.setParameterName(rs.getString("parameter_name"));
		rulesNotifications.setValueToNotify(rs.getString("value_notification"));
		rulesNotifications.setNotificationType(rs.getInt("notification_type"));
		rulesNotifications.setNotificationSubject(rs.getString("notification_subject"));
		rulesNotifications.setNotificationMessage(rs.getString("notification_message"));
		rulesNotifications.setNotificationName(rs.getString("notification_name"));
		rulesNotifications.setAlertType(rs.getInt("alert_type"));
		rulesNotifications.setNotificationState(rs.getInt("notification_state"));
		rulesNotifications.setIdRulePk(rs.getInt("id_rule_pk"));
		rulesNotifications.setRuleName(rs.getString("rule_name"));
		
		return rulesNotifications;
	}

}
