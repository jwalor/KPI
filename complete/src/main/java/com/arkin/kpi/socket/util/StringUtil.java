package com.arkin.kpi.socket.util;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;


/**
 * <p>Utility class to handle the Strings.</p>
 *
 */
public final class StringUtil extends StringUtils {
	
	public static String regexSocket = "([a-zA-Z]+)([0-9]*)";

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
	
	public static String getValueByPattern(String value) {
		Pattern pattern = Pattern.compile(regexSocket);
        Matcher matcher = pattern.matcher(value);
        if (matcher.find()) {
           return matcher.group();
        }
        return "";
	}
}
