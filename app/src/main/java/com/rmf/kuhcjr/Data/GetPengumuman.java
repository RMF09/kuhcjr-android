package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPengumuman {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataPengumuman> listPengumuman;
    @SerializedName("message")
    String message;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<DataPengumuman> getListPengumuman() {
        return listPengumuman;
    }

    public void setListPengumuman(List<DataPengumuman> listPengumuman) {
        this.listPengumuman = listPengumuman;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
