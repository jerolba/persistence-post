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
package com.jerolba.benchmark;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.jerolba.benchmark.shared.BenchmarkMeter;
import com.jerolba.benchmark.shared.CityBikeParser;
import com.jerolba.benchmark.shared.CityBikeReader;
import com.jerolba.benchmark.shared.DataSourceFactory;
import com.jerolba.benchmark.shared.EntityManagerFactoryFactory;
import com.jerolba.benchmark.shared.MutableInteger;
import com.jerolba.benchmark.shared.TableHelper;
import com.jerolba.benchmark.shared.TripEntityJpa;

/*
 * Persist all information in PostgreSQL or MySQL using JPA batched inserts with Statement Rewrite
 */
public class JpaBatchInsertStatementRewrite {

    public static void main(String[] args) throws IOException, SQLException {
        String properties = args[0].replace(".properties", "2.properties"); //For script support
        int batchSize = Integer.parseInt(args[1]);
        DataSourceFactory dsFactory = new DataSourceFactory(properties);
        TableHelper.createTable(dsFactory);

        EntityManagerFactoryFactory factory = new EntityManagerFactoryFactory(dsFactory, TripEntityJpa.class) {
            @Override
            public Properties properties() {
                Properties properties = super.properties();
                properties.put("hibernate.jdbc.batch_size", batchSize);
                return properties;
            }
        };
        EntityManager entityManager = factory.newEntityManagerFactory().createEntityManager();

        CityBikeParser<TripEntityJpa> parser = new CityBikeParser<>(() -> new TripEntityJpa());
        CityBikeReader<TripEntityJpa> reader = new CityBikeReader<>("/tmp", 300_000, str -> parser.parse(str));

        MutableInteger idSeq = new MutableInteger();
        BenchmarkMeter meter = new BenchmarkMeter(JpaBatchInsertStatementRewrite.class, properties, batchSize);
        meter.meter(() -> reader.forEachCsvInZip(trips -> {
            EntityTransaction tx = entityManager.getTransaction();
            Iterator<TripEntityJpa> iterator = trips.iterator();
            tx.begin();
            int cont = 0;
            while (iterator.hasNext()) {
                TripEntityJpa trip = iterator.next();
                trip.setId(idSeq.incAndGet());
                entityManager.persist(trip);
                cont++;
                if (cont % batchSize == 0) {
                    entityManager.flush();
                    entityManager.clear();
                    tx.commit();
                    tx.begin();
                }
            }
            tx.commit();
            entityManager.close();
        }));
    }

}
