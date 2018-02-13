package com.arkin.kpi.quartz.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import com.arkin.kpi.quartz.dao.NotificationsDao;
import com.arkin.kpi.quartz.model.rowMapper.NotificationDestinationsRowMapper;
import com.arkin.kpi.quartz.model.rowMapper.NotificationLoggerRowMapper;
import com.arkin.kpi.quartz.model.rowMapper.NotificationRowMapper;
import com.arkin.kpi.quartz.model.rowMapper.RulesNotificationsRowMapper;
import com.arkin.kpi.quartz.model.rowMapper.RulesResultRowMapper;
import com.arkin.kpi.quartz.model.rowMapper.ScheduleRowMapper;
import com.arkin.kpi.quartz.model.rowMapper.SessionSocketRowMapper;
import com.arkin.kpi.quartz.model.rowMapper.UsersDestinationsRowMapper;
import com.arkin.kpi.quartz.model.to.NotificationDestinationsTo;
import com.arkin.kpi.quartz.model.to.NotificationLoggerTo;
import com.arkin.kpi.quartz.model.to.NotificationTo;
import com.arkin.kpi.quartz.model.to.RulesNotificationsTo;
import com.arkin.kpi.quartz.model.to.RulesResultTo;
import com.arkin.kpi.quartz.model.to.ScheduleTo;
import com.arkin.kpi.quartz.model.to.SessionSocketTo;
import com.arkin.kpi.quartz.model.to.UsersDestinationsTo;
import com.arkin.kpi.socket.util.Constantes;
import com.arkin.kpi.socket.util.DateUtil;

@Repository
public class NotificationsDaoImpl implements NotificationsDao {

	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
	
	@Override
	public List<NotificationTo> getNotifications(Map<String, Integer> params) {

		String query = Constantes.Querys.SELECT_NOTIFICATIONS + Constantes.Querys.WHERE_NOTIFICATIONS;

		return jdbcTemplate.query(query, params, new NotificationRowMapper() {
		});
	}

	public List<ScheduleTo> getSchedules(Map<String, Integer> params) {

		String query = Constantes.Querys.SELECT_SCHEDULE_WITH_CRONEXPRESIONS
				+ Constantes.Querys.WHERE_SCHEDULES_WITH_CRONEXPRESIONS;

		return jdbcTemplate.query(query, params, new ScheduleRowMapper() {
		});
	}
	
	@Override
	public List<RulesNotificationsTo> getRulesNotificationsByEvents(){
		
		return jdbcTemplate.query(Constantes.Querys.SELECT_RULES_NOTIFICATIONS_EVENTS, new RulesNotificationsRowMapper());
	}

	@Override
	public List<RulesResultTo> getRulesResult(String query) {
		
		return jdbcTemplate.query(query, new RulesResultRowMapper());
	}
	
	@Override
	public List<NotificationDestinationsTo> getNotificationDestinations(Map<String, Long> params){
		
		return jdbcTemplate.query(Constantes.Querys.SELECT_NOTIFICATION_DESTINATIONS, params,new NotificationDestinationsRowMapper());
	}

	@Override
	public List<RulesNotificationsTo> getRulesNotificationsByEventsAndTable(Map<String, String> params) {
		return jdbcTemplate.query(Constantes.Querys.SELECT_RULES_NOTIFICATIONS_EVENTS_BYTABLE, params, new RulesNotificationsRowMapper());
	}
	
	@Override
	public void saveNotificationLogger(Map map) {
		
		KeyHolder keyHolder = new GeneratedKeyHolder();
		
		MapSqlParameterSource parameters = new MapSqlParameterSource()
			 .addValue(Constantes.NotificationLogger.ID_NOTIFICATION_PROCESS, map.get(Constantes.NotificationLogger.ID_NOTIFICATION_PROCESS))
			 .addValue(Constantes.NotificationLogger.ID_USER, map.get(Constantes.NotificationLogger.ID_USER))
			 .addValue(Constantes.NotificationLogger.NOTIFICATION_NAME, map.get(Constantes.NotificationLogger.NOTIFICATION_NAME))
			 .addValue(Constantes.NotificationLogger.NOTIFICATION_SUBJECT, map.get(Constantes.NotificationLogger.NOTIFICATION_SUBJECT))
			 .addValue(Constantes.NotificationLogger.NOTIFICATION_MESSAGE, map.get(Constantes.NotificationLogger.NOTIFICATION_MESSAGE))
			 .addValue(Constantes.NotificationLogger.NOTIFICATION_TYPE, map.get(Constantes.NotificationLogger.NOTIFICATION_TYPE))
			 .addValue(Constantes.NotificationLogger.NOTIFICATION_DATE, DateUtil.currentDate())
			 .addValue(Constantes.NotificationLogger.DESTINATION_TYPE, map.get(Constantes.NotificationLogger.DESTINATION_TYPE))
			 .addValue(Constantes.NotificationLogger.ALERT_TYPE, map.get(Constantes.NotificationLogger.ALERT_TYPE))
			 .addValue(Constantes.NotificationLogger.NOTIFICATION_STATE, map.get(Constantes.NotificationLogger.NOTIFICATION_STATE))			               
			 .addValue(Constantes.NotificationLogger.REGISTER_DATE, DateUtil.currentDate())
			 .addValue(Constantes.NotificationLogger.LAST_MODIFY_DATE, DateUtil.currentDate())
			 .addValue(Constantes.NotificationLogger.LAST_MODIFY_APP, 1)
			 .addValue(Constantes.NotificationLogger.LAST_MODIFY_USER, "CAQUI")
			 .addValue(Constantes.NotificationLogger.LAST_MODIFY_IP, "localhost");
		
		jdbcTemplate.update(Constantes.Querys.INSERT_INTO_NOTIFICATION_LOGGER, parameters, keyHolder, new String[]{"id_notification_logger_pk"});
			
	}

	@Override
	public List<UsersDestinationsTo> getUsersDestinationsByNotifications(Map<String, Long> params) {
		return jdbcTemplate.query(Constantes.Querys.SELECT_USERS_DESTINATIONS_BY_NOTIFICATION, params, new UsersDestinationsRowMapper());
	}
	
	@Override
	public List<SessionSocketTo> getSessionSocketByUser(Map<String,String> params) {
		return jdbcTemplate.query(Constantes.Querys.SELECT_SESSION_SOCKET_BY_USER, params, new SessionSocketRowMapper());
	}

	@Override
	public List<NotificationLoggerTo> getNotificationLoggerByUser(Map<String, String> params) {
		return jdbcTemplate.query(Constantes.Querys.SELECT_NOTIFICATION_LOGGER_BY_USER, params,new NotificationLoggerRowMapper());
	}

	@Override
	public List<RulesNotificationsTo> getRulesNotificationsBySchedule(Map<String, Integer> params) {
		return jdbcTemplate.query(Constantes.Querys.SELECT_RULES_NOTIFICATIONS_BYSCHEDULE, params, new RulesNotificationsRowMapper());
	}

}
