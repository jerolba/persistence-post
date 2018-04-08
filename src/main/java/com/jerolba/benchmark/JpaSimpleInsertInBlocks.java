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

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import com.jerolba.benchmark.shared.BenchmarkMeter;
import com.jerolba.benchmark.shared.CityBikeParser;
import com.jerolba.benchmark.shared.CityBikeReader;
import com.jerolba.benchmark.shared.DataSourceFactory;
import com.jerolba.benchmark.shared.EntityManagerFactoryFactory;
import com.jerolba.benchmark.shared.TableHelper;
import com.jerolba.benchmark.shared.TripEntity;

/*
 * Persist all information in PostgreSQL or MySQL using simple JPA inserts in transactions of N elements.
 */
public class JpaSimpleInsertInBlocks {

    public static void main(String[] args) throws IOException, SQLException {
        String properties = args[0];
        int batchSize = Integer.parseInt(args[1]);
        DataSourceFactory dsFactory = new DataSourceFactory(properties);
        TableHelper.createTable(dsFactory);

        EntityManagerFactoryFactory factory = new EntityManagerFactoryFactory(dsFactory, TripEntity.class);
        EntityManager entityManager = factory.newEntityManagerFactory().createEntityManager();

        CityBikeParser<TripEntity> parser = new CityBikeParser<>(() -> new TripEntity());
        CityBikeReader<TripEntity> reader = new CityBikeReader<>("/tmp", 300_000, str -> parser.parse(str));

        BenchmarkMeter meter = new BenchmarkMeter(JpaSimpleInsertInBlocks.class, properties, batchSize);
        meter.meter(() -> reader.forEachCsvInZip(trips -> {
                EntityTransaction tx = entityManager.getTransaction();
                Iterator<TripEntity> iterator = trips.iterator();
                tx.begin();
                int cont = 0;
                while (iterator.hasNext()) {
                    entityManager.persist(iterator.next());
                    cont++;
                    if (cont % batchSize == 0) {
                        tx.commit();
                        tx.begin();
                    }
                }
                tx.commit();
                entityManager.close();
            })
        );
    }

}
