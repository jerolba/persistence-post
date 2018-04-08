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

import java.util.function.Supplier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BenchmarkMeter {

    private static final Logger LOGGER = LoggerFactory.getLogger(BenchmarkMeter.class);
    
    private String test;
    private String db;
    private int size;
    
    public BenchmarkMeter(Class<?> class1, String properties, int size) {
        this.test = class1.getSimpleName();
        if (properties.toLowerCase().contains("my")) {
            this.db = "mysql";
        } else {
            this.db = "postgres";
        }
        this.size = size;
    }
            
    public void meter(Supplier<Long> a) {
        long before = System.currentTimeMillis();
        Long lines = a.get();
        long after = System.currentTimeMillis();
        LOGGER.info(test+","+db+","+size+","+(after-before)/1000+","+lines);
    }
}
