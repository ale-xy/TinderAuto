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
import android.widget.ImageButton;

import java.util.List;
import java.util.Random;

import static name.alexy.test.tinderauto.AppsAuto.LAUNCH_TIMEOUT;

/**
 * Created by alexeykrichun on 25/10/2017.
 */

public class OperaVpnAuto {
    public final static String OPERA_VPN_PACKAGE = "com.opera.vpn";
    public static final int CONNECT_TIMEOUT = 60000;

    private final UiDevice mDevice;

    public OperaVpnAuto(UiDevice mDevice) {
        this.mDevice = mDevice;
    }

    public void startOpera() {
        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(OPERA_VPN_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(OPERA_VPN_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    public void connect() throws UiObjectNotFoundException {
        UiObject connect = mDevice.findObject(new UiSelector().resourceId("com.opera.vpn:id/btn_vpn_start"));
        if (connect.exists()) {
            connect.click();
        } else {
            changeRegion();
        }

        UiObject connected = mDevice.findObject(new UiSelector().text("You are connected to"));

        if (!connected.waitForExists(CONNECT_TIMEOUT)) {
            throw new UiObjectNotFoundException("Can't connect");
        }
    }

    private void changeRegion() throws UiObjectNotFoundException {
        UiObject region = mDevice.findObject(new UiSelector().resourceId("com.opera.vpn:id/btn_change_region"));
        if (region.exists()) {
            region.click();

            List<UiObject2> countries = mDevice.findObjects(By.res("com.opera.vpn:id/region_item_name"));
            Random random = new Random();
            countries.get(random.nextInt(countries.size() - 1) + 1).click();
        }
    }

    public void disconnect() throws UiObjectNotFoundException {
        mDevice.findObject(new UiSelector().className(ImageButton.class).description("Open navigation drawer")).click();
        mDevice.findObject(new UiSelector().resourceId("com.opera.vpn:id/menu_power_switch")).click();
        mDevice.pressBack();
    }
}
