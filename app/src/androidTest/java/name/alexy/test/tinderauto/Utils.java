package name.alexy.test.tinderauto;

import android.support.test.InstrumentationRegistry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by alexeykrichun on 04/10/2017.
 */

public class Utils {
    public static void runADBCommand(String adbCommand) throws IOException {
        String returnValue = "";

        System.out.println("running " + adbCommand);
        Process process = Runtime.getRuntime().exec(adbCommand);
        BufferedReader stdInput = new BufferedReader(new
                InputStreamReader(process.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(process.getErrorStream()));

        System.out.println("Standard output\n");
        String s;
        while ((s = stdInput.readLine()) != null) {
            System.out.println(s);
        }

        System.out.println("Error:\n");
        while ((s = stdError.readLine()) != null) {
            System.out.println(s);
        }

        try {
            process.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static String getRandomLine(String fileName) throws IOException {
        InputStream file = InstrumentationRegistry.getContext().getAssets().open(fileName);
        List<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(file));
        String line;

        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }

        Random random = new Random();
        return lines.get(random.nextInt(lines.size()));
    }
}
