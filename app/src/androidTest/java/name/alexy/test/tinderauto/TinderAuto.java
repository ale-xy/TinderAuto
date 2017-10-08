package name.alexy.test.tinderauto;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.List;

import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class TinderAuto {

    private static final String FB_PACKAGE = "com.facebook.katana";
    private static final int LAUNCH_TIMEOUT = 15000;
    private static final int FIND_TIMEOUT = 5000;
    private static final String FIRST_NAME = "Vasya";
    private static final String LAST_NAME = "Pupkin";
    private static final String COUNTRY = "United Kingdom";
    private static final String COUNTRY_CODE = "44";
//    private static final String PHONE = "447893364366";
    private static final String PASSWORD = "QpAlZm102938";
//    private static final String SMS = "12345";

    private static final String FILE_NAMES = "names.txt";
    private static final String FILE_SURNAMES = "surnames.txt";
    private static final String FILE_BIRTHDAYS = "birthdays.txt";

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
    public void play() throws Exception{
        String phone = PhoneSmsHelper.getFacebookFreePhoneNumber();

        if (TextUtils.isEmpty(phone)) {
            fail("No phone number");
            return;
        }

        createFacebookAccount(phone);
    }

    private void createFacebookAccount(final String phone) throws Exception {
        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(FB_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(FB_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

        UiObject createAccountButton = mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/login_create_account_button"));

        if (createAccountButton == null) {
            fail("Can't create FB account");
        }

        createAccountButton.click();

        //next
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();

        //permissions
        pressMultipleTimes("DENY");

        enterName();

        int screensLeft = 4;
        long phoneTime = new Date().getTime();
        while (screensLeft > 0) {
            String title = mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/header_text")).getText();
            if (TextUtils.equals(title, "Enter Your Mobile Number")) {
                enterPhone(phone);
                screensLeft--;
            } else if (title.contains("Birth") && (title.contains("Date") || title.contains("day"))) {
                enterBirthday();
                screensLeft--;
            } else if (title.contains("Gender")) {
                enterSex();
                screensLeft--;
            } else if (title.contains("Password")) {
                enterPassword();
                screensLeft--;
            }
        }

        //finish signing up
        try {
            mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/finish_without_contacts")).click();
        } catch (UiObjectNotFoundException e) {
            e.getMessage();
        }

        try {
            mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/finish_button")).click();
        } catch (UiObjectNotFoundException e) {
            e.getMessage();
        }

        PhoneSmsHelper.addUsedPhone(phone);

        //wait for sms request
        mDevice.wait(Until.hasObject(By.text("DENY")), FIND_TIMEOUT);
        pressMultipleTimes("DENY");

        try {
            //save password
            mDevice.wait(Until.hasObject(By.text("SAVE PASSWORD")), FIND_TIMEOUT);
            mDevice.findObject(new UiSelector().className(Button.class).text("SAVE PASSWORD")).click();
        } catch (UiObjectNotFoundException e) {
            e.getMessage();
        }

        try {
            //Next Time, Log In With One Tap
            mDevice.wait(Until.hasObject(By.text("Next Time, Log In With One Tap")), FIND_TIMEOUT);
            mDevice.findObject(new UiSelector().className(Button.class).text("OK")).click();
        } catch (UiObjectNotFoundException e) {
            e.getMessage();
        }

        try {
            //Log In With One Tap
            mDevice.wait(Until.hasObject(By.text("Log In With One Tap")), FIND_TIMEOUT);
            mDevice.findObject(new UiSelector().className(Button.class).text("OK")).click();
        } catch (UiObjectNotFoundException e) {
            e.getMessage();
        }

        pressMultipleTimes("SKIP");

        //sms
        if (mDevice.wait(Until.hasObject(By.text("Enter the code from your SMS")), FIND_TIMEOUT)) {

            String code = PhoneSmsHelper.getFacebookCode(phone, phoneTime - 20000, 30, 2000);

            if (TextUtils.isEmpty(code)) {
                fail("No facebook code received");
            }

            mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/code_input")).setText(code);
            mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/continue_button")).click();
        } else {
            Log.w("TinderAuto", "No SMS requested");
        }

        pressMultipleTimes("SKIP");

        pressMultipleTimes("DENY");

    }

    private void enterPassword() throws UiObjectNotFoundException {
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/password_input")).setText(PASSWORD);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();
    }

    private void enterPhone(String phone) throws UiObjectNotFoundException {
        //country selector
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/country_name_selector")).click();
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/country_search_edit_text")).setText(COUNTRY);
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/country_name").textStartsWith(COUNTRY)).click();
        //number
        String localPhone = phone;
        if (phone.startsWith(COUNTRY_CODE)) {
            localPhone = phone.substring(COUNTRY_CODE.length());
        }

        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/phone_input")).setText(localPhone);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();
    }

    private void enterSex() throws UiObjectNotFoundException {
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/gender_female")).click();
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/finish_button")).click();
    }

    private void enterBirthday() throws Exception {
        String birthday = Utils.getRandomLine(FILE_BIRTHDAYS);
        String[] parts = birthday.split("-");  //format: 01-Jan-2000

        List<UiObject2> inputs = mDevice.findObjects(By.clazz(EditText.class).res("android:id/numberpicker_input"));

        if (inputs.size() == 3) {
            //month
            inputs.get(0).click();
            inputs.get(0).setText(parts[1]);
            //day
            inputs.get(1).click();
            inputs.get(1).setText(parts[0]);
            //year
            inputs.get(2).click();
            inputs.get(2).setText(parts[2]);
            inputs.get(0).click();

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();

        // is today your birthday / is your age ...
        pressMultipleTimes("YES");
    }

    private void enterName() throws Exception {
        String firstName = Utils.getRandomLine(FILE_NAMES);
        String lastName = Utils.getRandomLine(FILE_SURNAMES);

        mDevice.findObject(new UiSelector().className(EditText.class).resourceId("com.facebook.katana:id/first_name_input")).setText(firstName);
        mDevice.findObject(new UiSelector().className(EditText.class).resourceId("com.facebook.katana:id/last_name_input")).setText(lastName);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();
    }

    private void pressMultipleTimes(String text) throws UiObjectNotFoundException {
        try {
            while (true) {
                mDevice.findObject(new UiSelector().textMatches("(?i)" + text)).click();
            }
        } catch (UiObjectNotFoundException e) {
            e.getMessage();
        }
    }
}
