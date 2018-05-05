package com.jerolba.benchmark;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Iterator;
import java.util.function.Supplier;

import org.postgresql.copy.CopyManager;
import org.postgresql.jdbc.PgConnection;

import com.jerolba.benchmark.shared.BenchmarkMeter;
import com.jerolba.benchmark.shared.CityBikeParser;
import com.jerolba.benchmark.shared.CityBikeReader;
import com.jerolba.benchmark.shared.ConnectionProvider;
import com.jerolba.benchmark.shared.TableHelper;
import com.jerolba.benchmark.shared.TripEntity;

public class CopyInsert {

    private final static String COPY = "COPY bike_trip (tripduration, starttime, stoptime, "
            + "start_station_id, start_station_name, start_station_latitude, start_station_longitude, "
            + "end_station_id, end_station_name, end_station_latitude, end_station_longitude, bike_id, "
            + "user_type, birth_year, gender) FROM STDIN WITH (FORMAT TEXT, ENCODING 'UTF-8', DELIMITER '\t', HEADER false)";

    private static final SimpleDateFormat sdfDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws IOException, SQLException {
        String properties = args[0];
        int batchSize = Integer.parseInt(args[1]);

        Supplier<Connection> connectionSuplier = new ConnectionProvider(properties);
        try (Connection connection = connectionSuplier.get()) {
            TableHelper.createTable(connection);

            CityBikeParser<TripEntity> parser = new CityBikeParser<>(() -> new TripEntity());
            CityBikeReader<TripEntity> reader = new CityBikeReader<>("/tmp", str -> parser.parse(str));

            PgConnection unwrapped = connection.unwrap(PgConnection.class);
            CopyManager copyManager = unwrapped.getCopyAPI();

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
                        sb.append(trip.getGender());
                        sb.append("\n");
                        cont++;
                        if (cont % batchSize == 0) {
                            InputStream is = new ByteArrayInputStream(sb.toString().getBytes());
                            copyManager.copyIn(COPY, is);
                            sb.setLength(0);
                        }
                    }
                } catch (Exception e) {
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
