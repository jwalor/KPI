package com.arkin.kpi.socket.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Date;

import org.apache.commons.lang3.math.NumberUtils;

/**
 * <p>Utility class to handle the numbers.</p>
 *
 */
public class NumberUtil extends NumberUtils {

	public static final BigDecimal ONE_THOUSEND = BigDecimal.valueOf(1000);

	public static final BigDecimal ONE_MILLION = BigDecimal.valueOf(1000000);

	/**
	 * @param number
	 * @param precision
	 * @return
	 */
	public static BigDecimal roundBigDecimal(BigDecimal number, int precision) {
		if (number == null)
			return number;
		return number.setScale(precision, RoundingMode.HALF_UP);
	}
	
	/**
	 * 
	 * @param number
	 * @param zeroes
	 * @return
	 */
	public static double roundUP(double number, int zeroes) {
		BigDecimal value = new BigDecimal(number);
		value = value.setScale(zeroes, RoundingMode.UP);
		return value.doubleValue();
	}

	/**
	 * @param value
	 * @return
	 */
	public static boolean isNumber(String value) {
		return NumberUtils.isNumber(value);
	}

	/**
	 * @param value
	 * @return
	 */
	public static double toDouble(String value) {
		return NumberUtils.toDouble(value);
	}

	/**
	 * @param pattern
	 * @param number
	 * @return
	 */
	public static String formatNumber(String pattern, BigDecimal number) {
		DecimalFormat formatter = new DecimalFormat(pattern);
		return formatter.format(number);
	}

	/**
	 * @param decimal
	 * @return
	 */
	public static String decimal2(BigDecimal decimal) {
		if (decimal == null)
			return "";
		return roundBigDecimal(decimal, 2).toString();
	}

	/**
	 * @param decimal
	 * @param decimalDefault
	 * @return
	 */
	public static String decimal2(BigDecimal decimal, BigDecimal decimalDefault) {
		return decimal == null ? decimal2(decimalDefault) : decimal2(decimal);
	}

	/**
	 * @param value
	 * @return
	 */
	public static Long toLong(Object value) {
		if (value instanceof Long)
			return (Long) value;
		if (value instanceof String) {
			if (StringUtil.EMPTY.equals((String) value))
				return null;
			return new Long((String) value);
		}
		if (value instanceof Number)
			return new Long(((Number) value).shortValue());

		return new Long(value.toString());
	}


	/**
	 * @param createdDate
	 * @param sequence
	 * @return
	 */
	public static Long getMessageControl(Date createdDate,Long sequence){		
		String dateFormated = DateUtil.getDateFormatted(createdDate, "yyyyMMddHHmmss");
		String sequenceFormated = StringUtil.leftPad(sequence.toString(), 4, "0");
		return Long.valueOf(dateFormated + sequenceFormated);
	}
	
	public static Integer toInteger(Object value) {
		if (value == null)
			return null;
		if (value instanceof Integer)
			return (Integer) value;
		if (value instanceof String) {
			if ("".equals((String) value))
				return null;
			return new Integer((String) value);
		}

		if (value instanceof Number)
			return new Integer(((Number) value).intValue());
		if (value instanceof BigInteger)
			((BigInteger) value).intValue();		
		if (value instanceof BigDecimal)
			return Integer.valueOf(((BigDecimal)value).intValue());
		
		return new Integer(value.toString());
	}

	public static Float toFloat(Object value) {
		if (value == null)
			return null;
		if (value instanceof Float)
			return (Float) value;
		if (value instanceof String) {
			if ("".equals((String) value))
				return null;
			return new Float((String) value);
		}
		if (value instanceof Number)
			return new Float(((Number) value).floatValue());

		return new Float(value.toString());
	}

	public static Double toDouble(Object value) {
		if (value == null)
			return null;
		if (value instanceof Double)
			return (Double) value;
		if (value instanceof String) {
			if ("".equals((String) value))
				return null;
			return new Double((String) value);
		}
		if (value instanceof Number)
			return new Double(((Number) value).doubleValue());
		if (value instanceof BigDecimal)
			((BigDecimal) value).doubleValue();
		
		return new Double(value.toString());
	}

	public static BigInteger toBigInteger(Object value) {
		if (value == null)
			return null;
		if (value instanceof BigInteger)
			return (BigInteger) value;
		if (value instanceof String) {
			if ("".equals((String) value))
				return null;
			return new BigInteger((String) value);
		}

		return new BigInteger(value.toString());
	}

	public static BigDecimal toBigDecimal(Object value) {
		if (value == null)
			return null;
		if (value instanceof BigDecimal)
			return (BigDecimal) value;
		if (value instanceof String) {
			if ("".equals((String) value))
				return null;
			return new BigDecimal((String) value);
		}
		if (value instanceof Number)
			return new BigDecimal(((Number) value).doubleValue());

		return new BigDecimal(value.toString());
	}
	
	/**
	 * @param value
	 * @return
	 */
	public static String toString(Object value) {
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
	
	public static Integer resolveUnlimitNumber(String value){
		if("0x7fffffff".equals(value)){
			return Integer.MAX_VALUE;
		}else{
			return Integer.parseInt(value);
		}
	}
}
