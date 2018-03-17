package com.arkin.kpi.component.service;

import com.arkin.kpi.component.AEvent;

/**
 * 
 * @author jalor
 *
 */
@SuppressWarnings("rawtypes")
public interface IObserver {
	
	 
	public void verifyConections(AEvent event);
}
