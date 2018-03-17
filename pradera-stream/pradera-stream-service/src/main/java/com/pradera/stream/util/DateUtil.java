package com.pradera.stream.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang3.time.DateUtils;

/**
 * <p>
 * DateUtils contains a lot of common methods considering manipulations of Dates or Calendars. Some methods require some extra explanation.
 * </p>
 *
 */
@SuppressWarnings("deprecation")
public class DateUtil extends DateUtils {

	public static final String FORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String PATTERN_ONLY_DATE = "yyyy-MM-dd";
	public static final String PATTERN_ONLY_TIME = " HH:mm:ss";
	public static final String PATTERN_LOCAL_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public static final String TIME_INITIAL_HOUR = " 00:00:00";
	public static final String TIME_END_HOUR = " 23:59:00";
	public static final String DATE_UUUUMMDDHHMMSS = "uuuuMMddHHmmss";
	public static final String DATE_UUUUMMDDHHMM = "uuuuMMddHHmm";
	public static final String DATE_UUUUMMDD = "uuuuMMdd";
	public static final String DATE_UUUU_MM_DD = "uuuu-MM-dd";
	public static final String DATE_YYYYMMDDHHMM = "yyyyMMddHHmm";
	public static final String DATE_YYYYMMDDHHMMSS = "yyyyMMddHHmmss";
	
	
	private DateUtil() {
	}

	/**
	 * @return
	 */
	public static Date getSystemDate() {
		return new Date(System.currentTimeMillis());
	}
	
	/**
	 * @return
	 */
	public static Date getSystemTimestamp() {
		return new Timestamp(System.currentTimeMillis());
	}
	
	public static java.sql.Date getSystemDBDate() {
		return new java.sql.Date(System.currentTimeMillis());
	}
	
	/**
	 * @param dateUser
	 * @param template
	 * @return
	 */
	public static String getDateFormatted(Date dateUser, String template) {
		String newTemplate = template;
		if (dateUser == null) {
			return StringUtil.EMPTY;
		}
		if (newTemplate == null || newTemplate.equals(StringUtil.EMPTY)) {
			newTemplate = "MM/dd/yyyy";
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat(newTemplate);
		return dateFormat.format(dateUser);
	}

	/**
	 * @param date
	 * @param pattern
	 * @return
	 */
	public static Date getDate(String date, String pattern) {
		SimpleDateFormat formatDate = new SimpleDateFormat();
		formatDate.applyPattern(pattern);
		try {
			return formatDate.parse(date);
		}
		catch (ParseException e) {
			return new Date(date);
		}
	}

	/**
	 * @param date
	 * @param firstHour
	 * @return
	 */
	public static Date getFirstEndHourforDay(Date date, boolean firstHour) {
		String partDate = getDateFormatted(date, PATTERN_ONLY_DATE);
		partDate += firstHour ? TIME_INITIAL_HOUR : TIME_END_HOUR;
		return getDate(partDate, PATTERN_ONLY_DATE + PATTERN_ONLY_TIME);
	}

	/**
	 * @param dateUser
	 * @param format
	 * @return
	 */
	public static String getDateStringFormatted(Date dateUser, String format) {
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		return dateFormat.format(dateUser);
	}

	/**
	 * @param timePoint
	 * @param time
	 * @param timeType
	 * @return
	 */
	public static LocalDateTime dateTimePlusTime(LocalDateTime timePoint, Long time, int timeType) {
		LocalDateTime triggerTime = null;

		if (GregorianCalendar.SECOND == timeType) {
			triggerTime = timePoint.plusSeconds(time);
		}
		if (GregorianCalendar.MINUTE == timeType) {
			triggerTime = timePoint.plusMinutes(time);
		}
		if (GregorianCalendar.HOUR == timeType) {
			triggerTime = timePoint.plusHours(time);
		}
		if (GregorianCalendar.MONTH == timeType) {
			triggerTime = timePoint.plusMonths(time);
		}
		return triggerTime;
	}
	
	/**
	 * @param timePoint
	 * @param time
	 * @param timeType
	 * @return
	 */
	public static LocalDateTime dateTimeMinusTime(LocalDateTime timePoint, Long time, int timeType) {
		LocalDateTime triggerTime = null;

		if (GregorianCalendar.SECOND == timeType) {
			triggerTime = timePoint.minusSeconds(time);
		}
		if (GregorianCalendar.MINUTE == timeType) {
			triggerTime = timePoint.minusMinutes(time);
		}
		if (GregorianCalendar.HOUR == timeType) {
			triggerTime = timePoint.minusHours(time);
		}
		if (GregorianCalendar.MONTH == timeType) {
			triggerTime = timePoint.minusMonths(time);
		}
		return triggerTime;
	}

	/**
	 * @param time
	 * @param timeType
	 * @return
	 */
	public static LocalDateTime dateTimePlusCurrentTime(Long time, int timeType) {
		LocalDateTime timePoint = LocalDateTime.now();
		return DateUtil.dateTimePlusTime(timePoint, time, timeType);
	}

	/**
	 * @param dateTime
	 * @param pattern
	 * @return
	 */
	public static LocalDateTime getDateTimeFromString(String dateTime, String pattern) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return LocalDateTime.parse(dateTime, formatter);
	}
	
	/**
	 * @param pattern
	 * @return
	 */
	public static String getCurrentDateTime(String pattern){
		LocalDateTime dateTime = LocalDateTime.now();
		return DateUtil.getDateTime(dateTime, pattern);
	}
	
	/**
	 * @param dateTime
	 * @param pattern
	 * @return
	 */
	public static String getDateTime(LocalDateTime dateTime,String pattern){
		return dateTime.format(DateTimeFormatter.ofPattern(pattern));
	}	
	
	public static long dateToLongTime(Date date){
		long endDateMilliseconds = date.getTime();
		return endDateMilliseconds / 1000;
	}
	
	public static Date getStartOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);  
		calendar.set(Calendar.MINUTE, 0);  
		calendar.set(Calendar.SECOND, 0);  
		calendar.set(Calendar.MILLISECOND, 0);		
		return calendar.getTime();
	}

	public static Date getEndOfDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 23);  
		calendar.set(Calendar.MINUTE, 59);  
		calendar.set(Calendar.SECOND, 59);		
		return calendar.getTime();
	}
	
}
