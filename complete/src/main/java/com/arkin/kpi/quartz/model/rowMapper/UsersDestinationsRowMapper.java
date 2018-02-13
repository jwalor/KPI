package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import com.arkin.kpi.quartz.model.to.UsersDestinationsTo;


public class UsersDestinationsRowMapper implements RowMapper<UsersDestinationsTo> {

	@Override
	public UsersDestinationsTo mapRow(ResultSet rs, int rowNum) throws SQLException {
		UsersDestinationsTo usersDestinations = new UsersDestinationsTo();
		
		usersDestinations.setIdUserPk(rs.getLong("id_user_pk"));
		usersDestinations.setUserEmail(rs.getString("user_email"));
		usersDestinations.setUserName(rs.getString("user_name"));
		usersDestinations.setUserFullname(rs.getString("user_fullname"));
		
		return usersDestinations;
	}

}
