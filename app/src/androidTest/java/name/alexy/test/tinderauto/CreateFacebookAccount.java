package name.alexy.test.tinderauto;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.widget.Button;
import android.widget.EditText;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.action.ViewActions.click;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class CreateFacebookAccount {

    private static final String FB_PACKAGE = "com.facebook.katana";
    private static final int LAUNCH_TIMEOUT = 15000;
    private static final String FIRST_NAME = "Vasya";
    private static final String LAST_NAME = "Pupkin";
    private static final String COUNTRY = "Germany";
    private static final String PHONE = "447893364366";
    private static final String PASSWORD = "QpAlZm102938";
    private static final String SMS = "12345";
    private UiDevice mDevice;

    @Before
    public void init() throws Exception {

        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = mDevice.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);
    }

    @Test
    public void createFacebookAccount() throws Exception {
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

        //name
        mDevice.findObject(new UiSelector().className(EditText.class).resourceId("com.facebook.katana:id/first_name_input")).setText(FIRST_NAME);
        mDevice.findObject(new UiSelector().className(EditText.class).resourceId("com.facebook.katana:id/last_name_input")).setText(LAST_NAME);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();

        //birthday
        mDevice.wait(Until.hasObject(By.text("What's Your Birthday?")), LAUNCH_TIMEOUT);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();

        // is today your birthday
        pressMultipleTimes("YES");

        //sex
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/gender_male")).click();
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/finish_button")).click();

        //phone

        //country selector
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/country_name_selector")).click();
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/country_search_edit_text")).setText(COUNTRY);
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/country_name").textStartsWith(COUNTRY)).click();
        //number
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/phone_input")).setText(PHONE);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();

        //password
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/password_input")).setText(PASSWORD);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/finish_button")).click();

        //finish signing up
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/finish_without_contacts")).click();

        //wait for sms request
        mDevice.wait(Until.hasObject(By.text("DENY")), LAUNCH_TIMEOUT);
        pressMultipleTimes("DENY");

        //save password
        mDevice.wait(Until.hasObject(By.text("SAVE PASSWORD")), LAUNCH_TIMEOUT);
        mDevice.findObject(new UiSelector().className(Button.class).text("SAVE PASSWORD")).click();

        //sms
        //todo get SMS
        mDevice.wait(Until.hasObject(By.text("Enter the code from your SMS")), LAUNCH_TIMEOUT);
        mDevice.findObject(new UiSelector().resourceId("com.facebook.katana:id/code_input")).setText(SMS);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.facebook.katana:id/continue_button")).click();

        pressMultipleTimes("SKIP");

    }

    private void pressMultipleTimes(String text) throws UiObjectNotFoundException {
        try {
            while (true) {
                UiObject uiObject = mDevice.findObject(new UiSelector().textMatches("(?i)" + text));
                if (uiObject != null) {
                    uiObject.click();
                }
            }
        } catch (UiObjectNotFoundException e) {
            e.getMessage();
        }
    }
}
