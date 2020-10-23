package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetKegiatan {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataKegiatan> listDataKegiatan;
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
    public List<DataKegiatan> getListDataKegiatan() {
        return listDataKegiatan;
    }
    public void setListDataKegiatan(List<DataKegiatan> listDataKegiatan) {
        this.listDataKegiatan = listDataKegiatan;
    }
}
