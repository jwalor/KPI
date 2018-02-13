package com.arkin.kpi.quartz.job.schedule;

import java.util.Map;

import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.arkin.kpi.quartz.model.type.GenericStateType;
import com.arkin.kpi.socket.util.UtilException;


@Component
public class RestQuartzScheduler {

	@Autowired
	private UtilException utilException;

	private static final Logger LOGGER = LoggerFactory.getLogger(RestQuartzScheduler.class);

	private SchedulerFactoryBean schedulerFactoryBean;
	private RestJobSchedulerModelGenerator restJobSchedulerModelGenerator;

	@Autowired
	public RestQuartzScheduler(SchedulerFactoryBean schedulerFactoryBean,
			RestJobSchedulerModelGenerator restJobSchedulerModelGenerator) {
		this.schedulerFactoryBean = schedulerFactoryBean;
		this.restJobSchedulerModelGenerator = restJobSchedulerModelGenerator;
	}

	public void runScheduler(Map<String, String> params) {
		Scheduler scheduler = schedulerFactoryBean.getScheduler();

		try {
			Integer isUpdate = Integer.parseInt(params.get("isUpdate"));
			String idSchedule = params.get("idSchedule");
			if (isUpdate.equals(GenericStateType.ACTIVE.getCode())) {
				JobKey jobKey = new JobKey("JobName" + "_" + idSchedule,"Group");
				boolean isDeleted = scheduler.deleteJob(jobKey);
				System.out.println("se ha eliminado: "+ isDeleted);
			}
		} catch (SchedulerException e) {
			LOGGER.error(utilException.getSpecificException(e));
		}

		JobScheduleModel jobScheduleModel = restJobSchedulerModelGenerator.generateModels(params);

		try {
			scheduler.scheduleJob(jobScheduleModel.getJobDetail(), jobScheduleModel.getTrigger());
		} catch (SchedulerException e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
		try {
			scheduler.start();
		} catch (SchedulerException e) {
			LOGGER.error(utilException.getSpecificException(e));
		}
	}
}
