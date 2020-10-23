package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetPeminjamanKendaraan {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataPeminjamanKendaraan> listDataPeminjamanKendaraan;
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
    public List<DataPeminjamanKendaraan> getListDataPeminjamanKendaraan() {
        return listDataPeminjamanKendaraan;
    }
    public void setListDataPeminjamanKendaraan(List<DataPeminjamanKendaraan> listDataPeminjamanKendaraan) {
        this.listDataPeminjamanKendaraan= listDataPeminjamanKendaraan;
    }
}
