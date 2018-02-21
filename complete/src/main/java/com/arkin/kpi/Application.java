package com.arkin.kpi;


import static org.ehcache.config.builders.CacheManagerBuilder.newCacheManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.Configuration;
import org.ehcache.xml.XmlConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

import com.arkin.kpi.quartz.model.to.DashboardKpiTo;

@EnableCaching
@SpringBootApplication
@EnableAsync
public class Application   {
	
	    
	private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);	    
	
    public static void main(String[] args) {
    	
        SpringApplication.run(Application.class, args);
    }
    
    @SuppressWarnings("rawtypes")
	@Bean
    public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
    	
    	 LOGGER.info("Creating cache manager via XML resource ");
 	    Configuration xmlConfig = new XmlConfiguration(Application.class.getResource("/ehcache.xml"));
 	    try (CacheManager cacheManager = newCacheManager(xmlConfig)) {
 	      cacheManager.init();    	      
 	      
 	      Cache<Long, ArrayList> basicCache = cacheManager.getCache("basicCache", Long.class, ArrayList.class);
 	      List<DashboardKpiTo> dash = new ArrayList<>();
 	      dash.add(new DashboardKpiTo("pathDashboard",new Long("12"),"kpiName"));
 	          	      
 	      LOGGER.info("Putting to cache");
 	      basicCache.put(1L, (ArrayList) dash);
 	      Object value = basicCache.get(1L);
 	      LOGGER.info("Retrieved '{}'", value);

 	      LOGGER.info("Closing cache manager");
 	    }
    	
        return args -> {

            System.out.println("Let's inspect the beans provided by Spring Boot:");

            String[] beanNames = ctx.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            for (String beanName : beanNames) {
                System.out.println(beanName);
            }

        };
    }
    

    
}
