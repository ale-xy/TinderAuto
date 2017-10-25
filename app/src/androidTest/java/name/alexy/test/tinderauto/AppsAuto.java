package name.alexy.test.tinderauto;

import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.text.TextUtils;
import android.util.Log;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import java.util.regex.Pattern;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppsAuto {
    static final int LAUNCH_TIMEOUT = 30000;
    static final int FIND_TIMEOUT = 7000;
    static final int FIND_TIMEOUT_SHORT = 1000;
    static final int SMS_REPEAT = 30;
    static final int SMS_DELAY = 2000;
    static final int SMS_NEXT_NUMBER_RETRY = 2;

    static final String COUNTRY = "United Kingdom";
    static final String COUNTRY_CODE = "44";
    static final String PASSWORD = "QpAlZm102938";

    static final String FILE_NAMES = "names.txt";
    static final String FILE_SURNAMES = "surnames.txt";
    static final String FILE_BIRTHDAYS = "birthdays.txt";

    private UiDevice mDevice;
    private TinderAuto tinderAuto;


    @Before
    public void init() {
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
    }

    private void pressHome() {
        mDevice.pressHome();

        //todo storage permissions

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void play() throws Exception {
        OperaVpnAuto operaVpnAuto = new OperaVpnAuto(mDevice);
        operaVpnAuto.startOpera();
        operaVpnAuto.connect();

        pressHome();

        FacebookAuto facebookAuto = new FacebookAuto(mDevice);

        String phone = facebookAuto.createFacebookAccount();

        if (TextUtils.isEmpty(phone)) {
            fail("No phone number");
            return;
        }

        tinderAuto = new TinderAuto(mDevice);
        tinderAuto.runTinder(phone, true, true);
        tinderAuto.runTinder(phone, false, false);

    }

    @Test
    public void continueAfterPhoneReg() throws Exception {
        tinderAuto = new TinderAuto(mDevice);
        tinderAuto.continueAfterPhoneRegistration();

    }

    static void pressMultipleTimes(UiDevice device, String text) throws UiObjectNotFoundException {
        try {
            while (true) {
                Log.d("AppsAuto", "pressMultipleTimes " + text);
                device.findObject(new UiSelector().textMatches("(?i)" + text)).click();
            }
        } catch (UiObjectNotFoundException e) {
            Log.d("AppsAuto", "no more " + text);
            e.getMessage();
        }
    }

    static boolean clickIfExistsById(UiDevice mDevice, String id) throws UiObjectNotFoundException {
        return clickIfExistsById(mDevice, id, FIND_TIMEOUT);
    }

    static boolean clickIfExistsById(UiDevice mDevice, String id, long timeout) throws UiObjectNotFoundException {
        Log.d("AppsAuto", "clickIfExistsById " + id);
        if (!mDevice.wait(Until.hasObject(By.res(id)), timeout)) {
            Log.d("AppsAuto", id + " not found ");
            return false;
        }

        mDevice.findObject(new UiSelector().resourceId(id)).click();
        Log.d("AppsAuto", id + " found ");
        return true;
    }

    static boolean clickIfExistsByText(UiDevice mDevice, String text) throws UiObjectNotFoundException {
        return clickIfExistsByText(mDevice, text, FIND_TIMEOUT);
    }

    static boolean clickIfExistsByText(UiDevice mDevice, String text, long timeout) throws UiObjectNotFoundException {
        Log.d("AppsAuto", "clickIfExistsByText " + text);
        text = "(?i)" + text;
        if (!mDevice.wait(Until.hasObject(By.text(Pattern.compile(text))), timeout)) {
            Log.d("AppsAuto", text + " not found ");
            return false;
        }
        mDevice.findObject(new UiSelector().textMatches(text)).click();
        Log.d("AppsAuto", text + " found ");
        return true;
    }

}
