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


/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("rawtypes")
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
	public void saveNotificationLogger( Map map) {
		
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
			 .addValue(Constantes.NotificationLogger.IND_VIEW, map.get(Constantes.NotificationLogger.IND_VIEW))
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

	@Override
	public List<UsersDestinationsTo> getUsersDestinationsYetDontSeenNotification(Map<String, Object> params) {
		
		StringBuilder _sbQuery = new StringBuilder();
		_sbQuery.append("select * from  ( ");
		_sbQuery.append(" select distinct case when dst.id_user_fk is null then usp.id_user_pk  else usr.id_user_pk end as id_user_pk, ");
			_sbQuery.append(" case when dst.id_user_fk is null then usp.user_name else usr.user_name end as user_name, ");
		    _sbQuery.append(" case when dst.id_user_fk is null then usp.user_email else usr.user_email end as user_email, ");
		    _sbQuery.append(" case when dst.id_user_fk is null then usp.user_fullname else usr.user_fullname  end as user_fullname ");
		    _sbQuery.append("from notification_destinations dst ");
		    _sbQuery.append("left join users usr on dst.id_user_fk = usr.id_user_pk  and usr.user_state = 1 ");
		    _sbQuery.append("left join profiles prf on dst.id_profile_fk = prf.id_profile_pk and prf.profile_state = 1 ");
		    _sbQuery.append("left join users usp on prf.id_profile_pk = usp.id_profile_fk and usp.user_state = 1 ");
		    _sbQuery.append("where dst.id_notification_process_fk = :idNotificationProcess ");
		_sbQuery.append( ") users inner join ");
		_sbQuery.append(" ( select ");
		_sbQuery.append(" distinct nl.id_user_fk ");
		_sbQuery.append(" from ");
		_sbQuery.append(" notification_process np ");
		_sbQuery.append(" inner join notification_logger nl on nl.id_notification_process_fk = np.id_notification_process_pk ");
		_sbQuery.append(" where np.id_notification_process_pk = :idNotificationProcess ");
		_sbQuery.append("  and  nl.notification_date = :notificationDate  and nl.ind_view= 0 ");
		_sbQuery.append(" )user_yet_view  on id_user_fk = users.id_user_pk ");
		
		return jdbcTemplate.query(_sbQuery.toString(), params, new UsersDestinationsRowMapper());
	}
	@Override
	public List<UsersDestinationsTo> getUsersDestinationsYetDontSeenNotificationForPush(Map<String, Object> params) {
		
		StringBuilder _sbQuery = new StringBuilder();
		_sbQuery.append(" Select distinct soc.user_name, users.user_email ,soc.id_session,soc.dashboard_path,soc.state ");
		_sbQuery.append(" from ( select ");
			_sbQuery.append(" case when dst.id_user_fk is null then usp.id_user_pk else usr.id_user_pk end as id_user_pk,   ");
		    _sbQuery.append(" case  when dst.id_user_fk is null then usp.user_name else usr.user_name end as user_name,  ");
		    _sbQuery.append(" case when dst.id_user_fk is null then usp.user_email else usr.user_email end as user_email  ");
		    _sbQuery.append("from notification_destinations dst ");
		    _sbQuery.append("left join users usr on dst.id_user_fk = usr.id_user_pk  and usr.user_state = 1 ");
		    _sbQuery.append("left join profiles prf on dst.id_profile_fk = prf.id_profile_pk and prf.profile_state = 1 ");
		    _sbQuery.append("left join users usp on prf.id_profile_pk = usp.id_profile_fk and usp.user_state = 1 ");
		    _sbQuery.append("where dst.id_notification_process_fk = :idNotificationProcess ");
		_sbQuery.append(") users inner join ");
		_sbQuery.append("( select");
		_sbQuery.append(" distinct nl.id_user_fk ");
		_sbQuery.append(" from ");
		_sbQuery.append(" notification_process np ");
		_sbQuery.append(" inner join notification_logger nl on nl.id_notification_process_fk = np.id_notification_process_pk ");
		_sbQuery.append(" where np.id_notification_process_pk = :idNotificationProcess ");
		_sbQuery.append("  and  nl.notification_date = :notificationDate  and nl.ind_view= 0 ");
		_sbQuery.append(" )user_yet_view  on id_user_fk = users.id_user_pk ");
		_sbQuery.append("  inner join  ");
		_sbQuery.append("  session_socket soc on soc.user_name = users.user_name ");
		_sbQuery.append(" where soc.state = 1  and soc.dashboard_path = '/header'  ");
		
		return jdbcTemplate.query(_sbQuery.toString(), params, new UsersDestinationsRowMapper());
	}


	@Override
	public List<UsersDestinationsTo> getMatrixForSendNotification(Map<String, Object> params) {
		StringBuilder _sbQuery = new StringBuilder();
		
		_sbQuery.append(" Select users.* , ");
		_sbQuery.append(" user_yet_view.count_yet, ");
		_sbQuery.append(" user_seen.count_seen, ");
		_sbQuery.append(" s_socket.id_session  from ( select ");
		    _sbQuery.append(" case when dst.id_user_fk is null then usp.id_user_pk else usr.id_user_pk end as id_user_pk, ");
		    _sbQuery.append(" case when dst.id_user_fk is null then usp.user_name else usr.user_name end as user_name, ");
		    _sbQuery.append(" case when dst.id_user_fk is null then usp.user_email else usr.user_email end as user_email,  ");
		    _sbQuery.append(" case when dst.id_user_fk is null then usp.user_fullname else usr.user_fullname  end as user_fullname , ");
		    _sbQuery.append(" np.notification_repeat ");
		_sbQuery.append(" from notification_destinations dst ");
		_sbQuery.append(" inner join notification_process np on  dst.id_notification_process_fk  =  np.id_notification_process_pk ");
		_sbQuery.append(" left join users usr on dst.id_user_fk = usr.id_user_pk  and usr.user_state = 1 ");
		_sbQuery.append(" left join profiles prf on dst.id_profile_fk = prf.id_profile_pk and prf.profile_state = 1 ");
		_sbQuery.append(" left join users usp on prf.id_profile_pk = usp.id_profile_fk and usp.user_state = 1  ");
		_sbQuery.append(" where dst.id_notification_process_fk = :idNotificationProcess ");
		_sbQuery.append("  ) users left  join (  ");
		_sbQuery.append(" select distinct nl.id_user_fk,  ");
		_sbQuery.append(" count(1) as count_yet  ");
		_sbQuery.append(" from  ");
		_sbQuery.append(" notification_process np ");
		_sbQuery.append(" inner join notification_logger nl on nl.id_notification_process_fk = np.id_notification_process_pk  ");
		_sbQuery.append(" where np.id_notification_process_pk = :idNotificationProcess ");
		_sbQuery.append(" and  nl.notification_date = :notificationDate and nl.ind_view= 0 group by nl.id_user_fk ");
		_sbQuery.append(" )user_yet_view  on id_user_fk = users.id_user_pk  ");
		_sbQuery.append(" left  join ( select distinct nl.id_user_fk as user_fk , count(1) as count_seen ");
		_sbQuery.append("  from  notification_process np   ");
		_sbQuery.append(" inner join notification_logger nl on nl.id_notification_process_fk = np.id_notification_process_pk   ");
		_sbQuery.append(" where np.id_notification_process_pk = :idNotificationProcess and  nl.notification_date = :notificationDate and nl.ind_view=1  ");
		_sbQuery.append(" group by nl.id_user_fk ) user_seen  on user_fk = users.id_user_pk  ");
		_sbQuery.append(" left  join  ");
		_sbQuery.append(" (  select distinct user_name as named,id_session  from ");
		_sbQuery.append(" session_socket soc  ");
		_sbQuery.append(" where  ");
		_sbQuery.append(" soc.state = 1 and  date_trunc('day', soc.register_date) = :notificationDate ");
		_sbQuery.append(" and soc.dashboard_path = :pathHeader ");
		_sbQuery.append(" )s_socket on named = users.user_name  ");
		
		return jdbcTemplate.query(_sbQuery.toString(), params, new UsersDestinationsRowMapper());
	}


}
