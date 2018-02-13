package com.arkin.kpi.quartz.job.config;


public class JobProperties {


	private String cronExpression;

	private String idSchedule;
	
	public JobProperties(){
		
	}

	public String getCronExpression() {
		return cronExpression;
	}

	public void setCronExpression(String cronExpression) {
		this.cronExpression = cronExpression;
	}

	public String getIdSchedule() {
		return idSchedule;
	}

	public void setIdSchedule(String idSchedule) {
		this.idSchedule = idSchedule;
	}

}
