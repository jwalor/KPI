package com.arkin.kpi.quartz.rest;

import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.arkin.kpi.quartz.job.schedule.RestQuartzScheduler;

@RestController
@RequestMapping("/quartz")
public class QuartzRestController {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(QuartzRestController.class);
	
	@Autowired
	private RestQuartzScheduler restQuartzScheduler;

	@RequestMapping(value = "createCronQuartz", method = RequestMethod.POST)
	private ResponseEntity<Void> createCronQuartz(@RequestBody Map<String, String> params) {
		try {			
			restQuartzScheduler.runScheduler(params);
		} catch (Exception e) {
			LOGGER.error(e.getMessage());
			return ResponseEntity.badRequest().header("Failure", "Ocurrió un error al ejecutar la expresión cron").build();
		}
		return ResponseEntity.ok().build();
	}

}
