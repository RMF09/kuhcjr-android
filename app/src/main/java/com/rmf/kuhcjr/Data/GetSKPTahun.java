package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetSKPTahun {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataSKPTahun> mSKP;
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
    public List<DataSKPTahun> getListSKP() {
        return mSKP;
    }
    public void setSKP(List<DataSKPTahun> SKP) {
        mSKP = SKP;
    }

}
