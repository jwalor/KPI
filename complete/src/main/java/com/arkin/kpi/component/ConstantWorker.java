package com.arkin.kpi.component;

public enum ConstantWorker  {
	  
	  EVENT_DEMAND(1),
	  EVENT_SERVER(2),
	  CREATE_WORKER(3),
	  REMOVE_WORKER(4);
	  
	  private int value;    

	  private ConstantWorker(int value) {
	    this.value = value;
	  }

	  public int getValue() {
	    return value;
	  }
}
