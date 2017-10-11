package name.alexy.test.tinderauto;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
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

import java.util.Date;
import java.util.List;

import name.alexy.test.tinderauto.phoneservice.FacebookSmsParser;

import static name.alexy.test.tinderauto.AppsAuto.COUNTRY;
import static name.alexy.test.tinderauto.AppsAuto.COUNTRY_CODE;
import static name.alexy.test.tinderauto.AppsAuto.FILE_BIRTHDAYS;
import static name.alexy.test.tinderauto.AppsAuto.FILE_NAMES;
import static name.alexy.test.tinderauto.AppsAuto.FILE_SURNAMES;
import static name.alexy.test.tinderauto.AppsAuto.FIND_TIMEOUT;
import static name.alexy.test.tinderauto.AppsAuto.LAUNCH_TIMEOUT;
import static name.alexy.test.tinderauto.AppsAuto.PASSWORD;
import static name.alexy.test.tinderauto.AppsAuto.pressMultipleTimes;
import static org.junit.Assert.fail;

/**
 * Created by alexeykrichun on 09/10/2017.
 */

public class FacebookAuto {
    static final String FB_PACKAGE = "com.facebook.katana";
    private final UiDevice mDevice;

    public FacebookAuto(UiDevice mDevice) {
        this.mDevice = mDevice;
    }


    void createFacebookAccount(final String phone) throws Exception {
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
        pressMultipleTimes(mDevice, "DENY");

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
        pressMultipleTimes(mDevice, "DENY");

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

//        pressMultipleTimes(mDevice, "SKIP");

        //sms
        if (mDevice.wait(Until.hasObject(By.text("Enter the code from your SMS")), FIND_TIMEOUT)) {


            String code = new FacebookSmsParser().getCode(phone, phoneTime - 20000, 30, 2000);

            if (TextUtils.isEmpty(code)) {
                fail("No facebook code received");
            }

            mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/code_input")).setText(code);
            mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/continue_button")).click();
        } else {
            Log.w("TinderAuto", "No SMS requested");
        }

        pressMultipleTimes(mDevice, "SKIP");

        pressMultipleTimes(mDevice, "DENY");

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
        pressMultipleTimes(mDevice, "YES");
    }

    private void enterName() throws Exception {
        String firstName = Utils.getRandomLine(FILE_NAMES);
        String lastName = Utils.getRandomLine(FILE_SURNAMES);

        mDevice.findObject(new UiSelector().className(EditText.class).resourceId("com.facebook.katana:id/first_name_input")).setText(firstName);
        mDevice.findObject(new UiSelector().className(EditText.class).resourceId("com.facebook.katana:id/last_name_input")).setText(lastName);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();
    }

}
