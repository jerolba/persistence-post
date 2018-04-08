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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class DatabaseProperties {

    protected Map<String, String> prop = new HashMap<>();

    public DatabaseProperties(String propertiesName) throws IOException {
        InputStream is = locateInputStream(propertiesName);
        Properties p = new Properties();
        p.load(is);
        p.forEach((k, v) -> prop.put(k.toString(), v.toString()));
    }

    private InputStream locateInputStream(String propertiesName) throws FileNotFoundException {
        File f = new File(propertiesName);
        if (f.exists()) {
            return new FileInputStream(f);
        }
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(propertiesName);
        if (is == null) {
            throw new FileNotFoundException(propertiesName);
        }
        return is;
    }

    public void with(String key, String value) {
        prop.put(key, value);
    }

    public String getProperty(String key) {
        return prop.get(key);
    }

}
