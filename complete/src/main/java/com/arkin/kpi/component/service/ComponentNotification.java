package com.arkin.kpi.component.service;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import com.arkin.kpi.quartz.model.to.RulesNotificationsTo;

/**
 * 
 * @author jalor
 *
 */
public interface ComponentNotification {
	
	public CompletableFuture<Map<String, Object>> verifyRulesAndNotifyByAsync(RulesNotificationsTo ruleNotification);
	public void sentMessageForSessionId(String idSession,String queue, Object message) ;
	
}
