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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionProvider extends DatabaseProperties implements Supplier<Connection> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConnectionProvider.class);
            
    public ConnectionProvider(String propertiesName) throws IOException {
        super(propertiesName);
    }

    @Override
    public Connection get() {
        LOGGER.info("Connecting with {}", prop);
        String driver = prop.get("driver");
        try {
            Class.forName(driver).newInstance();
            return DriverManager.getConnection(prop.get("urlConnection"), prop.get("user"), prop.get("password"));
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            throw new RuntimeException("Can not instantiate driver " + driver, ex);
        } catch (SQLException ex) {
            throw new RuntimeException("Can not connect to database", ex);
        }
    }

}
