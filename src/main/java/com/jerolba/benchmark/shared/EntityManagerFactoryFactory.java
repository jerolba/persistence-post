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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.persistence.EntityManagerFactory;
import javax.persistence.spi.PersistenceUnitInfo;
import javax.sql.DataSource;

import org.hibernate.jpa.boot.internal.EntityManagerFactoryBuilderImpl;
import org.hibernate.jpa.boot.internal.PersistenceUnitInfoDescriptor;

import com.jerolba.benchmark.shared.DataSourceFactory.DatabaseType;

public class EntityManagerFactoryFactory {

    private Class<?>[] entities;
    private DataSource dataSource;
    private DatabaseType databaseType;

    public EntityManagerFactoryFactory(DataSourceFactory dataSourceFactory, Class<?>... entities) {
        this.entities = entities;
        this.dataSource = dataSourceFactory.get();
        this.databaseType = dataSourceFactory.getDatabaseType();
    }

    public EntityManagerFactory newEntityManagerFactory() {
        String name = getClass().getSimpleName();
        List<String> entiesClassNames = Arrays.asList(entities).stream().map(Class::getName)
                .collect(Collectors.toList());
        PersistenceUnitInfo persistenceUnitInfo = new PersistenceUnitInfoImpl(name, entiesClassNames, properties());
        PersistenceUnitInfoDescriptor puiDesc = new PersistenceUnitInfoDescriptor(persistenceUnitInfo);
        EntityManagerFactoryBuilderImpl entityManagerFactoryBuilder = new EntityManagerFactoryBuilderImpl(puiDesc,
                new HashMap<>());
        return entityManagerFactoryBuilder.build();
    }

    protected DataSource getDataSource() {
        return dataSource;
    }

    protected Properties properties() {
        Properties properties = new Properties();
        properties.put("hibernate.hbm2ddl.auto", "none");
        properties.put("hibernate.connection.datasource", getDataSource());
        properties.put("hibernate.generate_statistics", Boolean.TRUE.toString());
        if (databaseType == DatabaseType.mysql) {
            properties.put("hibernate.dialect.storage_engine", "innodb");
        } else if (databaseType == DatabaseType.postgres) {
            properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");
        }
        return properties;
    }

}
