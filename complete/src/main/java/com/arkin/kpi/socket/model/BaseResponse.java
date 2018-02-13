package com.arkin.kpi.socket.model;

import java.io.Serializable;

public class BaseResponse<T> implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private T response;
	private ResponseError error = new ResponseError();
	
	public T getResponse() {
		return response;
	}

	public void setResponse(T response) {
		this.response = response;
	}

	public ResponseError getError() {
		return error;
	}

	public void setError(ResponseError error) {
		this.error = error;
	}

	
}
