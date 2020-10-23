package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPostPutFileKegiatan {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataFileKegiatan> listDataFileKegiatan;
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
    public List<DataFileKegiatan> getListDataFileKegiatan() {
        return listDataFileKegiatan;
    }
    public void setListDataFileKegiatan(List<DataFileKegiatan> listDataFileKegiatan) {
        this.listDataFileKegiatan = listDataFileKegiatan;
    }
}
