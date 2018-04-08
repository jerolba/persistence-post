/**
 * Copyright 2018 Jerónimo López Bezanilla
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jerolba.benchmark.shared;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.postgresql.jdbc.PgConnection;

public class TableHelper {

    public static void createTable(DataSourceFactory dsFactory) throws SQLException {
        DataSource dataSource = dsFactory.get();
        try (Connection conn = dataSource.getConnection()) {
            TableHelper.createTable(conn);
        }
    }

    public static void createTable(Connection connection) throws SQLException {
        String create = "";
        if (isType(connection, com.mysql.jdbc.Connection.class)) {
            create = "CREATE TABLE bike_trip (id INT NOT NULL AUTO_INCREMENT, tripduration INT NOT NULL, "
            + "starttime DATETIME, stoptime DATETIME, start_station_id INT NOT NULL, start_station_name VARCHAR(255), "
            + "start_station_latitude DOUBLE NOT NULL, start_station_longitude DOUBLE NOT NULL, "
            + "end_station_id INT NOT NULL, end_station_name VARCHAR(255), end_station_latitude DOUBLE NOT NULL, "
            + "end_station_longitude DOUBLE NOT NULL, bike_id BIGINT NOT NULL, user_type VARCHAR(255), "
            + "birth_year INT, gender CHAR, PRIMARY KEY (id))";
        } else if (isType(connection, PgConnection.class)) {
            create = "CREATE TABLE bike_trip (id SERIAL, tripduration INT NOT NULL, "
            + "starttime DATE, stoptime DATE, start_station_id INT NOT NULL, start_station_name VARCHAR(255), "
            + "start_station_latitude FLOAT NOT NULL, start_station_longitude FLOAT NOT NULL, "
            + "end_station_id INT NOT NULL, end_station_name VARCHAR(255), end_station_latitude FLOAT NOT NULL, "
            + "end_station_longitude FLOAT NOT NULL, bike_id BIGINT NOT NULL, user_type VARCHAR(255), "
            + "birth_year INT, gender CHAR, PRIMARY KEY (id))";
        }
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS bike_trip");
            stmt.execute(create);
        }
    };

    private static boolean isType(Connection connection, Class<?> clasz) {
        try {
            Object unwrap = connection.unwrap(clasz);
            return unwrap!=null;
        } catch (SQLException e) {
            return false;
        }
    }

}
