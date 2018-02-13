package com.arkin.kpi.quartz.model.to;

import java.io.Serializable;

public class NotificationTo implements Serializable {

	private static final long serialVersionUID = 1L;

	private String strNotificationName;
	private Integer intNotificationType;
	private Integer intAlertType;
	private String strNotificationSubject;
	private String strNotificationMessage;
	private Integer intNotificationState;
	private ScheduleTo scheduleTo;
	private Long lngScheduleToPk;
	private Integer intNotificationSendType;

	public NotificationTo() {

	}

	public String getStrNotificationName() {
		return strNotificationName;
	}

	public void setStrNotificationName(String strNotificationName) {
		this.strNotificationName = strNotificationName;
	}

	public Integer getIntNotificationType() {
		return intNotificationType;
	}

	public void setIntNotificationType(Integer intNotificationType) {
		this.intNotificationType = intNotificationType;
	}

	public Integer getIntAlertType() {
		return intAlertType;
	}

	public void setIntAlertType(Integer intAlertType) {
		this.intAlertType = intAlertType;
	}

	public String getStrNotificationSubject() {
		return strNotificationSubject;
	}

	public void setStrNotificationSubject(String strNotificationSubject) {
		this.strNotificationSubject = strNotificationSubject;
	}

	public String getStrNotificationMessage() {
		return strNotificationMessage;
	}

	public void setStrNotificationMessage(String strNotificationMessage) {
		this.strNotificationMessage = strNotificationMessage;
	}

	public Integer getIntNotificationState() {
		return intNotificationState;
	}

	public void setIntNotificationState(Integer intNotificationState) {
		this.intNotificationState = intNotificationState;
	}

	public ScheduleTo getScheduleTo() {
		return scheduleTo;
	}

	public void setScheduleTo(ScheduleTo scheduleTo) {
		this.scheduleTo = scheduleTo;
	}

	public Long getLngScheduleToPk() {
		return lngScheduleToPk;
	}

	public void setLngScheduleToPk(Long lngScheduleToPk) {
		this.lngScheduleToPk = lngScheduleToPk;
	}

	public Integer getIntNotificationSendType() {
		return intNotificationSendType;
	}

	public void setIntNotificationSendType(Integer intNotificationSendType) {
		this.intNotificationSendType = intNotificationSendType;
	}
	
}
