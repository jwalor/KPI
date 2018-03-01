package com.arkin.kpi.socket.util;

import com.arkin.kpi.quartz.model.type.GenericStateType;
import com.arkin.kpi.quartz.model.type.NotificationSendType;

public class Constantes {
	
	public static final Integer SUCCESS = 1;
	public static final Integer ERROR = 0;
	
	public static final String	 userName = "userName";
	public static final String	 idSession = "idSession";
	public static final String	 dashBoard = "dashBoard";
	public static final String	 registerDate = "registerDate";
	public static final String	 updateDate = "updateDate";
	public static final String	 state 		= "state";
	
	public static abstract class TABLES_NAME {

		public final static String NOTIFICATION_PROCESS = "notification_process";
		public final static String SCHEDULE_PROCESS = "schedule_process";
		public final static String RULES = "rules";
		public final static String LOGICAL_TABLES = "logical_tables";
		public final static String LOGICAL_COLUMNS = "logical_columns";
		public final static String PARAMETER_TABLE = "parameter_table";
		public final static String NOTIFICATION_LOGGER = "notification_logger";
		public final static String NOTIFICATIONS_DESTINATIONS = "notification_destinations";
		public final static String SESSION_SOCKET = "session_socket";
		public final static String INTEGRATION_VIEW_COLUMN = "integration_view_column";
		public final static String INTEGRATION_VIEW = "integration_view";
		public final static String INTEGRATION_TABLE = "integration_table";
		public final static String INTEGRATION_TABLE_COLUMN = "integration_table_column";
		public final static String KPI_COLUMNS = "kpi_columns";
		public final static String KPI_COMPONENT = "kpi_component";
		public final static String DASHBOARD_KPIS = "dashboard_kpis";
		public final static String DASHBOARDS = "dashboards";
		public final static String USERS = "users";
		public final static String PROFILES = "profiles";
	
	}
	
	public static abstract class  NotificationStatus {
		
		public static final Integer ERROR_COLA = -1;
		public static final Integer ERROR_EMAIL = 0;
		public static final Integer SUCCESSFUL = 1;
	}
	
	public static abstract class  NotificationLogger {
	
		public final static String ID_NOTIFICATION_PROCESS = "idNotificationProcess";
		public final static String ID_USER = "idUser";
		public final static String NOTIFICATION_NAME = "notificationName";
		public final static String NOTIFICATION_SUBJECT = "notificationSubject";
		public final static String NOTIFICATION_MESSAGE = "notificationMessage";
		public final static String NOTIFICATION_TYPE = "notificationType";
		public final static String NOTIFICATION_DATE = "notificationDate";
		public final static String DESTINATION_TYPE = "destinationType";
		public final static String ALERT_TYPE = "alertType";
		public final static String NOTIFICATION_STATE = "notificationState";
		public final static String IND_VIEW = "indView";
		public final static String REGISTER_DATE = "registerDate";
		public final static String LAST_MODIFY_DATE = "lastModifyDate";
		public final static String LAST_MODIFY_APP = "lastModifyApp";
		public final static String LAST_MODIFY_USER = "lastModifyUser";
		public final static String LAST_MODIFY_IP=  "lastModifyIp";		
		public final static String PATH_HEADER = "PATH_HEADER";
		
	}
	
	public static abstract class Querys {
		
		// Query Notification
		public static final String SELECT_NOTIFICATIONS = " select np.notification_name,np.notification_type,np.alert_type,np.notification_subject, "
				+ " np.notification_message,np.notification_state,np.id_schedule_process_fk,np.notification_send_type "
				+ " from " + TABLES_NAME.NOTIFICATION_PROCESS + " np ";

		public static final String WHERE_NOTIFICATIONS = " where np.notification_state =:notificationState  and np.notification_send_type=:notificationSendType ";
		
		// Query Schedules
		public static final String SELECT_SCHEDULE_WITH_CRONEXPRESIONS = "select sp.id_schedule_process_pk,sp.schedule_hour,sp.schedule_minute,sp.schedule_second,sp.schedule_dayofmonth, "
				+ " sp.schedule_month,sp.schedule_dayofweek,sp.schedule_year,(sp.schedule_second || ' ' || sp.schedule_minute || ' ' || "
				+ " sp.schedule_hour || ' ' || sp.schedule_dayofmonth ||  ' ' || sp.schedule_month || ' ' || '?' || " 
				+ " (case when sp.schedule_year = '*' then '' else ' ' || sp.schedule_year end)) as cronExpresion "
				+ " from " + TABLES_NAME.SCHEDULE_PROCESS + " sp ";

		public static final String WHERE_SCHEDULES_WITH_CRONEXPRESIONS = " where sp.schedule_state =:scheduleState ";
		
		/* Query Rules - Notifications By Event And Schedule */
		
		public static final String SELECT_RULES_NOTIFICATIONS_EVENTS = " select np.id_notification_process_pk,tb.table_name,ru.rule_formule," 
				+ " co.column_name,ru.comparator_ref,ru.value_return_ref, pt.parameter_name, ru.value_notification, "
				+ " np.notification_type, np.notification_subject,np.notification_message,np.notification_name, np.alert_type "
				+ "  from " + TABLES_NAME.NOTIFICATION_PROCESS + " np "
				+ "  inner join " + TABLES_NAME.RULES + " ru "
				+ "		on np.id_rule_fk = ru.id_rule_pk and np.notification_send_type = " + NotificationSendType.EVENTO.getCode()
				+ "  	and ru.rule_state = " + GenericStateType.ACTIVE.getCode()
				+ "  inner join " + TABLES_NAME.LOGICAL_TABLES + " tb "
				+ "	 	on ru.id_logical_table_fk = tb.id_logical_table_pk and tb.table_state = " + GenericStateType.ACTIVE.getCode()
				+ "  inner join " + TABLES_NAME.LOGICAL_COLUMNS + " co "
				+ "	 	on ru.column_name_ref = co.id_logical_columns_pk and co.column_state = " + GenericStateType.ACTIVE.getCode()
				+ "  inner join " + TABLES_NAME.PARAMETER_TABLE + " pt "
				+ " 	on ru.operator_ref = pt.parameter_table_pk and pt.parameter_state = " + GenericStateType.ACTIVE.getCode()
				+ "  left join " + TABLES_NAME.NOTIFICATION_LOGGER + " nl "
				+ "	    on np.id_notification_process_pk = nl.id_notification_process_fk "
			 	+ "     and to_char(nl.notification_date,'DD/MM/YYYY') = to_char(now(),'DD/MM/YYYY') "
				+ "     and nl.notification_state = " + GenericStateType.ACTIVE.getCode()
			 	+ "  where nl.id_notification_process_fk is null ";
		
		public static final String SELECT_RULES_NOTIFICATIONS_EVENTS_BYTABLE = "select np.id_notification_process_pk,tb.table_name,ru.rule_formule, "
				+ " co.column_name,ru.comparator_ref,ru.value_return_ref, pt.parameter_name, ru.value_notification, "
				+ "	np.notification_type, np.notification_subject,np.notification_message,np.notification_name, np.alert_type, np.notification_state, "
				+ " ru.id_rule_pk, ru.rule_name "
				+ " from " + TABLES_NAME.NOTIFICATION_PROCESS + " np " 
				+ " inner join " +  TABLES_NAME.RULES + " ru " 
				+ " 	on np.id_rule_fk = ru.id_rule_pk and np.notification_send_type = " +  NotificationSendType.EVENTO.getCode()
				+ " 	and ru.rule_state =  " + GenericStateType.ACTIVE.getCode()
				+ " inner join " + TABLES_NAME.LOGICAL_TABLES + " tb "
				+ " 	on ru.id_logical_table_fk = tb.id_logical_table_pk and tb.table_state = " + GenericStateType.ACTIVE.getCode()
				+ " inner join " + TABLES_NAME.LOGICAL_COLUMNS + " co "
				+ " 	on ru.column_name_ref = co.id_logical_columns_pk and co.column_state = " + GenericStateType.ACTIVE.getCode()
				+ " inner join " + TABLES_NAME.INTEGRATION_VIEW_COLUMN + " ivc "
				+ " 	on co.id_integration_view_column_fk = ivc.id_integration_view_column_pk "
				+ " inner join " +  TABLES_NAME.INTEGRATION_VIEW + " ivt "
				+ " 	on ivc.id_integration_view_fk = ivt.id_integration_view_pk "
				+ " inner join " + TABLES_NAME.INTEGRATION_TABLE + " itb " 
				+ " 	on  ivt.id_integration_table_fk = itb.id_integration_table_pk "
				+ " inner join " + TABLES_NAME.PARAMETER_TABLE + " pt "
				+ " 	on ru.operator_ref = pt.parameter_table_pk and pt.parameter_state = " + GenericStateType.ACTIVE.getCode()
				+ " where itb.table_name =:tableTarget ";
		
		public static final String SELECT_RULES_NOTIFICATIONS_BYSCHEDULE = " select np.id_notification_process_pk,tb.table_name,ru.rule_formule, "
				+ " co.column_name,ru.comparator_ref,ru.value_return_ref, pt.parameter_name, ru.value_notification, "
				+ "	np.notification_type, np.notification_subject,np.notification_message,np.notification_name, np.alert_type, np.notification_state, "
				+ " ru.id_rule_pk, ru.rule_name "
				+ "	from " + TABLES_NAME.SCHEDULE_PROCESS + " sp " 
				+ "	inner join " +  TABLES_NAME.NOTIFICATION_PROCESS + " np "
				+ " 	on sp.id_schedule_process_pk = np.id_schedule_process_fk and np.notification_state = " + GenericStateType.ACTIVE.getCode()
				+ " inner join " + TABLES_NAME.RULES + " ru "
				+ " 	on np.id_rule_fk = ru.id_rule_pk and ru.rule_state = " + GenericStateType.ACTIVE.getCode()
				+ "	inner join " + TABLES_NAME.LOGICAL_TABLES + " tb "
				+ " 	on ru.id_logical_table_fk = tb.id_logical_table_pk and tb.table_state = " + GenericStateType.ACTIVE.getCode()
				+ " inner join " + TABLES_NAME.LOGICAL_COLUMNS + " co "
				+ " 	on ru.column_name_ref = co.id_logical_columns_pk and co.column_state = " + GenericStateType.ACTIVE.getCode()
				+ " inner join " + TABLES_NAME.PARAMETER_TABLE + " pt " 
				+ " 	on ru.operator_ref = pt.parameter_table_pk and pt.parameter_state = " + GenericStateType.ACTIVE.getCode()
				+ " where sp.id_schedule_process_pk =:idSchedule and sp.schedule_state = " + GenericStateType.ACTIVE.getCode();
		
		/* Query Rules - Notifications By Event And Schedule */
		
		/* Query Notifications Destinations - Destinations to notify */
		
		public static final String SELECT_NOTIFICATION_DESTINATIONS = "select nd.id_notification_destination_pk,nd.id_notification_process_fk,nd.destination_type, "
				+ " nd.id_user_fk,nd.id_profile_fk "
				+ " from " + TABLES_NAME.NOTIFICATIONS_DESTINATIONS + " nd "
				+ " where nd.id_notification_process_fk =:idNotificationProcess and nd.destination_state = " + GenericStateType.ACTIVE.getCode();
	
		/* Query Notifications Destinations - Destinations to notify */
		
		/* Querys Dashboard */
		
		public static final String SELECT_DASHBOARD_KPIS_BY_TABLE = " select distinct dbo.path_dashboard,dkp.id_dashboard_kpi_pk as id_kpi_component_pk, kpc.kpi_name "
				+ " from " + TABLES_NAME.INTEGRATION_TABLE + " itb " 
				+ " inner join " + TABLES_NAME.INTEGRATION_TABLE_COLUMN + " itc " 
				+ " 	on itb.id_integration_table_pk = itc.id_integration_table_fk and itb.table_name =:tableTarget "
				+ " inner join " + TABLES_NAME.INTEGRATION_VIEW + " itv " 
				+ " 	on itb.id_integration_table_pk = itv.id_integration_table_fk " 
				+ "	inner join " + TABLES_NAME.INTEGRATION_VIEW_COLUMN + " ivc " 
				+ " 	on itv.id_integration_view_pk = ivc.id_integration_view_fk " 
				+ "     and itc.id_integration_table_column_pk = ivc.id_integration_table_column_fk " 
				+ " inner join " + TABLES_NAME.LOGICAL_COLUMNS + " lgc " 
				+ " 	on ivc.id_integration_view_column_pk = lgc.id_integration_view_column_fk " 
				+ " inner join " + TABLES_NAME.KPI_COLUMNS + " kpi " 
				+ " 	on lgc.id_logical_columns_pk = kpi.id_logical_columns_fk " 
				+ " inner join " + TABLES_NAME.KPI_COMPONENT + " kpc " 
				+ " 	on kpi.id_kpi_component_fk = kpc.id_kpi_component_pk " 
				+ " inner join " + TABLES_NAME.DASHBOARD_KPIS + " dkp " 
				+ " 	on	kpc.id_kpi_component_pk = dkp.id_kpi_component_fk " 
				+ " inner join " + TABLES_NAME.DASHBOARDS + " dbo " 
				+ " 	on dkp.id_dashboards_fk = dbo.id_dashboards_pk "; 
		
		public static final String SELECT_SESSION_SOCKET = "select soc.id,soc.user_name,soc.id_session,soc.dashboard_path,to_char(soc.register_date,'dd/mm/yyyy') as register_date,to_char(soc.update_date,'dd/mm/yyyy') as update_date,soc.state "
				+ " from " + TABLES_NAME.SESSION_SOCKET +" soc " 
				+ " where soc.state = " + GenericStateType.ACTIVE.getCode();
		
		public static final String SELECT_SESSION_SOCKET_BY_USER = "select soc.id,soc.user_name,soc.id_session,soc.dashboard_path,to_char(soc.register_date,'dd/mm/yyyy') as register_date,to_char(soc.update_date,'dd/mm/yyyy') as update_date,soc.state "
				+ " from " + TABLES_NAME.SESSION_SOCKET +" soc " 
				+ " where soc.state = " + GenericStateType.ACTIVE.getCode() 
				+ " and soc.user_name =:userName and dashboard_path =:path ";
		 		
		/* Querys Dashboard */
		
		/* INSERT INTO NotificationLogger */
		
		public static final String INSERT_INTO_NOTIFICATION_LOGGER = "INSERT INTO " + TABLES_NAME.NOTIFICATION_LOGGER +"(id_notification_logger_pk,id_notification_process_fk,id_user_fk,notification_name," 
				+ " notification_subject,notification_message,notification_type,notification_date,destination_type, " 
				+ " alert_type,notification_state,ind_view,register_date,last_modify_date,last_modify_app, " 
				+ " last_modify_user,last_modify_ip) "
				+ " VALUES(NEXTVAL('SQ_ID_NOTIFICATION_LOGGER_PK'), :idNotificationProcess, :idUser, :notificationName, "
				+ " :notificationSubject, :notificationMessage, :notificationType, :notificationDate, :destinationType, "
				+ " :alertType, :notificationState, :indView , :registerDate, :lastModifyDate, :lastModifyApp,"
				+ " :lastModifyUser, :lastModifyIp )"; 
		
		/* NotificationLogger*/
		
		/* Users destinations */
		
		public final static String SELECT_USERS_DESTINATIONS_BY_NOTIFICATION = "" 
				+ " select distinct "
				+ "	case "
				+ " 	when dst.id_user_fk is null then usp.id_user_pk "
				+ " 	else usr.id_user_pk "
				+ " end as id_user_pk, "
				+ " case "
				+ " 	when dst.id_user_fk is null then usp.user_name "
				+ " 	else usr.user_name "
				+ "	end as user_name, "
				+ " case "
				+ " 	when dst.id_user_fk is null then usp.user_email "
				+ " 	else usr.user_email "
				+ " end as user_email, "
				+ " case "
				+ "		when dst.id_user_fk is null then usp.user_fullname "
				+ " 	else usr.user_fullname "
				+ " end as user_fullname "
				+ " from " + TABLES_NAME.NOTIFICATIONS_DESTINATIONS + " dst "
				+ " left join " + TABLES_NAME.USERS + " usr "
				+ " 	on dst.id_user_fk = usr.id_user_pk  and usr.user_state = " + GenericStateType.ACTIVE.getCode()
				+ " left join " + TABLES_NAME.PROFILES +  " prf " 
				+ " 	on dst.id_profile_fk = prf.id_profile_pk and prf.profile_state = " + GenericStateType.ACTIVE.getCode()
				+ " left join " + TABLES_NAME.USERS +  " usp "
				+ " 	on prf.id_profile_pk = usp.id_profile_fk and usp.user_state = " + GenericStateType.ACTIVE.getCode()
				+ " where dst.id_notification_process_fk =:idNotificationProcess ";
		
		/* Users destinations */
		
		/* Notifications Logger By User */
		
		public final static String SELECT_NOTIFICATION_LOGGER_BY_USER = " select distinct ntl.id_notification_process_fk,ntl.id_user_fk,ntl.notification_name,ntl.notification_subject, "
				+ " ntl.notification_message,ntl.notification_type,to_char(notification_date,'dd/mm/yyyy') as notification_date, " 
				+ " ntl.destination_type,ntl.alert_type, ntl.notification_state " 
				+ " from " + TABLES_NAME.NOTIFICATION_LOGGER + " ntl "
				+ " inner join "  + TABLES_NAME.USERS + " usu "
				+ " 	on ntl.id_user_fk = usu.id_user_pk and usu.user_name =:userName " 
				+ " where ntl.notification_state = " + GenericStateType.INACTIVE.getCode();
		
		/* Notifications Logger By User */
			
	}
}
