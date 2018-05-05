package com.jerolba.benchmark;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.function.Supplier;

import com.jerolba.benchmark.shared.BenchmarkMeter;
import com.jerolba.benchmark.shared.CityBikeParser;
import com.jerolba.benchmark.shared.CityBikeReader;
import com.jerolba.benchmark.shared.ConnectionProvider;
import com.jerolba.benchmark.shared.TableHelper;
import com.jerolba.benchmark.shared.TripEntity;
import com.mysql.jdbc.Statement;

public class LoadDataInsert {

    private final static String LOADDATA = "LOAD DATA LOCAL INFILE '' INTO TABLE `bike_trip` CHARACTER SET UTF8 "
            + "FIELDS TERMINATED BY '\t' ENCLOSED BY '' ESCAPED BY '\\\\' LINES TERMINATED BY '\n' "
            + "STARTING BY '' "
            + "(`tripduration`, `starttime`, `stoptime`, `start_station_id`, `start_station_name`, "
            + "`start_station_latitude`, `start_station_longitude`, `end_station_id`, `end_station_name`, "
            + "`end_station_latitude`, `end_station_longitude`, `bike_id`, `user_type`, `birth_year`, `gender`)";

    private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException, SQLException {
        String properties = args[0];
        int batchSize = Integer.parseInt(args[1]);
        Supplier<Connection> connectionSuplier = new ConnectionProvider(properties);
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);

            CityBikeParser<TripEntity> parser = new CityBikeParser<>(() -> new TripEntity());
            CityBikeReader<TripEntity> reader = new CityBikeReader<>("/tmp", str -> parser.parse(str));

            com.mysql.jdbc.Connection unwrapped = connection.unwrap(com.mysql.jdbc.Connection.class);
            unwrapped.setAllowLoadLocalInfile(true);

            BenchmarkMeter meter = new BenchmarkMeter(JpaSimpleInsert.class, properties, batchSize);
            meter.meter(() -> reader.forEachCsvInZip(trips -> {
                try {
                    int cont = 0;
                    StringBuilder sb = new StringBuilder();
                    Iterator<TripEntity> iterator = trips.iterator();
                    while (iterator.hasNext()) {
                        TripEntity trip = iterator.next();
                        sb.append(trip.getTripduration()).append("\t");
                        sb.append(sdfDateTime.format(trip.getStarttime())).append("\t");
                        sb.append(sdfDateTime.format(trip.getStoptime())).append("\t");
                        sb.append(trip.getStartStationId()).append("\t");
                        sb.append(trip.getStartStationName()).append("\t");
                        sb.append(trip.getStartStationLatitude()).append("\t");
                        sb.append(trip.getStartStationLongitude()).append("\t");
                        sb.append(trip.getEndStationId()).append("\t");
                        sb.append(trip.getEndStationName()).append("\t");
                        sb.append(trip.getEndStationLatitude()).append("\t");
                        sb.append(trip.getEndStationLongitude()).append("\t");
                        sb.append(trip.getBikeId()).append("\t");
                        sb.append(trip.getUserType()).append("\t");
                        sb.append(nullify(trip.getBirthYear())).append("\t");
                        sb.append(trip.getGender()).append("\t");
                        sb.append("\n");
                        cont++;
                        if (cont % batchSize == 0) {
                            Statement statement = (Statement) unwrapped.createStatement();
                            InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
                            statement.setLocalInfileInputStream(is);
                            statement.execute(LOADDATA);
                            sb.setLength(0);
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }})
            );

        }
    }

    private static String nullify(Integer value) {
        if (value==null) {
            return "\\N";
        }
        return value.toString();
    }

}
