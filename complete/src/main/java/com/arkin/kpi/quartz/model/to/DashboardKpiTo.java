package com.arkin.kpi.quartz.model.to;

import java.io.Serializable;

public class DashboardKpiTo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String pathDashboard;
	private Long idKpiComponent;
	private String kpiName;
	
	public DashboardKpiTo() {
	}
	
	public DashboardKpiTo(String pathDashboard,Long idKpiComponent,String kpiName) {
		this.pathDashboard = pathDashboard;
		this.idKpiComponent = idKpiComponent;
		this.kpiName = kpiName;
	}

	public String getPathDashboard() {
		return pathDashboard;
	}

	public void setPathDashboard(String pathDashboard) {
		this.pathDashboard = pathDashboard;
	}

	public Long getIdKpiComponent() {
		return idKpiComponent;
	}

	public void setIdKpiComponent(Long idKpiComponent) {
		this.idKpiComponent = idKpiComponent;
	}

	public String getKpiName() {
		return kpiName;
	}

	public void setKpiName(String kpiName) {
		this.kpiName = kpiName;
	}		

}
