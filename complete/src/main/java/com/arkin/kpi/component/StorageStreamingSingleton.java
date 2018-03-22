package com.arkin.kpi.component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.arkin.kpi.socket.util.MapperUtil;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings({"rawtypes","unchecked"})
public class StorageStreamingSingleton {

		public  static ConcurrentMap<String, Object> mapStreaming = new ConcurrentHashMap<>();
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
		
		
		public synchronized void anadir(Map  mapEvent , String _streamId) {
			
			Object _countStreaming = mapStreaming.get(_streamId);
			if (_countStreaming == null) {
				Map<String, Object> parameter = new HashMap<>();
				parameter.put("frequently", 1L);
				parameter.put("tableTarget", mapEvent.get("tableTarget"));
				mapStreaming.put(_streamId, parameter);
			}else {
				Map<String, Object> parameter = (Map<String, Object>)_countStreaming;
				Long _longCount = (Long) parameter.get("frequently");
				parameter.put(_streamId, ++_longCount);
				mapStreaming.put(_streamId, parameter);
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
