package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.DashboardKpiTo;


public class DashboardKpiRowMapper implements RowMapper<DashboardKpiTo> {

	@Override
	public DashboardKpiTo mapRow(ResultSet rs, int rowNum) throws SQLException {
		
		DashboardKpiTo dashboardKpi = new DashboardKpiTo();
		dashboardKpi.setPathDashboard(rs.getString("path_dashboard"));
		dashboardKpi.setIdKpiComponent(rs.getLong("id_kpi_component_pk"));	
		dashboardKpi.setKpiName(rs.getString("kpi_name"));
		
		return dashboardKpi;
	}
	
}
