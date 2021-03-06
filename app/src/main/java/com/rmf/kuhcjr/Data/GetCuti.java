package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetCuti {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataCuti> listDataCuti;
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
    public List<DataCuti> getListDataCuti() {
        return listDataCuti;
    }
    public void setListDataCuti(List<DataCuti> listDataCuti) {
        this.listDataCuti = listDataCuti;
    }
}
