package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataHasilPenilaian {

    @SerializedName("tahun_id")
    private String tahun_id;

    @SerializedName("username")
    private String username;

    public DataHasilPenilaian() {
    }

    public DataHasilPenilaian(String tahun_id, String username) {
        this.tahun_id = tahun_id;
        this.username = username;
    }

    public String getTahun_id() {
        return tahun_id;
    }

    public void setTahun_id(String tahun_id) {
        this.tahun_id = tahun_id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
