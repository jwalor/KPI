package com.arkin.kpi.quartz.dao.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import com.arkin.kpi.quartz.dao.DashboardDao;
import com.arkin.kpi.quartz.model.rowMapper.DashboardKpiRowMapper;
import com.arkin.kpi.quartz.model.rowMapper.SessionSocketRowMapper;
import com.arkin.kpi.quartz.model.to.DashboardKpiTo;
import com.arkin.kpi.quartz.model.to.SessionSocketTo;
import com.arkin.kpi.socket.util.Constantes;



@Repository
public class DashBoardDaoImpl implements DashboardDao{
	
	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;

	@Override
	public List<DashboardKpiTo> getDashboardKpiByTable(Map<String, String> params) {
		return jdbcTemplate.query(Constantes.Querys.SELECT_DASHBOARD_KPIS_BY_TABLE, params,new DashboardKpiRowMapper());
	}

	@Override
	public List<SessionSocketTo> getSessionSocket() {
		return jdbcTemplate.query(Constantes.Querys.SELECT_SESSION_SOCKET, new SessionSocketRowMapper());
	}
}
