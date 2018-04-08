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

import java.io.IOException;
import java.util.function.Supplier;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariDataSource;

public class DataSourceFactory extends DatabaseProperties implements Supplier<DataSource> {

    public static enum DatabaseType {
        mysql, postgres
    };

    public DataSourceFactory(String properties) throws IOException {
        super(properties);
    }

    @Override
    public DataSource get() {
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(prop.get("driver"));
        ds.setJdbcUrl(prop.get("urlConnection"));
        ds.setUsername(prop.get("user"));
        ds.setPassword(prop.get("password"));
        return ds;
    }

    public DatabaseType getDatabaseType() {
        String driver = prop.get("driver");
        if (driver.contains("mysql")) {
            return DatabaseType.mysql;
        }
        if (driver.contains("postgresql")) {
            return DatabaseType.postgres;
        }
        return null;
    }

}
