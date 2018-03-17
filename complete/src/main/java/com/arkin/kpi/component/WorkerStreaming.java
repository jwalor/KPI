package com.arkin.kpi.component;

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
@Service
public class WorkerStreaming extends Thread{
	
	private String connuid;
	private boolean _connectionRemoved= false;
	
	@Autowired
	ExecutorServiceKpi	executorServiceKpi;
	
	protected StorageStreamingSingleton STORAGE; 
	
	private static Log _logger = LogFactory.getLog(WorkerStreaming.class);
	
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
			    	STORAGE.anadir(connuid);
			    	wait();
	//				CommandProcessor _processor = CommandProcessor.getInstance();
	//		    	DestroyCommand _command = new DestroyCommand();
	//		    	_command.set_interfaceType(JdbcInterfaceType.CONNECTION);
	//		    	_command.set_uid( new Long(connuid));
	//		    	
	//		    	_logger.info("......  Thread ConnectionWorker should  remove connection with connuid  : "+ connuid + "  if exist");
	//		    	CallingContext _callingContext = new CallingContext();
	//		    	_callingContext.setHasConnectionWorker(true);
	//				_processor.process( new Long(connuid),  new Long(connuid), _command, _callingContext);
					_connectionRemoved = true;
			
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

	public boolean is_connectionRemoved() {
		return _connectionRemoved;
	}

	public void set_connectionRemoved(boolean _connectionRemoved) {
		this._connectionRemoved = _connectionRemoved;
	}
	
}
