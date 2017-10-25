package name.alexy.test.tinderauto;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.test.InstrumentationRegistry;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import name.alexy.test.tinderauto.phoneservice.TinderSmsParser;

import static junit.framework.Assert.fail;
import static name.alexy.test.tinderauto.AppsAuto.COUNTRY;
import static name.alexy.test.tinderauto.AppsAuto.COUNTRY_CODE;
import static name.alexy.test.tinderauto.AppsAuto.FILE_BIRTHDAYS;
import static name.alexy.test.tinderauto.AppsAuto.FILE_NAMES;
import static name.alexy.test.tinderauto.AppsAuto.FIND_TIMEOUT;
import static name.alexy.test.tinderauto.AppsAuto.LAUNCH_TIMEOUT;
import static name.alexy.test.tinderauto.AppsAuto.PASSWORD;
import static name.alexy.test.tinderauto.AppsAuto.clickIfExistsById;
import static name.alexy.test.tinderauto.AppsAuto.clickIfExistsByText;

/**
 * Created by alexeykrichun on 09/10/2017.
 */

public class TinderAuto {
    static final String TINDER_PACKAGE = "com.tinder";
    public static final int DISTANCE_IN_MILES = 30;
    private static final int LIKES_AMOUNT = 10;
    private static final int AUTH_RETRY_COUNT = 10;
    public static final String GOOGLE_MAPS_PACKAGE = "com.google.android.apps.maps";

    private final UiDevice mDevice;

    public TinderAuto(UiDevice mDevice) {
        this.mDevice = mDevice;
    }

    void runTinder(String phone, boolean facebook, boolean launch) throws Exception {

        if (launch) {
            launchMaps();
            launchTinder();
        }

        mDevice.wait(Until.hasObject(By.pkg(TINDER_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

        if (facebook) {
            UiObject error = mDevice.findObject(new UiSelector().resourceId("com.tinder:id/txt_dialog_title").textContains("went wrong"));
            int retries = 0;

            do {
                mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.tinder:id/real_facebook_login_button")).click();

                UiObject fbButton = mDevice.findObject(new UiSelector().className(Button.class).packageName("com.facebook.katana").resourceId("u_0_9"));

                Log.d("TinderAuto", "Searching for FB button");

                if (fbButton.waitForExists(LAUNCH_TIMEOUT)) {
                    fbButton.click();
                }

                if (fbButton.waitForExists(FIND_TIMEOUT)) {
                    fbButton.click();
                } else {
                    Log.d("TinderAuto", "No FB auth");
                }

                if (error.waitForExists(300)) {
                    mDevice.findObject(new UiSelector().resourceId("com.tinder:id/txt_mono_choice")).click();
                }

                if (retries >= AUTH_RETRY_COUNT) {
                    fail("Can't login to Tinder with Facebook");
                }
                retries++;
            } while (error.exists());

            inputAndVerifyPhone(phone);
            fillProfileAndLike(true);
        } else {
            mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.tinder:id/alternative_login_button")).click();
            inputAndVerifyPhone(phone);
            phoneRegistration();
        }
    }

    public void continueAfterPhoneRegistration() throws Exception {
        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_add_photo_done_button")).click();
        fillProfileAndLike(false);
    }

    private void fillProfileAndLike(boolean facebook) throws Exception {
        //allow location
        mDevice.wait(Until.hasObject(By.text("ALLOW")), FIND_TIMEOUT);
        AppsAuto.pressMultipleTimes(mDevice, "ALLOW");

        fillProfile(facebook? 2 : 1);

        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/tab_flame")).click();

        UiObject wrong = mDevice.findObject(new UiSelector().resourceId("com.tinder:id/recs_status_message").textContains("wrong")); //todo nobody

        int retries = 0;
        while (wrong.waitForExists(FIND_TIMEOUT)) {
            if (retries > AUTH_RETRY_COUNT) {
                fail("Something went wrong with Tinder");
            }

            mDevice.pressBack();

            launchMaps();

            launchTinder();
            mDevice.wait(Until.hasObject(By.pkg(TINDER_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
            retries++;
        }

        like();

        logout();
    }

    private void launchMaps() throws UiObjectNotFoundException, InterruptedException {
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(GOOGLE_MAPS_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        mDevice.wait(Until.hasObject(By.pkg(GOOGLE_MAPS_PACKAGE).depth(0)), LAUNCH_TIMEOUT);

        mDevice.findObject(new UiSelector().resourceId("com.google.android.apps.maps:id/mylocation_button")).click();
        Thread.sleep(3000);
    }

    private void launchTinder() {
        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(TINDER_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private void logout() throws UiObjectNotFoundException {
        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/tab_profile")).click();
        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/profile_tab_user_info_settings_button")).click();

        UiScrollable scrollView = new UiScrollable(new UiSelector().className("android.widget.ScrollView"));
        scrollView.scrollToEnd(30);
        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/button_logout")).click();
        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/txt_choice_two")).click();
    }

    private void like() throws UiObjectNotFoundException {
        boolean dialogShown = false;
        for (int i = 0; i < LIKES_AMOUNT; i++) {
            UiObject like = mDevice.findObject(new UiSelector().resourceId("com.tinder:id/gamepad_like"));
            if (!like.exists()) {
                clickIfExistsById(mDevice, "com.tinder:id/btn_find_more_matches");
                like.waitForExists(1000);
            }

            like.click();

            if (!dialogShown) {
                dialogShown = clickIfExistsById(mDevice, "com.tinder:id/txt_choice_two");
            }

        }
        clickIfExistsById(mDevice, "com.tinder:id/btn_find_more_matches");
    }

    private void phoneRegistration() throws Exception {
        //email
        UiObject emailSkip = mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_skip_button"));
        if (emailSkip.waitForExists(FIND_TIMEOUT)) {
            emailSkip.click();
            mDevice.findObject(new UiSelector().text("YES")).click();
            waitLoading();
            //password
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_password_edit_text")).setText(PASSWORD);
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_password_action_button")).click();
            waitLoading();
            String firstName = Utils.getRandomLine(FILE_NAMES);
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_name_edit_text")).setText(firstName);
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_name_add_button")).click();

            String birthday = Utils.getRandomLine(FILE_BIRTHDAYS);
            SimpleDateFormat format = new SimpleDateFormat("dd-MMM-yyyy", Locale.ENGLISH);
            Date date = format.parse(birthday);
            SimpleDateFormat newFormat = new SimpleDateFormat("MMddyyyy", Locale.ENGLISH);
            String newDate = newFormat.format(date);
            Log.d("TinderAuto", "birthday "+newDate);

            waitLoading();

            List<UiObject2> fields = mDevice.findObjects(By.clazz("android.widget.EditText"));

            for (int i = 0; i < fields.size(); i++) {
                fields.get(i).setText(newDate.substring(i, i + 1));
            }

            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_birthday_button")).click();

            waitLoading();
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/gender_female_selection_button")).click();
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_gender_continue_button")).click();

            waitLoading();
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/onboarding_add_photo_plus_circle")).click();
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/photo_source_selector_gallery")).click();

            addPhoto();

//            Coordinates.setNewLocation();

        }
    }

    private void waitLoading() throws InterruptedException {
        UiObject loading = mDevice.findObject(new UiSelector().textStartsWith("Loading"));

        do {
            Thread.sleep(200);
        } while (loading.waitForExists(100));
    }

    private void fillProfile(int numPhotos) throws UiObjectNotFoundException, IOException {
        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/tab_profile")).click();

        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/profile_tab_user_info_edit_button")).click();

        for (int i = 0; i < numPhotos; i++) {
            mDevice.findObject(new UiSelector().resourceId("com.tinder:id/profile_image_action_5")).click();
            addPhoto();
        }

        UiScrollable scrollView = new UiScrollable(new UiSelector().className("android.widget.ScrollView").resourceId("com.tinder:id/scrollView"));

        scrollView.scrollForward();

        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/editText_bio")).setText(Utils.getRandomLine("bio.txt"));
        //todo anthem

        mDevice.pressBack();

        if (mDevice.findObject(new UiSelector().resourceId("com.tinder:id/intro_top_section")).exists()) {
            mDevice.pressBack();
        }

        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/profile_tab_user_info_settings_button")).click();

        scrollView = new UiScrollable(new UiSelector().className("android.widget.ScrollView"));


//        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/textView_distance"))
        UiObject distanceBar = mDevice.findObject(new UiSelector().resourceId("com.tinder:id/seekBar_distance"));

        while (!distanceBar.exists()) {
            scrollView.scrollForward();
        }

        Rect distanceBounds = distanceBar.getBounds();
        int y = distanceBounds.centerY();
        int x = distanceBounds.left + distanceBounds.width() * DISTANCE_IN_MILES / 100;
        mDevice.click(x, y);
//
//        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/textView_years"))
//        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/seekBar_distance"))

        mDevice.pressBack();
    }

    private void addPhoto() throws UiObjectNotFoundException {
        String id = "com.tinder:id/photo_permission_dialog_button_positive";
        AppsAuto.clickIfExistsById(mDevice, id);

        AppsAuto.clickIfExistsByText(mDevice, "ALLOW");

        UiObject gallery = mDevice.findObject(new UiSelector().resourceId("com.tinder:id/text_albumName").text("Gallery"));
        if (gallery.exists()) {
            gallery.click();
        }

        clickIfExistsByText(mDevice, "com.simplemobiletools.gallery");
        mDevice.findObject(new UiSelector().resourceId("android:id/button_once")).click();


        mDevice.findObject(new UiSelector().resourceId("com.simplemobiletools.gallery:id/dir_thumbnail")).click();

        List<UiObject2> images = mDevice.findObjects(By.res("com.simplemobiletools.gallery:id/medium_thumbnail"));

        Random random = new Random();
        images.get(random.nextInt(images.size())).click();

        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/crop_image_menu_crop")).click();

    }

    private void inputAndVerifyPhone(String phone) throws UiObjectNotFoundException, IOException, ParseException {
        //phone
        if (!mDevice.wait(Until.hasObject(By.text("Enter your mobile number")), LAUNCH_TIMEOUT)) {
            return;
        }
        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/country_code")).click();

        UiScrollable listView = new UiScrollable(new UiSelector().className("android.widget.ListView").resourceId("android:id/select_dialog_listview"));
        UiSelector uiSelector = new UiSelector().textContains(COUNTRY);

        listView.scrollBackward();
        UiObject child = listView.getChild(uiSelector);

        if (!child.exists()) {
            listView.scrollIntoView(uiSelector);
            child = listView.getChild(uiSelector);
        }

        child.click();
        long phoneTime = new Date().getTime();

        String localPhone = phone;
        if (phone.startsWith(COUNTRY_CODE)) {
            localPhone = phone.substring(COUNTRY_CODE.length());
        }

        mDevice.findObject(new UiSelector().resourceId("com.tinder:id/com_accountkit_phone_number")).setText(localPhone);
        mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.tinder:id/com_accountkit_next_button")).click();

        //sms
        UiObject smsButton = mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.tinder:id/com_accountkit_next_button"));

        if (smsButton.waitForExists(FIND_TIMEOUT)) {
            UiObject smsTitle = mDevice.findObject(new UiSelector().resourceId("com.tinder:id/com_accountkit_title"));

            if (!smsTitle.getText().contains("verified")) {

                String code = new TinderSmsParser().getCode(phone, phoneTime - 20000, AppsAuto.SMS_REPEAT, AppsAuto.SMS_DELAY);

                if (TextUtils.isEmpty(code)) {
                    fail("No tinder code received");
                }

                for (int i = 1; i <= 6; i++) {
                    mDevice.findObject(new UiSelector().resourceId("com.tinder:id/com_accountkit_confirmation_code_" + i)).setText(code.substring(i - 1, i));
                }
            }

            mDevice.findObject(new UiSelector().className(Button.class).resourceId("com.tinder:id/com_accountkit_next_button")).click();
        } else {
            Log.w("TinderAuto", "No SMS requested");
        }

    }
}
