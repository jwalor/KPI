package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.ScheduleTo;


public class ScheduleRowMapper implements RowMapper<ScheduleTo>{

	@Override
	public ScheduleTo mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		ScheduleTo schedule = new ScheduleTo();
		schedule.setIdSchedule(rs.getLong("id_schedule_process_pk"));
		schedule.setScheduleHour(rs.getString("schedule_hour"));
		schedule.setScheduleMinute(rs.getString("schedule_minute"));
		schedule.setScheduleSecond(rs.getString("schedule_second"));
		schedule.setScheduleDayOfMonth(rs.getString("schedule_dayofmonth"));
		schedule.setScheduleMonth(rs.getString("schedule_month"));
		schedule.setScheduleDayOfWeek(rs.getString("schedule_dayofweek"));
		schedule.setScheduleYear(rs.getString("schedule_year"));
		schedule.setCronExpression(rs.getString("cronExpresion"));		
		
		return schedule;
	}
	
	

}
