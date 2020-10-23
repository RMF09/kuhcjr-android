package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class PostPutKegiatan {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    DataKegiatan mKegiatan;
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
    public DataKegiatan getmKegiatan() {
        return mKegiatan;
    }
    public void setCuti(DataKegiatan Kegiatan) {
        mKegiatan = Kegiatan;
    }

}
