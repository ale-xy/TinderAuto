package name.alexy.test.tinderauto;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.test.InstrumentationRegistry;
import android.text.TextUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import name.alexy.test.tinderauto.phoneservice.Phone;
import name.alexy.test.tinderauto.phoneservice.PhoneData;
import name.alexy.test.tinderauto.phoneservice.PhoneServiceApi;
import name.alexy.test.tinderauto.phoneservice.SmsData;
import name.alexy.test.tinderauto.phoneservice.SmsMessage;

/**
 * Created by alexeykrichun on 05/10/2017.
 */

public class PhoneSmsHelper {

    private static final String SHARED_PREFS = "TinderAutoPrefs";
    private static final String USED_PHONES_SET = "USED_PHONES_SET";

    public static String getFacebookFreePhoneNumber() throws IOException, ParseException {
//        return "447700373468";

        PhoneData phoneData = PhoneServiceApi.service.getPhoneData().execute().body();

        Set<String> usedPhones = getUsedPhones();

        if (phoneData != null && phoneData.getPhones() != null) {
            ListIterator<Phone> iterator = phoneData.getPhones().listIterator(phoneData.getPhones().size());
            while (iterator.hasPrevious()){
                Phone phone = iterator.previous();
                System.out.println("Checking phone " + phone.getNumber());
                if (
                        //TextUtils.isEmpty(getFacebookCode(phone.getNumber(), 0L)) &&
                        (usedPhones == null || !usedPhones.contains(phone.getNumber()))) {
                    System.out.println("Found phone without sms " + phone.getNumber());
                    return phone.getNumber();
                }
            }
        }
        System.out.println("Phone not found");
        return null;
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


    private static List<SmsMessage> getMessages(String number) throws IOException {
        SmsData data = PhoneServiceApi.service.getSmsData(number).execute().body();
        if (data == null) {
            return null;
        }
        return data.getSmsMessages();
    }

    public static String getFacebookCode(String phone, long startTime) throws IOException, ParseException {
        List<SmsMessage> messages = getMessages(phone);
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        for (SmsMessage message: messages) {
            String code = parseFacebookCode(message, startTime);
            if (!TextUtils.isEmpty(code)) {
                return code;
            }
        }

        return null;
    }

    public static String getFacebookCode(String phone, long startTime, int repeat, long delay) throws IOException, ParseException {
        for (int i = 0; i < repeat; i++) {
            System.out.println("Fetching code, attempt " + i);
            String code = getFacebookCode(phone, startTime);

            if (!TextUtils.isEmpty(code)) {
                return code;
            }

            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    private static String parseFacebookCode(SmsMessage message, long startTime) throws ParseException {
        System.out.println("Parsing sms " + message.getMessage());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS"); //2017-10-05T12:06:02.91
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date date = dateFormat.parse(message.getDateReceived());
        System.out.println("Sms time " + message.getDateReceived() + " parsed time " + dateFormat.format(date) + " start time " + dateFormat.format(startTime));

        if (date.getTime() > startTime) {
            String text = message.getMessage();
            if (text.toLowerCase().contains("facebook")) {
                Pattern pattern = Pattern.compile("\\b(\\d{5})\\b");
                Matcher matcher = pattern.matcher(text);
                if (matcher.find()) {
                    System.out.println("Code found " + matcher.group());
                    return matcher.group();
                }
            }
        }
        return null;
    }

}
