package com.arkin.kpi.component;

import java.util.Map;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("rawtypes")
public class EventImp extends AEvent<Map>{
	
	public EventImp(Map mapEntity ,String connuid, int   behavior) {
		super(mapEntity,connuid,behavior);
	}
	public EventImp(String connuid, int   behavior) {
		super(connuid,behavior);
	}
}
