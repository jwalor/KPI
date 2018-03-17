package com.arkin.kpi.component;

/**
 * 
 * @author jalor
 *
 */
public abstract class AEvent<T> {
	
	private final T      mapEntity ;
//    protected final Object type;
    protected final  int   behavior;
    private final String      connuid;
    
    public AEvent(T mapEntity ,String connuid, int   behavior) {
        this.connuid = connuid;
//        this.type = type;
        this.behavior = behavior;
    	this.mapEntity = mapEntity;
    }
    
    public AEvent(String connuid, int   behavior) {
        this.connuid = connuid;
//        this.type = type;
        this.behavior = behavior;
    	this.mapEntity = null;
    }
	public T getMapEntity() {
		return mapEntity;
	}

	public String getConnuid() {
		return connuid;
	}
	
//
//	public Object getType() {
//		return type;
//	}

	public int getBehavior() {
		return behavior;
	}
	
	
}
