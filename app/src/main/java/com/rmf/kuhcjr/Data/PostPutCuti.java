package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostPutCuti {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataCuti> mCuti;
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
    public List<DataCuti> getListCuti() {
        return mCuti;
    }
    public void setListCuti(List<DataCuti> Cuti) {
        mCuti = Cuti;
    }

}
