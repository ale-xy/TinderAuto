package name.alexy.test.tinderauto.phoneservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhoneData {

    @SerializedName("Status")
    @Expose
    private String status;
    @SerializedName("ErrorCode")
    @Expose
    private String errorCode;
    @SerializedName("AccountName")
    @Expose
    private String accountName;
    @SerializedName("Allocated")
    @Expose
    private List<Phone> phone = null;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public List<Phone> getPhones() {
        return phone;
    }

    public void setPhone(List<Phone> phone) {
        this.phone = phone;
    }

}