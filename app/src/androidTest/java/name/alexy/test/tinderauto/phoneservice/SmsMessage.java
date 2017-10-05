package name.alexy.test.tinderauto.phoneservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SmsMessage {

    @SerializedName("MessageId")
    @Expose
    private String messageId;
    @SerializedName("Message")
    @Expose
    private String message;
    @SerializedName("Number")
    @Expose
    private String number;
    @SerializedName("DateReceived")
    @Expose
    private String dateReceived;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(String dateReceived) {
        this.dateReceived = dateReceived;
    }

}
