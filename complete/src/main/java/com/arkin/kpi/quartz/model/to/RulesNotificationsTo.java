package com.arkin.kpi.quartz.model.to;

public class RulesNotificationsTo {
	
	private Long lngNotificationProcessPk;
	private String tableName;
	private String ruleFormule;
	private String columnName;
	private String comparatorRef;
	private String valueReturnRef;
	private String parameterName;
	private String valueToNotify;
	private Integer notificationType;
	private String notificationSubject;
	private String notificationMessage;
	private String notificationName;
	private Integer alertType;
	private Integer notificationState;
	private Integer idRulePk;
	private String ruleName;
		
	public RulesNotificationsTo(){
	}
	
	public Long getLngNotificationProcessPk() {
		return lngNotificationProcessPk;
	}

	public void setLngNotificationProcessPk(Long lngNotificationProcessPk) {
		this.lngNotificationProcessPk = lngNotificationProcessPk;
	}
	
	public String getTableName() {
		return tableName;
	}
	
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	public String getRuleFormule() {
		return ruleFormule;
	}

	public void setRuleFormule(String ruleFormule) {
		this.ruleFormule = ruleFormule;
	}

	public String getColumnName() {
		return columnName;
	}
	
	public void setColumnName(String columnName) {
		this.columnName = columnName;
	}
	
	public String getComparatorRef() {
		return comparatorRef;
	}
	
	public void setComparatorRef(String comparatorRef) {
		this.comparatorRef = comparatorRef;
	}
	
	public String getValueReturnRef() {
		return valueReturnRef;
	}
	
	public void setValueReturnRef(String valueReturnRef) {
		this.valueReturnRef = valueReturnRef;
	}
	
	public String getParameterName() {
		return parameterName;
	}
	
	public void setParameterName(String parameterName) {
		this.parameterName = parameterName;
	}

	public String getValueToNotify() {
		return valueToNotify;
	}

	public void setValueToNotify(String valueToNotify) {
		this.valueToNotify = valueToNotify;
	}	

	public Integer getNotificationType() {
		return notificationType;
	}

	public void setNotificationType(Integer notificationType) {
		this.notificationType = notificationType;
	}

	public String getNotificationSubject() {
		return notificationSubject;
	}

	public void setNotificationSubject(String notificationSubject) {
		this.notificationSubject = notificationSubject;
	}

	public String getNotificationMessage() {
		return notificationMessage;
	}

	public void setNotificationMessage(String notificationMessage) {
		this.notificationMessage = notificationMessage;
	}

	public String getNotificationName() {
		return notificationName;
	}

	public void setNotificationName(String notificationName) {
		this.notificationName = notificationName;
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

	public Integer getIdRulePk() {
		return idRulePk;
	}

	public void setIdRulePk(Integer idRulePk) {
		this.idRulePk = idRulePk;
	}

	public String getRuleName() {
		return ruleName;
	}

	public void setRuleName(String ruleName) {
		this.ruleName = ruleName;
	}

	@Override
	public String toString() {
		return "RulesNotificationsTo [lngNotificationProcessPk="
				+ lngNotificationProcessPk + ", tableName=" + tableName
				+ ", ruleFormule=" + ruleFormule + ", columnName=" + columnName
				+ ", comparatorRef=" + comparatorRef + ", valueReturnRef="
				+ valueReturnRef + ", parameterName=" + parameterName
				+ ", valueToNotify=" + valueToNotify + "]";
	}	
	
}
