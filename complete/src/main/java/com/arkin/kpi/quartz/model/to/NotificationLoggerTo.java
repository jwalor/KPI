package com.arkin.kpi.quartz.model.to;

public class NotificationLoggerTo {
	
	private Long idNotificationProcess;
	private Long idUser;
	private String notificationName;
	private String notificationSubject;
	private String notification_message;
	private Integer  notificationType;
	private String notificationDate;
	private Integer  destinationType;
	private Integer alertType;
	private Integer notificationState;
	
	public NotificationLoggerTo() {
	}

	public Long getIdNotificationProcess() {
		return idNotificationProcess;
	}

	public void setIdNotificationProcess(Long idNotificationProcess) {
		this.idNotificationProcess = idNotificationProcess;
	}

	public Long getIdUser() {
		return idUser;
	}

	public void setIdUser(Long idUser) {
		this.idUser = idUser;
	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
	}

	public String getNotificationSubject() {
		return notificationSubject;
	}

	public void setNotificationSubject(String notificationSubject) {
		this.notificationSubject = notificationSubject;
	}

	public String getNotification_message() {
		return notification_message;
	}

	public void setNotification_message(String notification_message) {
		this.notification_message = notification_message;
	}

	public Integer getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(Integer notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationDate() {
		return notificationDate;
	}

	public void setNotificationDate(String notificationDate) {
		this.notificationDate = notificationDate;
	}

	public Integer getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(Integer destinationType) {
		this.destinationType = destinationType;
	}

	public Integer getAlertType() {
		return alertType;
	}

	public void setAlertType(Integer alertType) {
		this.alertType = alertType;
	}

	public Integer getNotificationState() {
		return notificationState;
	}

	public void setNotificationState(Integer notificationState) {
		this.notificationState = notificationState;
	}

}
