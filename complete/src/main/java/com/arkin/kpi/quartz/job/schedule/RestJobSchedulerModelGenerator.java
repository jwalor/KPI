package com.arkin.kpi.quartz.job.schedule;

import static org.quartz.CronScheduleBuilder.cronSchedule;

import java.util.Map;

import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.stereotype.Component;

import com.arkin.kpi.quartz.job.config.JobProperties;


@Component
public class RestJobSchedulerModelGenerator {

	public static final String JOB_NAME = "JobName";
	public static final String GROUP_NAME = "Group";
	public static final String ID_SCHEDULE = "idSchedule";

	public JobScheduleModel generateModels(Map<String, String> params) {

		// Obtenemos la expresion cron y la escala se va a ejecutar
		String cronExpression = params.get("cronExpressions");
		String idSchedule = params.get("idSchedule");

		JobProperties job = new JobProperties();
		job.setCronExpression(cronExpression);
		job.setIdSchedule(idSchedule);

		JobScheduleModel model = generateModelFrom(job,
				Integer.parseInt(idSchedule));
		return model;
	}

	private JobScheduleModel generateModelFrom(JobProperties job, int jobIndex) {
		
		JobDetail jobDetail = getJobDetailFor(JOB_NAME + "_" + jobIndex,
				GROUP_NAME, job);

		Trigger trigger = getTriggerFor(job.getCronExpression(), jobDetail);
		JobScheduleModel jobScheduleModel = new JobScheduleModel(jobDetail,
				trigger);
		return jobScheduleModel;
	}

	private JobDetail getJobDetailFor(String jobName, String groupName,
			JobProperties job) {
		JobDetail jobDetail = JobBuilder
				.newJob(JobRunner.class)
				.setJobData(getJobDataMapFrom(job.getIdSchedule()))
				.withDescription(
						"Job with data to write : " + job.getIdSchedule()
								+ " and CRON expression : "
								+ job.getCronExpression())
				.withIdentity(jobName, groupName).build();
		return jobDetail;
	}

	private JobDataMap getJobDataMapFrom(String idSchedule) {
		JobDataMap jobDataMap = new JobDataMap();
		jobDataMap.put(ID_SCHEDULE, idSchedule);
		return jobDataMap;
	}

	private Trigger getTriggerFor(String cronExpression, JobDetail jobDetail) {
		Trigger trigger = TriggerBuilder.newTrigger().forJob(jobDetail)
				.withSchedule(cronSchedule(cronExpression)).build();
		return trigger;
	}

}
