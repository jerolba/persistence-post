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

import java.util.Date;

public interface Trip {

    void setTripduration(int tripduration);

    void setStarttime(Date starttime);

    void setStoptime(Date stoptime);

    void setStartStationId(int startStationId);

    void setStartStationName(String startStationName);

    void setStartStationLatitude(double startStationLatitude);

    void setStartStationLongitude(double startStationLongitude);

    void setEndStationId(int endStationId);

    void setEndStationName(String endStationName);

    void setEndStationLatitude(double endStationLatitude);

    void setEndStationLongitude(double endStationLongitude);

    void setBikeId(long bikeId);

    void setUserType(String userType);

    void setBirthYear(Integer birthYear);

    void setGender(Character gender);

}
