package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PostPutPeminjamanKendaraan {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataPeminjamanKendaraan> mDataPeminjamanKendaraan;
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
    public List<DataPeminjamanKendaraan> getDataPeminjamanKendaraan() {
        return mDataPeminjamanKendaraan;
    }
    public void setDataPeminjamanKendaraan(List<DataPeminjamanKendaraan> dataPeminjamanKendaraan) {
        mDataPeminjamanKendaraan = dataPeminjamanKendaraan;
    }

}
