package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataCetak {

    @SerializedName("dari_tanggal")
    private String dari_tanggal;
    @SerializedName("sampai_tanggal")
    private String sampai_tanggal;
    @SerializedName("username")
    private String username;

    public DataCetak() {
    }

    public DataCetak(String dari_tanggal, String sampai_tanggal, String username) {
        this.dari_tanggal = dari_tanggal;
        this.sampai_tanggal = sampai_tanggal;
        this.username = username;
    }

    public String getDari_tanggal() {
        return dari_tanggal;
    }

    public void setDari_tanggal(String dari_tanggal) {
        this.dari_tanggal = dari_tanggal;
    }

    public String getSampai_tanggal() {
        return sampai_tanggal;
    }

    public void setSampai_tanggal(String sampai_tanggal) {
        this.sampai_tanggal = sampai_tanggal;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
