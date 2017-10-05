
package name.alexy.test.tinderauto.phoneservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SmsData {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("Messages")
    @Expose
    private List<SmsMessage> smsMessages = null;
    @SerializedName("ErrorCode")
    @Expose
    private String errorCode;
    @SerializedName("Error")
    @Expose
    private Object error;
    @SerializedName("NumberOfMessages")
    @Expose
    private Integer numberOfMessages;
    @SerializedName("TimeOfLastMessage")
    @Expose
    private String timeOfLastMessage;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<SmsMessage> getSmsMessages() {
        return smsMessages;
    }

    public void setSmsMessages(List<SmsMessage> smsMessages) {
        this.smsMessages = smsMessages;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public Object getError() {
        return error;
    }

    public void setError(Object error) {
        this.error = error;
    }

    public Integer getNumberOfMessages() {
        return numberOfMessages;
    }

    public void setNumberOfMessages(Integer numberOfMessages) {
        this.numberOfMessages = numberOfMessages;
    }

    public String getTimeOfLastMessage() {
        return timeOfLastMessage;
    }

    public void setTimeOfLastMessage(String timeOfLastMessage) {
        this.timeOfLastMessage = timeOfLastMessage;
    }

}