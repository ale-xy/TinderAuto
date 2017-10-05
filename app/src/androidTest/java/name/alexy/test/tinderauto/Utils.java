package name.alexy.test.tinderauto;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by alexeykrichun on 04/10/2017.
 */

public class Utils {
    public static String runADBCommand(String adbCommand) throws IOException {
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

        System.out.println("Return " + returnValue);
        return returnValue;
    }
}
