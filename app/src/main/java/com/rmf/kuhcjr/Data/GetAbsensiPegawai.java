package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class GetAbsensiPegawai {
    @SerializedName("status")
    String status;
    @SerializedName("result")
    List<DataAbsensiPegawai> listDataAbsensiPegawai;
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
    public List<DataAbsensiPegawai> getListDataAbsensiPegawai() {
        return listDataAbsensiPegawai;
    }
    public void setListDataAbsensiPegawai(List<DataAbsensiPegawai> listDataAbsensiPegawai) {
        this.listDataAbsensiPegawai = listDataAbsensiPegawai;
    }
}
