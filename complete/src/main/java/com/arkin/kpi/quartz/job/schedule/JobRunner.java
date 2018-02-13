package com.arkin.kpi.quartz.job.schedule;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;

import com.arkin.kpi.quartz.service.impl.SomeService;


@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class JobRunner implements Job{
	 
    @Autowired
    private SomeService someService;
	
	@Override
	public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
		JobKey jobKey = jobExecutionContext.getJobDetail().getKey();
				
		String[] jobNames = jobKey.getName().split("_");
		int idSchedule = Integer.parseInt(jobNames[1]);
		
		someService.SendNotifications(idSchedule);
	}

}
