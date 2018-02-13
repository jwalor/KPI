package com.arkin.kpi.quartz.model.to;

import java.io.Serializable;

public class SessionSocketTo implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String userName;
	private String idSession;
	private String DashboardPath;
	private String registerDate;
	private String updateDate;
	private int state;
	
	public SessionSocketTo() {
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getIdSession() {
		return idSession;
	}

	public void setIdSession(String idSession) {
		this.idSession = idSession;
	}

	public String getDashboardPath() {
		return DashboardPath;
	}

	public void setDashboardPath(String dashboardPath) {
		DashboardPath = dashboardPath;
	}

	public String getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(String registerDate) {
		this.registerDate = registerDate;
	}

	public String getUpdateDate() {
		return updateDate;
	}

	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}	

}
