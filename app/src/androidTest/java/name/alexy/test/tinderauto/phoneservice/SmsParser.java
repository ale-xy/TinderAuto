package name.alexy.test.tinderauto.phoneservice;

import android.text.TextUtils;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by alexeykrichun on 11/10/2017.
 */

public abstract class SmsParser {

    private List<SmsMessage> getMessages(String number) throws IOException {
        SmsData data = PhoneServiceApi.service.getSmsData(number).execute().body();
        if (data == null) {
            return null;
        }
        return data.getSmsMessages();
    }

    public String getCode(String phone, long startTime) throws IOException, ParseException {
        List<SmsMessage> messages = getMessages(phone);
        if (messages == null || messages.isEmpty()) {
            return null;
        }

        for (SmsMessage message: messages) {
            String code = parseMessage(message, startTime);
            if (!TextUtils.isEmpty(code)) {
                return code;
            }
        }

        return null;
    }

    public String getCode(String phone, long startTime, int repeat, long delay) throws IOException, ParseException {
        for (int i = 0; i < repeat; i++) {
            System.out.println("Fetching code, attempt " + i);
            String code = getCode(phone, startTime);

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

    private String parseMessage(SmsMessage message, long startTime) throws ParseException {
        if (!validateTime(message, startTime)) {
            return null;
        }

        String text = message.getMessage();
        return parseCode(text);
    }

    private boolean validateTime(SmsMessage message, long startTime) throws ParseException {
        System.out.println("Parsing sms " + message.getMessage());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SS"); //2017-10-05T12:06:02.91
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT+1"));
        Date date = dateFormat.parse(message.getDateReceived());
        System.out.println("Sms time " + message.getDateReceived() + " parsed time " + dateFormat.format(date) + " start time " + dateFormat.format(startTime));
        return date.getTime() >= startTime;
    }

    protected abstract String parseCode(String text);

}
