package name.alexy.test.tinderauto.phoneservice;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Phone {

    @SerializedName("Number")
    @Expose
    private String number;
    @SerializedName("NumberRange")
    @Expose
    private Object numberRange;
    @SerializedName("ServiceName")
    @Expose
    private String serviceName;
    @SerializedName("ServiceId")
    @Expose
    private Integer serviceId;
    @SerializedName("DateAllocated")
    @Expose
    private String dateAllocated;

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public Object getNumberRange() {
        return numberRange;
    }

    public void setNumberRange(Object numberRange) {
        this.numberRange = numberRange;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public Integer getServiceId() {
        return serviceId;
    }

    public void setServiceId(Integer serviceId) {
        this.serviceId = serviceId;
    }

    public String getDateAllocated() {
        return dateAllocated;
    }

    public void setDateAllocated(String dateAllocated) {
        this.dateAllocated = dateAllocated;
    }

}
