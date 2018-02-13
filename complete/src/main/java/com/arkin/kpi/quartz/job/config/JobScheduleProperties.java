package com.arkin.kpi.quartz.job.config;

import java.util.List;

//import javax.validation.Valid;
//import javax.validation.constraints.NotNull;
//import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.factory.annotation.Autowired;

import com.arkin.kpi.quartz.service.NotificationsService;

public class JobScheduleProperties {

	private List<JobProperties> jobs;

	@Autowired
	private NotificationsService notificationsService;
	
	public JobScheduleProperties(){
		
	}

	public List<JobProperties> getJobs() {
		return jobs;
	}

	public void setJobs(List<JobProperties> jobs) {
		this.jobs = jobs;
	}

}
