package com.arkin.kpi.quartz.model.type;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public enum NotificationType {
	
	APLICATION(new Integer(38)),
	
	EMAIL(new Integer(39)),
	
	EMAIL_APLICATION(new Integer(40));
	
	/** The Constant list. */
	public final static List<NotificationType> list=new ArrayList<NotificationType>();
	
	/** The Constant lookup. */
	public final static Map<Integer, NotificationType> lookup=new HashMap<Integer, NotificationType>();

	static {
		for(NotificationType c : NotificationType.values()){
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
	private NotificationType(Integer code) {
		this.code = code;
	}
	
	/**
	 * Gets the.
	 *
	 * @param codigo the codigo
	 * @return the master table type
	 */
	public static NotificationType get(Integer codigo) {
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
