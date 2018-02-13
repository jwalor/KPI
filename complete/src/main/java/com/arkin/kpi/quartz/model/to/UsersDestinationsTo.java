package com.arkin.kpi.quartz.model.to;

public class UsersDestinationsTo {
	
	private Long idUserPk;
	private String userName;
	private String userEmail;
	private String userFullname;
	
	public UsersDestinationsTo() {
	}

	public Long getIdUserPk() {
		return idUserPk;
	}

	public void setIdUserPk(Long idUserPk) {
		this.idUserPk = idUserPk;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserEmail() {
		return userEmail;
	}

	public void setUserEmail(String userEmail) {
		this.userEmail = userEmail;
	}

	public String getUserFullname() {
		return userFullname;
	}

	public void setUserFullname(String userFullname) {
		this.userFullname = userFullname;
	}
	
	

}
