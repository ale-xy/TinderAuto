package name.alexy.test.tinderauto;

import android.support.test.runner.AndroidJUnitRunner;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexeykrichun on 21/10/2017.
 */

public class TinderTestRunner extends AndroidJUnitRunner {
    private final String port;

    public TinderTestRunner() throws IOException {
        super();
        StringBuilder builder = new StringBuilder();
        Log.i("runner", "Starting");

        try {
            Process process = Runtime.getRuntime().exec(new String[]{"adb", "devices"});
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            String s;

            while ((s = stdInput.readLine()) != null) {
                builder.append(s).append("\n");
                Log.i("runner", s);
            }

            while ((s = stdError.readLine()) != null) {
                builder.append(s).append("\n");
                Log.i("runner", s);
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(builder.toString());
        }

        Pattern pattern = Pattern.compile("emulator-([0-9]+)");
        Matcher matcher = pattern.matcher(builder.toString());

        if (matcher.find()) {
            port = matcher.group();
            System.out.println("Port "+port);
        } else {
            throw new RuntimeException("No emulator found in " + builder.toString());
        }
    }

//    public boolean setLocation(double lat, double lon) {
//
//    }

}
