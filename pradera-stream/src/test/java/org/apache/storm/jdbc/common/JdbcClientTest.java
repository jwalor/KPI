/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.storm.jdbc.common;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.zaxxer.hikari.HikariDataSource;

public class JdbcClientTest extends JdbcClient {
	
	private ConnectionProvider connectionProvider;

	
	public JdbcClientTest(ConnectionProvider connectionProvider,
			int queryTimeoutSecs) {
		super(connectionProvider, queryTimeoutSecs);
		this.connectionProvider = connectionProvider;
	}
	
	public List<Column> getColumnSchema(String tableName) {
        Connection connection = null;
        List<Column> columns = new ArrayList<Column>();
        try {
            connection = connectionProvider.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet resultSet = metaData.getColumns(null, "CSDCORE_BVL", tableName, null);
            while (resultSet.next()) {
                columns.add(new Column(resultSet.getString("COLUMN_NAME"), resultSet.getInt("DATA_TYPE")));
            }
            return columns;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to get schema for table " + tableName, e);
        } finally {
            closeConnection(connection);
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
	
	  
	  
}
