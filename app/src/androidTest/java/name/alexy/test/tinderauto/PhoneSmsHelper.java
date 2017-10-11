package name.alexy.test.tinderauto;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;

import java.io.IOException;
import java.text.ParseException;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by alexeykrichun on 05/10/2017.
 */

public class PhoneSmsHelper {

    private static final String SHARED_PREFS = "TinderAutoPrefs";
    private static final String USED_PHONES_SET = "USED_PHONES_SET";

    public static String getFacebookFreePhoneNumber() throws IOException, ParseException {
        return "447417247109";

//
//        PhoneData phoneData = PhoneServiceApi.service.getPhoneData().execute().body();
//
//        Set<String> usedPhones = getUsedPhones();
//
//        if (phoneData != null && phoneData.getPhones() != null) {
//            ListIterator<Phone> iterator = phoneData.getPhones().listIterator(phoneData.getPhones().size());
//            while (iterator.hasPrevious()){
//                Phone phone = iterator.previous();
//                System.out.println("Checking phone " + phone.getNumber());
//                if (TextUtils.isEmpty(new FacebookSmsParser().getCode(phone.getNumber(), 0L)) &&
//                        (usedPhones == null || !usedPhones.contains(phone.getNumber()))) {
//                    System.out.println("Found phone without sms " + phone.getNumber());
//                    return phone.getNumber();
//                }
//            }
//        }
//        System.out.println("Phone not found");
//        return null;
    }

    private static Set<String> getUsedPhones() {
        SharedPreferences prefs = InstrumentationRegistry.getTargetContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return prefs.getStringSet(USED_PHONES_SET, null);
    }

    public static void addUsedPhone(String phone) {
        SharedPreferences prefs = InstrumentationRegistry.getTargetContext().getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        Set<String> set = prefs.getStringSet(USED_PHONES_SET, null);

        if (set == null) {
            set = new HashSet<>();
        }

        HashSet<String> newSet = new HashSet<>(set);
        newSet.add(phone);
        SharedPreferences.Editor edit = prefs.edit();
        edit.remove(USED_PHONES_SET).putStringSet(USED_PHONES_SET, newSet).commit();
    }


}
