package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetLembur {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataLembur> listDataLembur;
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
    public List<DataLembur> getListDataLembur() {
        return listDataLembur;
    }
    public void setListDataLembur(List<DataLembur> listDataLembur) {
        this.listDataLembur = listDataLembur;
    }
}
