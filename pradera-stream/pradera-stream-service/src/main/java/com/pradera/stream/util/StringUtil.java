package com.pradera.stream.util;

import java.text.MessageFormat;

import org.apache.commons.lang3.StringUtils;


/**
 * <p>Utility class to handle the Strings.</p>
 *
 */
public final class StringUtil extends StringUtils {
	
	private StringUtil() {
	}
	
	/**
	 * Return an array of empty arguments.
	 * @return
	 */
	public static String[] emptyArgs() {
		return new String[]{};
	}

	/**
	 * Fill string arguments
	 * @param args
	 * @return
	 */
	public static String[] fillArgs(String... args) {
		return args;
	}
	
	public static String format(String value, String... args) {
		MessageFormat form = new MessageFormat(value);
		return form.format(args);
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static String getFromObject(Object value) {
		if (value instanceof String) {
			if (StringUtil.EMPTY.equals((String) value))
				return null;
			return (String) value;
		}
		if (value instanceof Number){
			return value.toString();				
		}
		return StringUtil.EMPTY;
	}
	
}
