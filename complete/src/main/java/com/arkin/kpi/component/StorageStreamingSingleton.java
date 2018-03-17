package com.arkin.kpi.component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.arkin.kpi.socket.util.MapperUtil;

/**
 * 
 * @author jalor
 *
 */
public class StorageStreamingSingleton {

		public  static ConcurrentMap<String, Object> mapStreaming = new ConcurrentHashMap<>(100);
		private static StorageStreamingSingleton INSTANCE ;
		
		
		public StorageStreamingSingleton() {
		}
		
		public static StorageStreamingSingleton getInstance()  {
	   
	        if (INSTANCE == null) { 
	        
	            synchronized (StorageStreamingSingleton.class) {
	            
	                if (INSTANCE == null) { 
	                	INSTANCE = new StorageStreamingSingleton();
	                    System.out.println("Instance Created");
	                }
	            }
	 
	        }
	        return INSTANCE;
	    }
		
		public synchronized void anadir(String  streamId) {
			
			Object _countStreaming = mapStreaming.get(streamId);
			if (_countStreaming == null) {
				mapStreaming.put(streamId, 1L);
			}else {
				Long _longCount = (Long) _countStreaming;
				mapStreaming.put(streamId, ++_longCount);
			}
		}
		
		public synchronized Map<String, Object> sendToQueue() {
			Map<String, Object> parameters 	= null;
			if (mapStreaming.size() >0){
				parameters 	= MapperUtil.entrySetToMap(mapStreaming.entrySet());
				mapStreaming.clear();
			}
			return parameters;
			
		}
		
}
