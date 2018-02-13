package com.arkin.kpi.quartz.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum AlertType {
	
	WARNING(new Integer(111)),	
	SUCCESS(new Integer(112)),	
	INFO(new Integer(113)),
	ERROR(new Integer(114)),
	PRIMARY(new Integer(115));
	
	/** The Constant list. */
	public final static List<AlertType> list=new ArrayList<AlertType>();
	
	/** The Constant lookup. */
	public final static Map<Integer, AlertType> lookup=new HashMap<Integer, AlertType>();

	static {
		for(AlertType c : AlertType.values()){
			lookup.put(c.getCode(), c);
			list.add( c );
		}
	}
	
	/** The code. */
	private Integer code;
	
	/** The str code. */
	private String strCode;
	
	/**
	 * Instantiates a new master table type.
	 *
	 * @param code the code
	 */
	private AlertType(Integer code) {
		this.code = code;
	}
	
	/**
	 * Gets the.
	 *
	 * @param codigo the codigo
	 * @return the master table type
	 */
	public static AlertType get(Integer codigo) {
        return lookup.get(codigo);
    }

	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public Integer getCode() {
		return this.code;
	}
	
	/**
	 * Gets the str code.
	 *
	 * @return the strCode
	 */
	public String getStrCode() {
		return strCode;
	}
	
	/**
	 * Sets the str code.
	 *
	 * @param strCode the strCode to set
	 */
	public void setStrCode(String strCode) {
		this.strCode = strCode;
	}

}
