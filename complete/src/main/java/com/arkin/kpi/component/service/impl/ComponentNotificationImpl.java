package com.arkin.kpi.component.service.impl;

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
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.arkin.kpi.component.service.ComponentNotification;
import com.arkin.kpi.quartz.dao.NotificationsDao;
import com.arkin.kpi.quartz.model.to.EmailTemplateTo;
import com.arkin.kpi.quartz.model.to.RulesNotificationsTo;
import com.arkin.kpi.quartz.model.to.RulesResultTo;
import com.arkin.kpi.quartz.model.to.UsersDestinationsTo;
import com.arkin.kpi.quartz.model.type.NotificationType;
import com.arkin.kpi.quartz.service.EmailService;
import com.arkin.kpi.socket.util.Constantes;
import com.arkin.kpi.socket.util.DateUtil;
import com.arkin.kpi.socket.util.JsonUtils;
import com.arkin.kpi.socket.util.StringUtil;
import com.arkin.kpi.socket.util.UtilException;


/**
 * 
 * @author jalor
 *
 */
@Component
@Transactional
@SuppressWarnings({"unchecked","rawtypes"})
public class ComponentNotificationImpl implements ComponentNotification {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComponentNotificationImpl.class);

	@Autowired
	private NotificationsDao notificationsDao;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UtilException utilException;

	@Value("${path.users.active}")
	private String pathUserActive;
	
	@Value("${queue.users}")
	private String queueUsers;
	
	
	@Autowired
	private SimpMessagingTemplate brokerMessagingTemplate;

	/**
	 * 
	 *  @RulesNotificationsTo  Object that content attributes about rule and notifications.</br>  
	 *  Verify each rule.So that if the rule is affirmative then should notify to users involve .
	 */
	@Async
	public CompletableFuture<Map<String, Object>> verifyRulesAndNotifyByAsync(RulesNotificationsTo ruleNotification){

		Map<String,Object> map = new HashMap<>();

		StringBuilder _sbRule = new StringBuilder();
		_sbRule.append(" SELECT DISTINCT * FROM  ( ").append(" Select").append(ruleNotification.getRuleFormule()).append( " as rules_result from ");
		_sbRule.append(ruleNotification.getTableName()).append(" ) R  WHERE UPPER(R.RULES_RESULT) NOT LIKE 'NOTHING'");

		boolean isMeetRule = false;
		map.put("ruleNotification", ruleNotification);

		try {

			/**
			 *  Filter for getting users notifications.
			 */
			Map<String, Object> params_users = new HashMap<String, Object>();
			params_users.put("idNotificationProcess", ruleNotification.getLngNotificationProcessPk());
			params_users.put("notificationDate", DateUtil.currentDate());
			params_users.put("pathHeader", pathUserActive);

			/**
			 *  1.- In this section , verify if exist some user that  haven't yet seen the result of current rule.
			 *  
			 */

			List<UsersDestinationsTo> usersDestinations 	= notificationsDao.getMatrixForSendNotification(params_users);
			List<UsersDestinationsTo> filterUsersDestinations = verifyCanSendNotification(usersDestinations);

			if ( filterUsersDestinations.size() == 0 ) {

				map.put("isSend", Boolean.FALSE);
				map.put("isMeetRule", isMeetRule);
				map.put("allSeen", Boolean.TRUE);
				return CompletableFuture.completedFuture(map); 

			}

			/**
			 *  2.- In this section , verify if  the result of current rule is positive. In this case, we assume that
			 *  	yet exist user pending  to read the notification associated to the rule.
			 */

			List<RulesResultTo> rulesResult = notificationsDao.getRulesResult(_sbRule.toString());


			for (RulesResultTo result : rulesResult) {

				if (result.getResult().compareTo(ruleNotification.getValueToNotify()) == 0) {

					isMeetRule = true;

					/**
					 *  3.- In this section , getting all user that haven't yet seen the notification.
					 *  	After, it'll send some kind of notificaTion: EMAIL , APPLICATION or both.
					 *  
					 */	
					List<CompletableFuture<Map<String,Object>>>  listCompletableFuture = null;	

					try {								

						if (ruleNotification.getNotificationType().equals(NotificationType.EMAIL.getCode())) {
							listCompletableFuture = sendToNotifyByEmail(ruleNotification, ruleNotification.getValueToNotify(), filterUsersDestinations );
							usersDestinations = tranformUsersDestinations(listCompletableFuture, ruleNotification);
							
							// send queue
							sendingNotificationPush( usersDestinations, ruleNotification );
						}else

							if (ruleNotification.getNotificationType().equals(NotificationType.EMAIL_APLICATION.getCode())) {
								listCompletableFuture = sendToNotifyByEmail(ruleNotification, ruleNotification.getValueToNotify(), filterUsersDestinations );
							    usersDestinations = tranformUsersDestinations(listCompletableFuture, ruleNotification);
								// send queue
								sendingNotificationPush( usersDestinations, ruleNotification );
							}
							else
								if (ruleNotification.getNotificationType().equals(NotificationType.APLICATION.getCode())) {
									// send queue
								 sendingNotificationPush( filterUsersDestinations , ruleNotification );
								 usersDestinations	= filterUsersDestinations;
							}
						
					} catch (Exception e) {
						LOGGER.error(utilException.getSpecificException(e));
						map.put("isSend", Boolean.FALSE);
						map.put("isMeetRule", isMeetRule);
					} finally{
						saveNotificationLogger( ruleNotification, usersDestinations, ruleNotification.getNotificationType());
					}									
				}
			}
			
			if ( map.get("isSend")==null ) {
				map.put("isSend", Boolean.TRUE);			
				map.put("isMeetRule", isMeetRule);
			}

			return CompletableFuture.completedFuture(map); 

		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
			map.put("isSend", Boolean.FALSE);		
			map.put("isMeetRule", isMeetRule);
			return CompletableFuture.completedFuture(map); 
		}
	}

	/**
	 * 
	 * @param ruleNotification
	 * @param valueToNotify
	 * @param users
	 * @param usersPush
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private List<CompletableFuture<Map<String,Object>>>   sendToNotifyByEmail(RulesNotificationsTo ruleNotification, String valueToNotify,List<UsersDestinationsTo> users )
			throws InterruptedException, ExecutionException {

		String message = "";
		message = "Se ejecutó la siguiente regla: " + ruleNotification.getRuleFormule();
		message = message + "\n el resultado coincide con el valor a notificar: " + valueToNotify;

		List<CompletableFuture<Map<String,Object>>> completableFutures = new ArrayList<CompletableFuture<Map<String,Object>>>();

		EmailTemplateTo emailTemplate = new EmailTemplateTo();
		for(UsersDestinationsTo user : users) {
			String email = user.getUserEmail();
			String subject = ruleNotification.getNotificationSubject();

			emailTemplate.setTo(email);
			emailTemplate.setSubject(subject);
			emailTemplate.setText(message);
			emailTemplate.setUserDestination(user);
			CompletableFuture<Map<String,Object>> future = emailService.sendSimpleMessage(emailTemplate);

			completableFutures.add(future);
		}		

		CompletableFuture.allOf( completableFutures.toArray((new CompletableFuture[completableFutures.size()]))).join();	
		return completableFutures;
	}



	/**
	 * 
	 * @param completableFutures
	 * @param ruleNotification
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	private  List<UsersDestinationsTo>   tranformUsersDestinations( List<CompletableFuture<Map<String,Object>>> completableFutures , RulesNotificationsTo ruleNotification ) throws InterruptedException, ExecutionException {

		List<UsersDestinationsTo> userDestinationsToList = new ArrayList<>();
		Map<String,Object> mapFuture = new HashMap<>();
		
		for(CompletableFuture<Map<String,Object>> future : completableFutures) {

			mapFuture = future.get();
			boolean isSend = Boolean.parseBoolean(mapFuture.get("isSend").toString());
			UsersDestinationsTo userDestination = (UsersDestinationsTo) mapFuture.get("user");

			if(!isSend) {			
				LOGGER.error("Falló envio de notificación al usuario : " + userDestination.getUserName() + "\n Notificación: " 
						+ ruleNotification.getNotificationName() +  "\n Regla : " + ruleNotification.getIdRulePk() + "-" + ruleNotification.getRuleName());
			}
			userDestinationsToList.add(userDestination);
		}
		return userDestinationsToList;
	}

	private void saveNotificationLogger(RulesNotificationsTo ruleNotification, List<UsersDestinationsTo> users, Integer notificationType)  {

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
				params.put(Constantes.NotificationLogger.NOTIFICATION_STATE, user.getStatus());
				params.put(Constantes.NotificationLogger.IND_VIEW, ruleNotification.getIndView());		
				notificationsDao.saveNotificationLogger(params);			
			}
		} catch (Exception e) {
			LOGGER.error(utilException.getSpecificException(e));
		}		
	}
	
	
	private  void sendingNotificationPush( List<UsersDestinationsTo> userDestinations, RulesNotificationsTo ruleNotification ) {
		
		String  _idSession = StringUtil.EMPTY;
		
		/**
		 *  Setting data over map variable that it'ill be to sent on queue trough session id.
		 */
		
		Map map =  new HashMap();
		map.put("notificationId", ruleNotification.getLngNotificationProcessPk());
		map.put("notificationMessage", ruleNotification.getNotificationMessage());
		map.put("notificationType", ruleNotification.getNotificationType());
		
		List list = new ArrayList<>();
		list.add(map);
		
		for (UsersDestinationsTo usersDestinationsTo : userDestinations) {
			
			try {
				
				_idSession = (usersDestinationsTo.getIdSession()!=null)
							 ?usersDestinationsTo.getIdSession() : _idSession ;
				sentMessageForSessionId(_idSession, queueUsers , list);
			} catch (Exception e) {
				LOGGER.error("Falla al enviar mensaje de notificación al ususario conectado: " + usersDestinationsTo.getUserFullname()
								+" con sessión : " + usersDestinationsTo.getIdSession());
				usersDestinationsTo.setStatus(Constantes.NotificationStatus.ERROR_COLA);
			}finally {
				if (usersDestinationsTo.getStatus()!=Constantes.NotificationStatus.SUCCESSFUL ) {
					LOGGER.debug("Se envió mensaje de notificación al ususario : " + usersDestinationsTo.getUserFullname()
					+" con sessión : " + usersDestinationsTo.getIdSession());
				}
			}
		}
	}
	
	public void sentMessageForSessionId(String idSession,String queue, Object message) throws  MessagingException {

		/**
		 *  Sending message to queue for session id.
		 */
		SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.create(SimpMessageType.MESSAGE);
		headerAccessor.setSessionId(idSession);
		headerAccessor.setLeaveMutable(true);
		this.brokerMessagingTemplate.convertAndSendToUser(idSession, queue, 
				JsonUtils.toJson(message), headerAccessor.getMessageHeaders());

	}

	/**
	 * 
	 * @param userDestinations
	 * @return
	 */
	private  List<UsersDestinationsTo> verifyCanSendNotification( List<UsersDestinationsTo> userDestinations ) {
		List<UsersDestinationsTo> filterUserDestinations =  new ArrayList<>();

		for (UsersDestinationsTo usersDestinationsTo : userDestinations) {
			if (usersDestinationsTo.getQuantitySeen() == 0 && usersDestinationsTo.getQuantityYet() < usersDestinationsTo.getQuantityRepeat()) {
				filterUserDestinations.add(usersDestinationsTo);
			}
		}

		return filterUserDestinations;
	}


}
