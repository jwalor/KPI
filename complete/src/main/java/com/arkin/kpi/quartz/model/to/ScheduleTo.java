package com.arkin.kpi.quartz.model.to;

import java.io.Serializable;

public class ScheduleTo implements Serializable{
		
		private static final long serialVersionUID = 1L;

		private Long idSchedule; 		
		private String scheduleHour;		
		private String scheduleMinute;		
		private String scheduleSecond;
		private String scheduleDayOfMonth;		
		private String scheduleMonth;		
		private String scheduleDayOfWeek;		
		private String scheduleYear;		
		private String cronExpression;
		
		public ScheduleTo() {
			super();
		}
		public Long getIdSchedule() {
			return idSchedule;
		}
		public void setIdSchedule(Long idSchedule) {
			this.idSchedule = idSchedule;
		}
		public String getScheduleHour() {
			return scheduleHour;
		}
		public void setScheduleHour(String scheduleHour) {
			this.scheduleHour = scheduleHour;
		}
		public String getScheduleMinute() {
			return scheduleMinute;
		}
		public void setScheduleMinute(String scheduleMinute) {
			this.scheduleMinute = scheduleMinute;
		}
		public String getScheduleSecond() {
			return scheduleSecond;
		}
		public void setScheduleSecond(String scheduleSecond) {
			this.scheduleSecond = scheduleSecond;
		}
		public String getScheduleDayOfMonth() {
			return scheduleDayOfMonth;
		}
		public void setScheduleDayOfMonth(String scheduleDayOfMonth) {
			this.scheduleDayOfMonth = scheduleDayOfMonth;
		}
		public String getScheduleMonth() {
			return scheduleMonth;
		}
		public void setScheduleMonth(String scheduleMonth) {
			this.scheduleMonth = scheduleMonth;
		}
		public String getScheduleDayOfWeek() {
			return scheduleDayOfWeek;
		}
		public void setScheduleDayOfWeek(String scheduleDayOfWeek) {
			this.scheduleDayOfWeek = scheduleDayOfWeek;
		}
		public String getScheduleYear() {
			return scheduleYear;
		}
		public void setScheduleYear(String scheduleYear) {
			this.scheduleYear = scheduleYear;
		}
		
		public String getCronExpression() {
			return cronExpression;
		}
		public void setCronExpression(String cronExpression) {
			this.cronExpression = cronExpression;
		}
}
