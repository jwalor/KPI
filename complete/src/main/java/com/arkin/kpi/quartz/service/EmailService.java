package com.arkin.kpi.quartz.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.arkin.kpi.quartz.model.to.EmailTemplateTo;

public interface EmailService {	
	CompletableFuture<Map<String,Object>> sendSimpleMessage(EmailTemplateTo emailTemplate) throws InterruptedException;
}
