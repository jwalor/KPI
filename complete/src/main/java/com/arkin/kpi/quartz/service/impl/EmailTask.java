package com.arkin.kpi.quartz.service.impl;

import java.util.concurrent.Callable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;


public class EmailTask implements Callable<String>{

	private String _message;
	
	@Autowired
    public JavaMailSender emailSender;
	
    public EmailTask(String _message) {
		this._message = _message ;
	}
	
	@Override
	public String call() throws Exception {
		
		try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo("");
            message.setSubject("");
            message.setText(this._message);
            
            emailSender.send(message);
        } catch (MailException exception) {
            exception.printStackTrace();
        }finally{
        	System.gc();
        }
		return "SUCCESSFULL";
	}

	public String get_message() {
		return _message;
	}

	public void set_message(String _message) {
		this._message = _message;
	}
	
	
}
