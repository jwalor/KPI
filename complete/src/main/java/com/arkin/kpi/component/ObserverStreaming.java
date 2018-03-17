package com.arkin.kpi.component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.arkin.kpi.component.service.IObserver;

public class ObserverStreaming extends Thread implements IObserver{
	private static Log _logger = LogFactory.getLog(ObserverStreaming.class);

	// our incoming events
    private static BlockingQueue<WorkerStreaming> workers = new LinkedBlockingQueue<WorkerStreaming>();    
   
    private static BlockingQueue<AEvent<?>> incoming = new LinkedBlockingQueue<AEvent<?>>();

	@SuppressWarnings("rawtypes")
	@Override
	public synchronized void verifyConections(AEvent event) {
        EventImp evntImpl = (EventImp)event;
        if ( evntImpl.getBehavior() == ConstantWorker.CREATE_WORKER.getValue()){ 
        	WorkerStreaming _worker = existWorker(evntImpl.getConnuid());
        	if (_worker == null){
        		_worker = new WorkerStreaming();
        		_worker.setConnuid(evntImpl.getConnuid());
        		workers.add(_worker);
        	}
        }else if (evntImpl.getBehavior() == ConstantWorker.REMOVE_WORKER.getValue()){ 
        	WorkerStreaming _worker = existWorker(evntImpl.getConnuid());
        	if ( _worker != null){
        		_worker.set_connectionRemoved(true);
        	}
        }
        incoming.add(event);
	}
	
	private WorkerStreaming existWorker(String uid){
        WorkerStreaming _exist = null;
		for (WorkerStreaming worker : workers) {
			if ( worker.getConnuid().equals(uid) ){
				_exist = worker;
				break;
			}
		}
		return _exist;
	}
	
	@Override
    public void run() {
       
		while (true) {
        	try {	
				    _logger.info(" ....................................... Size of Worker Streaming  :"+ workers.size());
        			AEvent<?> evnt = incoming.take();
        			for (WorkerStreaming t : workers) {
					_logger.info(" ....................................... Size of Worker Streaming  :"+ workers.size());
					 if ( t.getConnuid().equalsIgnoreCase(evnt.getConnuid()) ) {
						 synchronized (t) {
                        	 if (!t.isAlive()){
                				 t.start();
                			 }else if ( t.isAlive() ) {
                				 t.notify();
                			 }
						 }
					 }
					     
					}
        	
			} catch (InterruptedException e) {
				_logger.error(e.getMessage());
			}
        }
    }

}
