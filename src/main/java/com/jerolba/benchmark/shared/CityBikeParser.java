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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.Supplier;

public class CityBikeParser<T extends Trip> {

    private final SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-M-d HH:mm:ss");
    private final SimpleDateFormat sdf2 = new SimpleDateFormat("M/d/yyyy H:mm");

    private Supplier<T> constructor;

    public CityBikeParser(Supplier<T> constructor) {
        this.constructor = constructor;
    }

    public T parse(String line) {
        String[] columns = CsvSplit.split(line, 15);
        T trip = constructor.get();
        trip.setTripduration(parseInt(columns[0]));
        trip.setStarttime(parseDate(columns[1]));
        trip.setStoptime(parseDate(columns[2]));
        trip.setStartStationId(parseInt(columns[3]));
        trip.setStartStationName(columns[4]);
        trip.setStartStationLatitude(parseDouble(columns[5]));
        trip.setStartStationLongitude(parseDouble(columns[6]));
        trip.setEndStationId(parseInt(columns[7]));
        trip.setEndStationName(columns[8]);
        trip.setEndStationLatitude(parseDouble(columns[9]));
        trip.setEndStationLongitude(parseDouble(columns[10]));
        trip.setBikeId(parseLong(columns[11]));
        trip.setUserType(columns[12]);
        trip.setBirthYear(parseInt(columns[13]));
        trip.setGender(parseChar(columns[14]));
        return trip;
    }

    public Date parseDate(String date) {
        try {
            if (date.contains("-")) {
                return sdf1.parse(date);
            } else {
                return sdf2.parse(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Long parseLong(String value) {
        if (value != null && value.length() > 0) {
            return Long.parseLong(value);
        }
        return null;
    }

    private Integer parseInt(String value) {
        if (value != null && value.length() > 0) {
            return Integer.parseInt(value);
        }
        return null;
    }

    private Double parseDouble(String value) {
        if (value != null && value.length() > 0) {
            return Double.parseDouble(value);
        }
        return null;
    }

    public Character parseChar(String str) {
        if (str == null || str.length() == 0) {
            return null;
        }
        return str.charAt(0);
    }

}
