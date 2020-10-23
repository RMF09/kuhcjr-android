package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataPimpinan {

    @SerializedName("nama")
    private String nama;
    @SerializedName("nik")
    private String nik;
    @SerializedName("jabatan")
    private String jabatan;


    public DataPimpinan() {
    }

    public DataPimpinan(String nama, String nik, String jabatan) {
        this.nama = nama;
        this.nik = nik;
        this.jabatan = jabatan;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }
}
