package com.arkin.kpi.component;

import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.arkin.kpi.component.service.ExecutorServiceKpi;
import com.arkin.kpi.socket.util.DateUtil;

/**
 * 
 * @author jalor
 *
 */
@Component
public class LectorStreaming extends Thread {
	
	private static Log _logger = LogFactory.getLog(LectorStreaming.class);
	protected StorageStreamingSingleton STORAGE;
	
	@Autowired
	ExecutorServiceKpi	executorServiceKpi;
	
	
	public LectorStreaming() {
		STORAGE = StorageStreamingSingleton.getInstance();
		
	}
	
	@Override
    public void run() {
		
		_logger.info(":::::::::: Starting Lector to execute : processNotificationsRules and processKpiDashBoards at " + DateUtil.currentDateWithTime());
      
		Map<String, Object> mapEntity 	= STORAGE.sendToQueue();
		
		try {
			
			if (mapEntity ==null || mapEntity.isEmpty()) {
				_logger.info(":::::::::: Finishing Lector because Map doesn't have data at  " + DateUtil.currentDateWithTime());
				return ;
			}
			
			///////////////////////////////////////////////////////////////////////////////////
			/**
			*   Processing sync about dashboard's kpis.
			*/
			
			executorServiceKpi.processKpiDashBoards(mapEntity);

			///////////////////////////////////////////////////////////////////////////////////
			/**
			*  Processing async about Rules and notifications.
			*/
			executorServiceKpi.processNotificationsRules(mapEntity);
			
			_logger.info(":::::::::: Finishing Lector to execute : processNotificationsRules and processKpiDashBoards at " + DateUtil.currentDateWithTime());

		} catch (Exception e) {
			_logger.error(e.getMessage());
		}
		          	
     }

}