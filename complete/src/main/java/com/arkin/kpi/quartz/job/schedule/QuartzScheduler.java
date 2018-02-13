package com.arkin.kpi.quartz.job.schedule;

import java.util.List;

import javax.annotation.PostConstruct;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class QuartzScheduler {
	
	private SchedulerFactoryBean schedulerFactoryBean;
    private JobSchedulerModelGenerator jobSchedulerModelGenerator;
 
    @Autowired
    public QuartzScheduler(SchedulerFactoryBean schedulerFactoryBean, JobSchedulerModelGenerator jobSchedulerModelGenerator) {
        this.schedulerFactoryBean = schedulerFactoryBean;
        this.jobSchedulerModelGenerator = jobSchedulerModelGenerator;
    }
 
    @PostConstruct
    public void init() {
        scheduleJobs();
    }
 
    public void scheduleJobs() {
        Scheduler scheduler = schedulerFactoryBean.getScheduler();
        List<JobScheduleModel> jobScheduleModels = jobSchedulerModelGenerator.generateModels();
        for (JobScheduleModel model : jobScheduleModels) {
            try {
                scheduler.scheduleJob(model.getJobDetail(), model.getTrigger());
            } catch (SchedulerException e) {
                // log the error
            }
        }
        try {
            scheduler.start();
        } catch (SchedulerException e) {
            // log the error
        }
    }

}
