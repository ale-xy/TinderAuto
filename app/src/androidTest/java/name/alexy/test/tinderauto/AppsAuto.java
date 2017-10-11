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

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class AppsAuto {
    static final int LAUNCH_TIMEOUT = 15000;
    static final int FIND_TIMEOUT = 5000;
    static final String FIRST_NAME = "Vasya";
    static final String LAST_NAME = "Pupkin";
    static final String COUNTRY = "United Kingdom";
    static final String COUNTRY_CODE = "44";
//    private static final String PHONE = "447893364366";
    static final String PASSWORD = "QpAlZm102938";

    static final String FILE_NAMES = "names.txt";
    static final String FILE_SURNAMES = "surnames.txt";
    static final String FILE_BIRTHDAYS = "birthdays.txt";

    private UiDevice mDevice;

    @Before
    public void init() throws Exception {

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();

        //todo storage permissions

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void play() throws Exception {
        String phone = PhoneSmsHelper.getFacebookFreePhoneNumber();

        if (TextUtils.isEmpty(phone)) {
            fail("No phone number");
            return;
        }

//        FacebookAuto facebookAuto = new FacebookAuto(mDevice);
//        facebookAuto.createFacebookAccount(phone);

        TinderAuto tinderAuto = new TinderAuto(mDevice);
        tinderAuto.runTinder(phone, true);
        tinderAuto.runTinder(phone, false);

    }

    static void pressMultipleTimes(UiDevice device, String text) throws UiObjectNotFoundException {
        try {
            while (true) {
                device.findObject(new UiSelector().textMatches("(?i)" + text)).click();
            }
        } catch (UiObjectNotFoundException e) {
            e.getMessage();
        }
    }

    static boolean clickIfExistsById(UiDevice mDevice, String id) throws UiObjectNotFoundException {
        if (!mDevice.wait(Until.hasObject(By.res(id)), FIND_TIMEOUT)) {
            return false;
        }

        mDevice.findObject(new UiSelector().resourceId(id)).click();
        return true;
    }

    static boolean clickIfExistsByText(UiDevice mDevice, String text) throws UiObjectNotFoundException {
        if (!mDevice.wait(Until.hasObject(By.text(text)), FIND_TIMEOUT)) {
            return false;
        }
        mDevice.findObject(new UiSelector().text(text)).click();
        return true;
    }

}
