package com.arkin.kpi.quartz.model.rowMapper;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Map;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.JdbcUtils;
import org.springframework.util.LinkedCaseInsensitiveMap;

import com.arkin.kpi.quartz.model.to.UsersDestinationsTo;
import com.arkin.kpi.socket.util.NumberUtil;
import com.arkin.kpi.socket.util.StringUtil;

/**
 * 
 * @author jalor
 *
 */
public class UsersDestinationsRowMapper implements RowMapper<UsersDestinationsTo> {
	
	
	@Override
	public UsersDestinationsTo mapRow(ResultSet rs, int rowNum) throws SQLException {
		UsersDestinationsTo usersDestinations = new UsersDestinationsTo();		
		
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		
		for (int i = 1; i <= columnCount; i++) {
			String key = getColumnKey(JdbcUtils.lookupColumnName(rsmd, i));
			
			switch (key) {
			
	            case "id_user_pk":  usersDestinations.setIdUserPk( getColumnValue(rs, i)!=null ? NumberUtil.toLong(getColumnValue(rs, i)) : null);
	            					break;
	            case "user_email": usersDestinations.setUserEmail( getColumnValue(rs, i)!=null ? getColumnValue(rs, i).toString() : StringUtil.EMPTY);
	            					break; 
	            case "user_name": usersDestinations.setUserName( getColumnValue(rs, i)!=null ? getColumnValue(rs, i).toString() : StringUtil.EMPTY);
									break;
	            case "user_fullname": usersDestinations.setUserFullname( getColumnValue(rs, i)!=null ? getColumnValue(rs, i).toString() : StringUtil.EMPTY);
									break;
	            case "notification_repeat": usersDestinations.setQuantityRepeat( getColumnValue(rs, i)!=null ? NumberUtil.toLong(getColumnValue(rs, i)) : 0);
									break;
	            case "count_yet": usersDestinations.setQuantityYet( getColumnValue(rs, i)!=null ? NumberUtil.toLong(getColumnValue(rs, i)) : 0);
									break;
	            case "count_seen": usersDestinations.setQuantitySeen( getColumnValue(rs, i)!=null ? NumberUtil.toLong(getColumnValue(rs, i)) : 0);
				break;	
	            case "id_session": usersDestinations.setIdSession( getColumnValue(rs, i)!=null ? getColumnValue(rs, i).toString() : null);
				break;	
				
			}          
		}
		
		return usersDestinations;
	}
	
	
	protected Map<String, Object> createColumnMap(int columnCount) {
		return new LinkedCaseInsensitiveMap<Object>(columnCount);
	}

	/**
	 * Determine the key to use for the given column in the column Map.
	 * @param columnName the column name as returned by the ResultSet
	 * @return the column key to use
	 * @see java.sql.ResultSetMetaData#getColumnName
	 */
	protected String getColumnKey(String columnName) {
		return columnName;
	}

	/**
	 * Retrieve a JDBC object value for the specified column.
	 * <p>The default implementation uses the {@code getObject} method.
	 * Additionally, this implementation includes a "hack" to get around Oracle
	 * returning a non standard object for their TIMESTAMP datatype.
	 * @param rs is the ResultSet holding the data
	 * @param index is the column index
	 * @return the Object returned
	 * @see org.springframework.jdbc.support.JdbcUtils#getResultSetValue
	 */
	protected Object getColumnValue(ResultSet rs, int index) throws SQLException {
		return JdbcUtils.getResultSetValue(rs, index);
	}

}
