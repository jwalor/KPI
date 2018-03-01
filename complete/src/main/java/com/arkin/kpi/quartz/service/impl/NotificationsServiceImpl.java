package com.arkin.kpi.quartz.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.arkin.kpi.component.service.ComponentNotification;
import com.arkin.kpi.quartz.dao.NotificationsDao;
import com.arkin.kpi.quartz.job.config.JobProperties;
import com.arkin.kpi.quartz.model.to.EmailTemplateTo;
import com.arkin.kpi.quartz.model.to.NotificationLoggerTo;
import com.arkin.kpi.quartz.model.to.NotificationTo;
import com.arkin.kpi.quartz.model.to.RulesNotificationsTo;
import com.arkin.kpi.quartz.model.to.ScheduleTo;
import com.arkin.kpi.quartz.model.to.SessionSocketTo;
import com.arkin.kpi.quartz.model.to.UsersDestinationsTo;
import com.arkin.kpi.quartz.model.type.GenericStateType;
import com.arkin.kpi.quartz.model.type.NotificationType;
import com.arkin.kpi.quartz.service.EmailService;
import com.arkin.kpi.quartz.service.NotificationsService;
import com.arkin.kpi.socket.util.Constantes;
import com.arkin.kpi.socket.util.UtilException;


/**
 * 
 * @author jalor
 *
 */
@Service
@Transactional
public class NotificationsServiceImpl implements NotificationsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationsServiceImpl.class);

	@Autowired
	private NotificationsDao notificationsDao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UtilException utilException;
	
	@Autowired
	private ComponentNotification componentNotification;
	
	@Value("${path.users.active}")
	private String pathUserActive;

	@Override
	public List<NotificationTo> getNotifications(Map<String, Integer> params) {
		return notificationsDao.getNotifications(params);
	}

	@Override
	public List<ScheduleTo> getSchedules(Map<String, Integer> params) {
		return notificationsDao.getSchedules(params);
	}

	@Override
	public List<JobProperties> getJobs() {

		try {
			Map<String, Integer> params = new HashMap<String, Integer>();
			params.put("scheduleState", GenericStateType.ACTIVE.getCode());
			List<ScheduleTo> schedules = notificationsDao.getSchedules(params);

			List<JobProperties> jobs = new ArrayList<JobProperties>();

			for (ScheduleTo schedule : schedules) {
				JobProperties jobProperties = new JobProperties();
				jobProperties.setCronExpression(schedule.getCronExpression());
				jobProperties.setIdSchedule(schedule.getIdSchedule().toString());

				jobs.add(jobProperties);
			}
			return jobs;
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
			return null;
		}
	}
	
	
	private void sendToNotifyByEmail(RulesNotificationsTo ruleNotification, String valueToNotify,List<UsersDestinationsTo> users) throws InterruptedException, ExecutionException {
		
		String message = "";
		message = "se ejecuto la siguiente regla: " + ruleNotification.getRuleFormule();
		message = message + "\n el resultado coincide con el valor a notificar: " + valueToNotify;
		
		List<CompletableFuture<Map<String,Object>>> completableFutures = new ArrayList<CompletableFuture<Map<String,Object>>>();
		
		for(UsersDestinationsTo user : users) {
			String email = user.getUserEmail();
			String subject = ruleNotification.getNotificationSubject();
			
			EmailTemplateTo emailTemplate = new EmailTemplateTo(email,subject,message,user);
			
			CompletableFuture<Map<String,Object>> future = emailService.sendSimpleMessage(emailTemplate);
			completableFutures.add(future);
		}		
		
		CompletableFuture.allOf( completableFutures.toArray((new CompletableFuture[completableFutures.size()]))).join();
		
		//Verificando envio de correos satisfactorios
		for(CompletableFuture<Map<String,Object>> future : completableFutures) {
			
			Map<String,Object> mapFuture = new HashMap<>();
			mapFuture = future.get();
			
			boolean isSend = Boolean.parseBoolean(mapFuture.get("isSend").toString());
			UsersDestinationsTo userDestination = (UsersDestinationsTo) mapFuture.get("user");
			
			if(!isSend) {
				LOGGER.error("Falló envio de notificación al usuario : " + userDestination.getUserName() + "\n NotificaciÃ³n: " 
						+ ruleNotification.getNotificationName() +  "\n Regla : " + ruleNotification.getIdRulePk() + "-" + ruleNotification.getRuleName());	
			}
		}
	}
	
	private void saveNotificationLogger(RulesNotificationsTo ruleNotification, List<UsersDestinationsTo> users, Integer notificationType) {
		
		try {
			for(UsersDestinationsTo user : users) {
				
				Map<String, Object> params = new HashMap<>();
				params.put(Constantes.NotificationLogger.ID_NOTIFICATION_PROCESS, ruleNotification.getLngNotificationProcessPk());
				params.put(Constantes.NotificationLogger.ID_USER, user.getIdUserPk());
				params.put(Constantes.NotificationLogger.NOTIFICATION_NAME, ruleNotification.getNotificationName());
				params.put(Constantes.NotificationLogger.NOTIFICATION_SUBJECT, ruleNotification.getNotificationSubject());
				params.put(Constantes.NotificationLogger.NOTIFICATION_MESSAGE, ruleNotification.getNotificationMessage());
				params.put(Constantes.NotificationLogger.NOTIFICATION_TYPE, notificationType);
				params.put(Constantes.NotificationLogger.DESTINATION_TYPE, notificationType);
				params.put(Constantes.NotificationLogger.ALERT_TYPE, ruleNotification.getAlertType());
				params.put(Constantes.NotificationLogger.NOTIFICATION_STATE, notificationType.equals(NotificationType.EMAIL.getCode()) ? GenericStateType.ACTIVE.getCode() : GenericStateType.INACTIVE.getCode());
								
				notificationsDao.saveNotificationLogger(params);			
			}
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}		
	}

	@Override
	@Transactional
	public void getRulesNotificationsByEventsAndTable(Map<String, String> params) {
		
		try {
			
			List<RulesNotificationsTo> rules = new ArrayList<RulesNotificationsTo>();

			rules = notificationsDao.getRulesNotificationsByEventsAndTable(params);

			if (rules.size() > 0) {

				/////////////////////////////////////////////////////////////////////////////////////////
				executingRulesAndNotifications(rules);
				/////////////////////////////////////////////////////////////////////////////////////////
			}
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
		
	}
	
	@Override
	@Transactional
	public void getRulesNotificationsBySchedule(Map<String, Integer> params) {
		
		try {
			List<RulesNotificationsTo> rules = new ArrayList<RulesNotificationsTo>();

			rules = notificationsDao.getRulesNotificationsBySchedule(params);

			if (rules.size() > 0) {
				
				/////////////////////////////////////////////////////////////////////////////////////////
				executingRulesAndNotifications(rules);
				/////////////////////////////////////////////////////////////////////////////////////////	
				
			}
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
	}
	
	/**
	 *  Method allow verify and send each  notification rule.
	 * @param rules
	 * @return
	 */
	@Transactional
	private void  executingRulesAndNotifications(List<RulesNotificationsTo> rules) {
		
		try {
			
			List<CompletableFuture<Map<String,Object>>> completableFutures = new ArrayList<CompletableFuture<Map<String,Object>>>();
			
			for (RulesNotificationsTo ruleNotification : rules) {
				
				CompletableFuture<Map<String,Object>> future = componentNotification.verifyRulesAndNotifyByAsync(ruleNotification);
				completableFutures.add(future);
			}
			
			CompletableFuture.allOf( completableFutures.toArray((new CompletableFuture[completableFutures.size()]))).join();
			
			/* Verificando ejecucion correcta */
			
			for(CompletableFuture<Map<String,Object>> future : completableFutures) {
				
				Map<String,Object> mapFuture = new HashMap<>();
				mapFuture = future.get();
				
				boolean isSend = Boolean.parseBoolean(mapFuture.get("isSend").toString());
				boolean isMeetRule = Boolean.parseBoolean(mapFuture.get("isMeetRule").toString());
				RulesNotificationsTo ruleNotif = (RulesNotificationsTo) mapFuture.get("ruleNotification");
				boolean allSeen = mapFuture.get("allSeen") !=null? Boolean.parseBoolean(mapFuture.get("allSeen").toString()):false;
				
				if (allSeen) {
					LOGGER.info("No se envió " + "\n Notificación: " 
							+ ruleNotif.getNotificationName() +  "\n Regla : " + ruleNotif.getIdRulePk() + "-" + ruleNotif.getRuleName()
							+ " debido a que los usuarios asociados ya revisaron las notficaciones.");
					break;
				}
				
				if(!isSend && isMeetRule) {
					LOGGER.error("Falló envío de notificación " + "\n Notificación: " 
							+ ruleNotif.getNotificationName() +  "\n Regla : " + ruleNotif.getIdRulePk() + "-" + ruleNotif.getRuleName());	
				}
			}
						
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}		
		
	}	
	
	@Override
	public List<NotificationLoggerTo> getNotificationLoggerByUser(Map<String, String> params) {
		return notificationsDao.getNotificationLoggerByUser(params);
	}
}
