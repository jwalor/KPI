package com.arkin.kpi.quartz.job.config;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import com.arkin.kpi.component.LectorStreaming;
import com.arkin.kpi.component.service.ExecutorServiceKpi;
import com.arkin.kpi.component.service.impl.ExecutorServiceKpiImpl;


/**
 * 
 * @author jalor
 *
 */
@Configuration
public class SchedulerConfig {

	@Autowired
	private Environment env;

	@Autowired
	LectorStreaming lectorStreaming;

	@Bean
	public JobFactory jobFactory(ApplicationContext applicationContext) {
		AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
		jobFactory.setApplicationContext(applicationContext);
		return jobFactory;
	}

	@Bean
	public SchedulerFactoryBean schedulerFactoryBean(JobFactory jobFactory) throws IOException {
		SchedulerFactoryBean factory = new SchedulerFactoryBean();
		factory.setAutoStartup(true);
		factory.setJobFactory(jobFactory);
		factory.setQuartzProperties(quartzProperties());
		return factory;
	}

	@Bean
	public Properties quartzProperties() throws IOException {
		Properties  properties = new Properties();
		properties.put("org.quartz.scheduler.instanceName", env.getProperty("org.quartz.scheduler.instanceName"));
		properties.put("org.quartz.scheduler.instanceId",env.getProperty("org.quartz.scheduler.instanceId") );
		properties.put("org.quartz.threadPool.threadCount",env.getProperty("org.quartz.threadPool.threadCount") );
		properties.put("org.quartz.jobStore.class",env.getProperty("org.quartz.jobStore.class") );

		return properties;
	}

	@Bean
	public ScheduledExecutorService asyncScheduler(ApplicationContext applicationContext) throws InterruptedException, ExecutionException {
		ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
		int _delay	=  Integer.parseInt(env.getProperty("time.delay"));
		int _interval	=  Integer.parseInt(env.getProperty("time.interval"));

		final ScheduledFuture<?> beeperHandle = scheduler.scheduleWithFixedDelay(this.lectorStreaming, 
				_delay, _interval, TimeUnit.SECONDS);
		return scheduler;
	}
}
