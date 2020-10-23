package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class PostPutKontak {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    DataKontak mKontak;
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
    public DataKontak getKontak() {
        return mKontak;
    }
    public void setKontak(DataKontak Kontak) {
        mKontak = Kontak;
    }

}
