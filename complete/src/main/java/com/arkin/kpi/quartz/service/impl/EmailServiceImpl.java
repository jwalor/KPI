package com.arkin.kpi.quartz.service.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.arkin.kpi.quartz.model.to.EmailTemplateTo;
import com.arkin.kpi.quartz.service.EmailService;
import com.arkin.kpi.socket.util.Constantes;
import com.arkin.kpi.socket.util.UtilException;

/**
 * 
 * @author jalor
 *
 */
@Component
public class EmailServiceImpl implements EmailService {

	private static final Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
	
	@Autowired
    public JavaMailSender emailSender;
	
	@Autowired
	private UtilException utilException;
	
	@Override
	public CompletableFuture<Map<String,Object>> sendSimpleMessage(EmailTemplateTo emailTemplate) throws InterruptedException {
		Map<String,Object> map = new HashMap<>();
		
		try {
			
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailTemplate.getTo());
            message.setSubject(emailTemplate.getSubject());
            message.setText(emailTemplate.getText());
            
            emailSender.send(message);
            
			 map.put("isSend", Boolean.TRUE);
	         map.put("user", emailTemplate.getUserDestination());
            
	        return CompletableFuture.completedFuture(map);            
            
        } catch (MailException exception) {        	
        	LOGGER.error(utilException.getSpecificException(exception));         
            map.put("isSend", Boolean.FALSE);
            map.put("user", emailTemplate.getUserDestination());
            emailTemplate.getUserDestination().setStatus(Constantes.NotificationStatus.ERROR_EMAIL);
        	return CompletableFuture.completedFuture(map);
        }
	}

}
