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

import java.io.Closeable;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to log the number of items processed in a Stream and meter the
 * time elaspsed from creation.
 */
public class StreamCounter<T> implements Closeable, Function<T,T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(StreamCounter.class);

    private final long batchSize;
    private final long init;
    private long counter;

    public StreamCounter() {
        this(10_000);
    }

    public StreamCounter(long batchSize) {
        this.batchSize = batchSize;
        this.init = System.nanoTime();
        this.counter = 0;
    }

    @Override
    public T apply(T t) {
        return count(t);
    }
    
    public T count(T item) {
        counter++;
        if (batchSize>0 && counter % batchSize == 0) {
            trace();
        }
        return item;
    }

    public void trace() {
        LOGGER.info("Processed " + counter + " items in " + (System.nanoTime() - init) / 1_000_000 + " ms");
    }
    
    public long count() {
        return counter;
    }

    @Override
    public void close() {
        trace();
    }

}