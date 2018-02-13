package com.arkin.kpi.quartz.model.type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum NotificationSendType {
	
	TIEMPO(new Integer(1)),
	
	EVENTO(new Integer(0));
	
	/** The Constant list. */
	public final static List<NotificationSendType> list=new ArrayList<NotificationSendType>();
	
	/** The Constant lookup. */
	public final static Map<Integer, NotificationSendType> lookup=new HashMap<Integer, NotificationSendType>();

	static {
		for(NotificationSendType c : NotificationSendType.values()){
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
	private NotificationSendType(Integer code) {
		this.code = code;
	}
	
	/**
	 * Gets the.
	 *
	 * @param codigo the codigo
	 * @return the master table type
	 */
	public static NotificationSendType get(Integer codigo) {
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
