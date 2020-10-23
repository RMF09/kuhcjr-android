package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetUserLogin {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataUserLogin> data;
    @SerializedName("message")
    String message;

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    public List<DataUserLogin> getData() {
        return data;
    }
    public void setData(List<DataUserLogin> data) {
        this.data= data;
    }
}
