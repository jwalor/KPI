package com.arkin.kpi.socket.dao.impl;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import com.arkin.kpi.socket.dao.IntegrationDao;
import com.arkin.kpi.socket.util.Constantes;
import com.arkin.kpi.socket.util.DateUtil;

/**
 *  Dao with native queries.
 * @author jalor
 *
 */

@Repository
@SuppressWarnings("rawtypes")
public class IntegrationDaoImpl implements IntegrationDao{
	
	@Autowired
	NamedParameterJdbcTemplate jdbcTemplate;
	
	@Override
	public int getIntegrationTableCount() {
		Map<String, Object> map = jdbcTemplate.getJdbcOperations().queryForMap
		("select nm_mechanism from cp_dwr_settlement_cycles where op_id_operation_pk = 4686577" );
		System.out.println(map.size());
		return 0;
	}
	
	public void saveSessionSocket( Map map) {
		
		String _sql = "INSERT INTO session_socket(id, user_name, id_session, dashboard_path,register_date)"
				+ " VALUES(NEXTVAL('session_socket_id_seq'), :user_name, :id_session , :dashboard_path , :register_date)"; 
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("user_name", map.get(Constantes.userName))
                .addValue("id_session", map.get(Constantes.idSession))
                .addValue("dashboard_path", map.get(Constantes.dashBoard))
				.addValue("register_date", DateUtil.getSystemTimestamp());
		jdbcTemplate.update(_sql, parameters, keyHolder, new String[]{"id"});
	}
	
	@Override
	public void deleteSessionSocket( Map map) {

		String _sql = "UPDATE session_socket SET state = :state , update_date = :update_date  "
				+ "where state = 1 and id_session = :id_session and user_name = :user_name"; 
		KeyHolder keyHolder = new GeneratedKeyHolder();
		MapSqlParameterSource parameters = new MapSqlParameterSource()
				.addValue("id_session", map.get(Constantes.idSession))
                .addValue("user_name", map.get(Constantes.userName))
                .addValue("state", map.get(Constantes.state))
                .addValue("update_date", map.get(Constantes.updateDate)) ;
		jdbcTemplate.update(_sql, parameters, keyHolder, new String[]{"id"});
	}
	
}
