package com.arkin.kpi.quartz.model.to;

public class NotificationDestinationsTo {
		
	private Long idNotificationDestination;
	private Long idNotificationProcess;
	private Integer destinationType;
	private Long idUserPk;
	private Long idProfilePk;
	
	public NotificationDestinationsTo() {
	}

	public Long getIdNotificationDestination() {
		return idNotificationDestination;
	}

	public void setIdNotificationDestination(Long idNotificationDestination) {
		this.idNotificationDestination = idNotificationDestination;
	}

	public Long getIdNotificationProcess() {
		return idNotificationProcess;
	}

	public void setIdNotificationProcess(Long idNotificationProcess) {
		this.idNotificationProcess = idNotificationProcess;
	}

	public Integer getDestinationType() {
		return destinationType;
	}

	public void setDestinationType(Integer destinationType) {
		this.destinationType = destinationType;
	}

	public Long getIdUserPk() {
		return idUserPk;
	}

	public void setIdUserPk(Long idUserPk) {
		this.idUserPk = idUserPk;
	}

	public Long getIdProfilePk() {
		return idProfilePk;
	}

	public void setIdProfilePk(Long idProfilePk) {
		this.idProfilePk = idProfilePk;
	}	
	
}
