package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPerjalananDinas {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataPerjalananDinas> listDataPerjalananDinas;
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
    public List<DataPerjalananDinas> getListDataPerjalananDinas() {
        return listDataPerjalananDinas;
    }
    public void setListDataPerjalananDinas(List<DataPerjalananDinas> listDataPerjalananDinas) {
        this.listDataPerjalananDinas = listDataPerjalananDinas;
    }
}
