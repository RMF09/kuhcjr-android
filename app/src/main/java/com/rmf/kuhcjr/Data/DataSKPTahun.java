package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataSKPTahun {

    @SerializedName("id")
    private int id;
    @SerializedName("tahun")
    private String tahun;


    public DataSKPTahun() {
    }

    public DataSKPTahun(int id, String tahun) {
        this.id = id;
        this.tahun = tahun;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }
}
