package com.arkin.kpi.quartz.job.config;

import java.io.IOException;
import java.util.Properties;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.PropertiesFactoryBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

@Configuration
public class SchedulerConfig {
	
	//public static final String QUARTZ_PROPERTIES_PATH = "/quartz.properties";
    @Autowired
    private Environment env;
    
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

}
