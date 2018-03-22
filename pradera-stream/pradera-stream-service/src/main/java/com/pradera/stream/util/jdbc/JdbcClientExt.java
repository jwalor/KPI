package com.pradera.stream.util.jdbc;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import org.apache.storm.jdbc.common.Column;
import org.apache.storm.jdbc.common.ConnectionProvider;
import org.apache.storm.jdbc.common.JdbcClient;
import org.apache.storm.jdbc.common.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.pradera.stream.model.CustomColumn;
import com.pradera.stream.singleton.HikariCPConnectionSingletonSource;
import com.pradera.stream.util.StringUtil;

/**
 * This class have added NamedParameterStatement.class for improve 
 * the effectiveness and stability when parameters are being inserted and deleted
 * 
 * @author jalor
 *
 */

@SuppressWarnings({"rawtypes" , "unlikely-arg-type"}) 
public class JdbcClientExt extends JdbcClient {

	private static final Logger LOG = LoggerFactory.getLogger(JdbcClientExt.class);

	private ConnectionProvider connectionProvider;
	private int queryTimeoutSecs;
	private String schema = StringUtil.EMPTY;

	public JdbcClientExt(ConnectionProvider connectionProvider, int queryTimeoutSecs) {
		super(connectionProvider, queryTimeoutSecs);
		this.connectionProvider = connectionProvider;
		this.queryTimeoutSecs 	= queryTimeoutSecs;
		setSchema(((HikariCPConnectionSingletonSource)connectionProvider).getSchema());
	}

	public void executeSql(String sql) {
		Connection connection = null;
		try {
			connection = connectionProvider.getConnection();
			if ( !schema.isEmpty()) {
				connection.setSchema(schema);
			}
			Statement statement = connection.createStatement();
			statement.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException("Failed to execute SQL", e);
		} finally {
			closeConnection(connection);
		}
	}

	@Override
	public void executeInsertQuery(String query, List<List<Column>> columnLists) {
		Connection connection = null;
		try {
			connection =  connectionProvider.getConnection();
			if ( !schema.isEmpty()) {
				connection.setSchema(schema);
			}
			boolean autoCommit = connection.getAutoCommit();
			if(autoCommit) {
				connection.setAutoCommit(false);
			}

			LOG.debug("Executing query {}", query);

			NamedParameterStatement namedParameterStatement	= new NamedParameterStatement(connection, query);
			if(queryTimeoutSecs > 0) {
				namedParameterStatement.getStatement().setQueryTimeout(queryTimeoutSecs);
			}

			for(List<Column> columnList : columnLists) {
				setPreparedStatementParams(namedParameterStatement, columnList);
				namedParameterStatement.addBatch();
			}

			int[] results = namedParameterStatement.executeBatch();
			if(Arrays.asList(results).contains(Statement.EXECUTE_FAILED)) {
				connection.rollback();
				throw new RuntimeException("failed at least one sql statement in the batch, operation rolled back.");
			} else {
				try {
					connection.commit();
				} catch (SQLException e) {
					throw new RuntimeException("Failed to commit insert query " + query, e);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to execute insert query " + query, e);
		} finally {
			closeConnection(connection);
		}
	}

	@Override
	public List<List<Column>> select(String sqlQuery, List<Column> queryParams) {
		Connection connection = null;
		try {
			connection =  connectionProvider.getConnection();
			if ( !schema.isEmpty()) {
				connection.setSchema(schema);
			}
			NamedParameterStatement namedParameterStatement	= new NamedParameterStatement(connection, sqlQuery);

			if(queryTimeoutSecs > 0) {
				namedParameterStatement.getStatement().setQueryTimeout(queryTimeoutSecs);
			}

			if (queryParams!=null && queryParams.size()>0) {
				setPreparedStatementParams(namedParameterStatement, queryParams);
			}

			ResultSet resultSet = namedParameterStatement.executeQuery();
			List<List<Column>> rows = Lists.newArrayList();
			while(resultSet.next()){
				ResultSetMetaData metaData = resultSet.getMetaData();
				int columnCount = metaData.getColumnCount();
				List<Column> row = Lists.newArrayList();
				for(int i=1 ; i <= columnCount; i++) {
					String columnLabel = metaData.getColumnLabel(i);
					int columnType = metaData.getColumnType(i);
					Class columnJavaType = Util.getJavaType(columnType);
					if (columnJavaType.equals(String.class)) {
						row.add(new CustomColumn<String>(columnLabel, resultSet.getString(columnLabel), columnType));
					} else if (columnJavaType.equals(Integer.class)) {
						row.add(new CustomColumn<Integer>(columnLabel, resultSet.getInt(columnLabel), columnType));
					} else if (columnJavaType.equals(Double.class)) {
						row.add(new CustomColumn<Double>(columnLabel, resultSet.getDouble(columnLabel), columnType));
					} else if (columnJavaType.equals(Float.class)) {
						row.add(new CustomColumn<Float>(columnLabel, resultSet.getFloat(columnLabel), columnType));
					} else if (columnJavaType.equals(Short.class)) {
						row.add(new CustomColumn<Short>(columnLabel, resultSet.getShort(columnLabel), columnType));
					} else if (columnJavaType.equals(Boolean.class)) {
						row.add(new CustomColumn<Boolean>(columnLabel, resultSet.getBoolean(columnLabel), columnType));
					} else if (columnJavaType.equals(byte[].class)) {
						row.add(new CustomColumn<byte[]>(columnLabel, resultSet.getBytes(columnLabel), columnType));
					} else if (columnJavaType.equals(Long.class)) {
						row.add(new CustomColumn<Long>(columnLabel, resultSet.getLong(columnLabel), columnType));
					} else if (columnJavaType.equals(Date.class)) {
						row.add(new CustomColumn<Date>(columnLabel, resultSet.getDate(columnLabel), columnType));
					} else if (columnJavaType.equals(Time.class)) {
						row.add(new CustomColumn<Time>(columnLabel, resultSet.getTime(columnLabel), columnType));
					} else if (columnJavaType.equals(Timestamp.class)) {
						row.add(new CustomColumn<Timestamp>(columnLabel, resultSet.getTimestamp(columnLabel), columnType));
					} else {
						throw new RuntimeException("type =  " + columnType + " for column " + columnLabel + " not supported.");
					}
				}
				rows.add(row);
			}
			return rows;
		} catch (SQLException e) {
			throw new RuntimeException("Failed to execute select query " + sqlQuery, e);
		} finally {
			closeConnection(connection);
		}
	}


	/**
	 */
	private void setPreparedStatementParams(NamedParameterStatement namedParameterStatement, List<Column> columnList) throws SQLException {

		for (Column column : columnList) {
			Class columnJavaType = Util.getJavaType(column.getSqlType());
			if (columnJavaType.equals(String.class)) {
				namedParameterStatement.setString(column.getColumnName(), (String) column.getVal());
			} else if (columnJavaType.equals(Integer.class)) {
				namedParameterStatement.setInt(column.getColumnName(), (Integer) column.getVal());
			} else if (columnJavaType.equals(Double.class)) {
				namedParameterStatement.setDouble(column.getColumnName(), (Double) column.getVal());
			} else if (columnJavaType.equals(Float.class)) {
				namedParameterStatement.setFloat(column.getColumnName(), (Float) column.getVal());
			} else if (columnJavaType.equals(Short.class)) {
				namedParameterStatement.setShort(column.getColumnName(), (Short) column.getVal());
			} else if (columnJavaType.equals(Boolean.class)) {
				namedParameterStatement.setBoolean(column.getColumnName(), (Boolean) column.getVal());
			} else if (columnJavaType.equals(byte[].class)) {
				namedParameterStatement.setBytes(column.getColumnName(), (byte[]) column.getVal());
			} else if (columnJavaType.equals(Long.class)) {
				namedParameterStatement.setLong(column.getColumnName(), (Long) column.getVal());
			} else if (columnJavaType.equals(Date.class)) {
				namedParameterStatement.setDate(column.getColumnName(), (Date) column.getVal());
			} else if (columnJavaType.equals(Time.class)) {
				namedParameterStatement.setTime(column.getColumnName(), (Time) column.getVal());
			} else if (columnJavaType.equals(Timestamp.class)) {
				namedParameterStatement.setTimestamp(column.getColumnName(), (Timestamp) column.getVal());
			} else {
				throw new RuntimeException("Unknown type of value " + column.getVal() + " for column " + column.getColumnName());
			}
		}
	}

	private void closeConnection(Connection connection) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException("Failed to close connection", e);
			}
		}
	}

	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}
	public void setConnectionProvider(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}
	public String getSchema() {
		return schema;
	}

	public void setSchema(String schema) {
		this.schema = schema;
	}

}
