package com.arkin.kpi.socket.model;

import java.io.Serializable;

public class ErrorBean implements Serializable{
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Integer errorCode;
	String errorMessage;
	String errorType;
	
	public ErrorBean() {
	}

	public ErrorBean(Integer errorCode, String errorMessage){
		setErrorCode(errorCode);
		setErrorMessage(errorMessage);
	}
	
	public ErrorBean(Integer errorCode, String errorMessage, String errorType){
		setErrorCode(errorCode);
		setErrorMessage(errorMessage);
		setErrorType(errorType);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((errorCode == null) ? 0 : errorCode.hashCode());
		result = prime * result
				+ ((errorMessage == null) ? 0 : errorMessage.hashCode());
		result = prime * result
				+ ((errorType == null) ? 0 : errorType.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ErrorBean other = (ErrorBean) obj;
		if (errorCode == null) {
			if (other.errorCode != null)
				return false;
		} else if (!errorCode.equals(other.errorCode))
			return false;
		if (errorMessage == null) {
			if (other.errorMessage != null)
				return false;
		} else if (!errorMessage.equals(other.errorMessage))
			return false;
		if (errorType == null) {
			if (other.errorType != null)
				return false;
		} else if (!errorType.equals(other.errorType))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ErrorBean [errorCode=" + errorCode + ", errorMessage="
				+ errorMessage + ", errorMessage=" + errorType + "]";
	}

	public String getErrorType() {
		return errorType;
	}

	public void setErrorType(String errorType) {
		this.errorType = errorType;
	}
	
	
}
