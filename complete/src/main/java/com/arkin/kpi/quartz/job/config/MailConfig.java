package com.arkin.kpi.quartz.job.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.mail.SimpleMailMessage;

@Configuration
public class MailConfig {
	
	@Bean
    public SimpleMailMessage templateSimpleMessage() {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setText("Esta es la plantilla de email de prueba para su email:\n%s\n");
        return message;
    }	
}
