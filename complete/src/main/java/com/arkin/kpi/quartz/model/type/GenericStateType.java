package com.arkin.kpi.quartz.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum GenericStateType {
	
	ACTIVE(Integer.valueOf(1),"ACTIVE"),
	INACTIVE(Integer.valueOf(0),"INACTIVE");
	
	
	private Integer code;
	private String value;
	
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	private GenericStateType(Integer code, String value) {
		this.code = code;
		this.value = value;
	}
	
	public static String getValueWithCode(Integer code) {
		String valor = lookup.get(code).value;
		return (valor!=null)?valor:"NO ENCONTRADO";
	}
	
	public static final List<GenericStateType> list = new ArrayList<GenericStateType>();
	public static final Map<Integer, GenericStateType> lookup = new HashMap<Integer, GenericStateType>();
	static {
        for (GenericStateType d : GenericStateType.values()){
            lookup.put(d.getCode(), d);
        	list.add(d);
        }
    }
	
}