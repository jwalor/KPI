package com.arkin.kpi.component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

@Service
public abstract class ControlStreaming {
	
	@Autowired
	private static Environment env;
	
	private static final ScheduledExecutorService scheduler =
		     Executors.newScheduledThreadPool(1);
		 
	public static void beepForAnHour() {		    
		    
	 int _delay	=  Integer.parseInt(env.getProperty("time.delay"));
	 int _interval	=  Integer.parseInt(env.getProperty("time.interval"));
	 
	 final ScheduledFuture<?> beeperHandle = scheduler.scheduleWithFixedDelay(new LectorStreaming(), 
			 _delay, _interval, TimeUnit.SECONDS);
	 
	 if (beeperHandle.isDone()) {
		 System.out.println("ssssssssss");
	 }
//		     scheduler.schedule(new Runnable() {
//		         public void run() { 
//		        	 System.out.println("died");
//		        	 beeperHandle.cancel(true);
//		        	 }
//		       }, 10, TimeUnit.SECONDS);
	 	
	  }     
}
