package com.arkin.kpi.quartz.model.to;

import com.arkin.kpi.socket.util.Constantes;

public class UsersDestinationsTo {
	
	private Long idUserPk;
	private String userName;
	private String userEmail;
	private String userFullname;
	private Long quantityRepeat;
	private Long quantityYet;
	private Long quantitySeen;
	private String idSession;
	private Integer status;
	
	public UsersDestinationsTo() {
		/**
		 *  Setting value by default.
		 */
		this.status = Constantes.NotificationStatus.SUCCESSFUL;
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

	public Long getQuantityRepeat() {
		return quantityRepeat;
	}

	public void setQuantityRepeat(Long quantityRepeat) {
		this.quantityRepeat = quantityRepeat;
	}

	public Long getQuantityYet() {
		return quantityYet;
	}

	public void setQuantityYet(Long quantityYet) {
		this.quantityYet = quantityYet;
	}

	public Long getQuantitySeen() {
		return quantitySeen;
	}

	public void setQuantitySeen(Long quantitySeen) {
		this.quantitySeen = quantitySeen;
	}

	public String getIdSession() {
		return idSession;
	}

	public void setIdSession(String idSession) {
		this.idSession = idSession;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}
	
	

}
