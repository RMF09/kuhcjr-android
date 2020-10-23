package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostPutPerjalananDinas {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataPerjalananDinas> mPerjalananDinas;
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
    public List<DataPerjalananDinas> getListPerjalananDinas() {
        return mPerjalananDinas;
    }
    public void setListPerjalananDinas(List<DataPerjalananDinas> PerjalananDinas) {
        mPerjalananDinas = PerjalananDinas;
    }

}
