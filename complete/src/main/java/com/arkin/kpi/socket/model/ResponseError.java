package com.arkin.kpi.socket.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.arkin.kpi.socket.util.Constant;


public class ResponseError implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Integer errorCode;
	private String httpStatus;
	private String errorMessage;
	private String rootErrorMessage;
	private List<ErrorBean> errorList;
	
	public ResponseError() {
		errorCode = Constant.SUCCESS;
		errorMessage = null;
		errorList = new ArrayList<ErrorBean>();
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getHttpStatus() {
		return httpStatus;
	}

	public void setHttpStatus(String httpStatus) {
		this.httpStatus = httpStatus;
	}

	public String getErrorMessage() {
		return errorMessage;
	}
	
	public String getRootErrorMessage() {
		return rootErrorMessage;
	}

	public void setRootErrorMessage(String rootErrorMessage) {
		this.rootErrorMessage = rootErrorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public List<ErrorBean> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<ErrorBean> errorList) {
		this.errorList = errorList;
	}
	
	/**
	 * Utility method that add an error to the list, but initializing it if is
	 * required
	 * 
	 * @param errorCode
	 * @param errorMessage
	 */
	public void addError(Integer errorCode, String errorMessage) {
		addError(new ErrorBean(errorCode, errorMessage));
		errorCode = Constant.ERROR;
	}
	
	public void addError(ErrorBean error){
		getErrorList().add(error);
	}
}
