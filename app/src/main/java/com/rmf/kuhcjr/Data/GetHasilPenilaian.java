package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class GetHasilPenilaian {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    String isiHTML;
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
    public String getIsiHTML() {
        return isiHTML;
    }
    public void setIsiHTML(String isiHTML) {
        this.isiHTML= isiHTML;
    }
}
