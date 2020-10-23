package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPimpinan {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataPimpinan> listDataPimpinan;
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
    public List<DataPimpinan> getListDataPimpinan() {
        return listDataPimpinan;
    }
    public void setListDataPimpinan(List<DataPimpinan> listDataPimpinan) {
        this.listDataPimpinan = listDataPimpinan;
    }
}
