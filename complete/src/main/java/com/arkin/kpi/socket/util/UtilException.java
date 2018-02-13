package com.arkin.kpi.socket.util;

import org.springframework.stereotype.Component;

@Component
public class UtilException {
	
	public UtilException() {
	}
	
	public String getSpecificException(Exception e) {
		
		StackTraceElement stack = e.getStackTrace()[0];
		
		return "Error en la clase: " + stack.getClassName() + ", " + "en el metodo: " + stack.getMethodName()
		+ ", " + "en la linea: " + stack.getLineNumber() + "\n" + e.getMessage();
	}
}
