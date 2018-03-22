package com.arkin.kpi.component;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.arkin.kpi.component.service.ExecutorServiceKpi;



/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("rawtypes")
@Service
public class WorkerStreaming extends Thread{
	
	private static Log _logger = LogFactory.getLog(WorkerStreaming.class);

	private String connuid;
	public Map mapEvent;
	protected StorageStreamingSingleton STORAGE; 
	
	@Autowired
	ExecutorServiceKpi	executorServiceKpi;
	
	public WorkerStreaming() {
		STORAGE = StorageStreamingSingleton.getInstance();
	}
	
	@Override
	public void run() {
		_logger.info("...... Started Thread WorkerStreaming # : "+ connuid +".... ");
		
		while (true) {        	
			try {
				synchronized (this) {
			    	_logger.info("......  Thread WorkerStreaming started with connuid  : "+ connuid + ".........");
			    	STORAGE.anadir(mapEvent,connuid);
			    	wait();
				}	
			} catch (InterruptedException e) {
				_logger.error(e.getCause());
			}
		}	
	}

	public String getConnuid() {
		return connuid;
	}

	public void setConnuid(String connuid) {
		this.connuid = connuid;
	}

	
	public Map getMapEvent() {
		return mapEvent;
	}

	public void setMapEvent(Map mapEvent) {
		this.mapEvent = mapEvent;
	}
	
	
}
