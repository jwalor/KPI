package com.arkin.kpi.component;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.arkin.kpi.component.service.IObserver;

/**
 * 
 * @author jalor
 *
 */
public class EventBus {
	
	  // our observers
    private static HashMap<IObserver, Class<?>> m_Observers = new HashMap<IObserver, Class<?>>();
    // our incoming events
    private static BlockingQueue<AEvent<?>> incoming = new LinkedBlockingQueue<AEvent<?>>();
    
    // start our internal thread
    static {
        new Thread(new DelegationThread()).start();
    }
    
	private static Log _logger = LogFactory.getLog(EventBus.class);

 // subscribe an observer
    public static void subscribe(IObserver obs, Class<?> evtClass) {
        synchronized (m_Observers) {
            m_Observers.put(obs, evtClass);
        }
    }

    // publish and event
    public static void publishAsync(AEvent<?> event) {
    	 EventImp evntImpl = (EventImp)event;
    	_logger.info(" ...... Sending event to EventBus with next info :");
    	_logger.info(" ....................................... Map Streaming :" + evntImpl.getMapEntity());
        incoming.add(event);
    }
    
    private static class DelegationThread implements Runnable {
       
    	@Override
        public void run() {
            while (true) {
                try {
                    AEvent<?> evnt = incoming.take();
                    synchronized (m_Observers) {
                        for (Entry<IObserver, Class<?>> entry : m_Observers.entrySet()) {
                            if (entry.getValue() == evnt.getClass()) {
                                entry.getKey().verifyConections(evnt);
                            }
                        }
                    }
                }
                catch (InterruptedException e) {
                	_logger.error(e.getCause());
                }
            }
        }
    }
}
