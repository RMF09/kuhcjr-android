package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetKantor {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataKantor> listDataKantor;
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
    public List<DataKantor> getListDataKantor() {
        return listDataKantor;
    }
    public void setListDataKantor(List<DataKantor> listDataKantor) {
        this.listDataKantor = listDataKantor;
    }
}
