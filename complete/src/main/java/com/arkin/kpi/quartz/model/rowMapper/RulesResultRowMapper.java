package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.RulesResultTo;


public class RulesResultRowMapper implements RowMapper<RulesResultTo> {

	@Override
	public RulesResultTo mapRow(ResultSet rs, int rowNum) throws SQLException {
		RulesResultTo rulesResult = new RulesResultTo();
		
		rulesResult.setResult(rs.getString("rules_result"));
		
		return rulesResult;
	}

}
