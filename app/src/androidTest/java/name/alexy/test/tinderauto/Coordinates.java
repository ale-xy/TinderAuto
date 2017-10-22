package name.alexy.test.tinderauto;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Alexey on 16/10/2017.
 */

public class Coordinates {
    private final static String LOCATIONS = "coordinates.txt";
    private static LatLon lastLocation;

    static class LatLon{
        double lat;
        double lon;

        public LatLon(double lat, double lon) {
            this.lat = lat;
            this.lon = lon;
        }
    }

    public static void setNewLocation() throws Exception {
//        LatLon location;
//
//        do {
//            location = getRandomLocation();
//        } while (lastLocation != null && distance(lastLocation, location) < 0.01);
//
//        Process p = Runtime.getRuntime().exec(new String[]{"am", "startservice",
//                            "--user", "0",
//                            "-a", "com.lexa.fakegps.STOP"});
//        getOutput(p);
//
//        Thread.sleep(500);
//
//        p = Runtime.getRuntime().exec(new String[]{"am", "startservice", "-a", "com.lexa.fakegps.START",
//                            "--user", "0",
//                            "-e", "lat", String.valueOf(location.lat),
//                            "-e", "long", String.valueOf(location.lon)});
//        getOutput(p);
    }

    private static void getOutput(Process p) throws IOException {

        String line;

        BufferedReader in = new BufferedReader(
                new InputStreamReader(p.getInputStream()) );
        while ((line = in.readLine()) != null) {
            Log.d("Coordinates", line);
        }
        in.close();

        in = new BufferedReader(
                new InputStreamReader(p.getErrorStream()) );
        while ((line = in.readLine()) != null) {
            Log.e("Coordinates", line);
        }
        in.close();
    }

    public static LatLon getRandomLocation() throws Exception {
        String location = Utils.getRandomLine(LOCATIONS);
        String[] latlon = location.split("\\t");
        return new LatLon(Double.parseDouble(latlon[0]), Double.parseDouble(latlon[1]));
    }

    private static double distance(LatLon first, LatLon second) {
        return Math.sqrt((first.lat - second.lat) * (first.lat - second.lat) + (first.lon - second.lon) * (first.lon - second.lon));
    }
}
