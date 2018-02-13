package com.arkin.kpi.quartz.job.schedule;

import java.util.ArrayList;
import java.util.List;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arkin.kpi.quartz.job.config.JobProperties;
import com.arkin.kpi.quartz.service.NotificationsService;

import static org.quartz.CronScheduleBuilder.cronSchedule;

@Component
public class JobSchedulerModelGenerator {
	
	public static final String JOB_NAME = "JobName";
    public static final String GROUP_NAME = "Group";
    public static final String DATA_TO_WRITE = "dataToWrite";

    //private JobScheduleProperties jobScheduleProperties;
    
    @Autowired
    private NotificationsService notificationsService;

//    @Autowired
//    public JobSchedulerModelGenerator(JobScheduleProperties jobScheduleProperties) {
//        this.jobScheduleProperties = jobScheduleProperties;
//   }

    public List<JobScheduleModel> generateModels() {
        List<JobProperties> jobs = notificationsService.getJobs();//jobScheduleProperties.getJobs();
        List<JobScheduleModel> generatedModels = new ArrayList<>();
        for (int i = 0; i < jobs.size(); i++) {
            JobScheduleModel model = generateModelFrom(jobs.get(i), Integer.parseInt(jobs.get(i).getIdSchedule()));
            generatedModels.add(model);
        }	
        return generatedModels;
    }

    private JobScheduleModel generateModelFrom(JobProperties job, int jobIndex) {
        JobDetail jobDetail = getJobDetailFor(JOB_NAME + "_"  + jobIndex, GROUP_NAME, job);

        Trigger trigger = getTriggerFor(job.getCronExpression(), jobDetail);
        JobScheduleModel jobScheduleModel = new JobScheduleModel(jobDetail, trigger);
        return jobScheduleModel;
    }

    private JobDetail getJobDetailFor(String jobName, String groupName, JobProperties job) {
        JobDetail jobDetail = JobBuilder.newJob(JobRunner.class)
                .setJobData(getJobDataMapFrom(job.getIdSchedule()))
                .withDescription("Job with data to write : " + job.getIdSchedule() +
                        " and CRON expression : " + job.getCronExpression())
                .withIdentity(jobName, groupName)
                .build();
        return jobDetail;
    }

    private JobDataMap getJobDataMapFrom(String dataToWrite) {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(DATA_TO_WRITE, dataToWrite);
        return jobDataMap;
    }

    private Trigger getTriggerFor(String cronExpression, JobDetail jobDetail) {
        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withSchedule(cronSchedule(cronExpression))
                .build();
        return trigger;
    }
    
    
    
}
