package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataKegiatan {

    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String username;
    @SerializedName("tanggal")
    private String tanggal;
    @SerializedName("kegiatan")
    private String kegiatan;
    @SerializedName("hasil")
    private String hasil;
    @SerializedName("jumlah")
    private String jumlah;
    @SerializedName("satuan")
    private String satuan;
    @SerializedName("keterangan")
    private String keterangan;
    @SerializedName("date_added")
    private String date_added;
    @SerializedName("date_updated")
    private String date_updated;

    public DataKegiatan() {
    }

    public DataKegiatan(int id, String username, String tanggal, String kegiatan, String hasil, String jumlah, String satuan, String keterangan, String date_added, String date_updated) {
        this.id = id;
        this.username = username;
        this.tanggal = tanggal;
        this.kegiatan = kegiatan;
        this.hasil = hasil;
        this.jumlah = jumlah;
        this.satuan = satuan;
        this.keterangan = keterangan;
        this.date_added = date_added;
        this.date_updated = date_updated;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(String kegiatan) {
        this.kegiatan = kegiatan;
    }

    public String getHasil() {
        return hasil;
    }

    public void setHasil(String hasil) {
        this.hasil = hasil;
    }

    public String getJumlah() {
        return jumlah;
    }

    public void setJumlah(String jumlah) {
        this.jumlah = jumlah;
    }

    public String getSatuan() {
        return satuan;
    }

    public void setSatuan(String satuan) {
        this.satuan = satuan;
    }

    public String getKeterangan() {
        return keterangan;
    }

    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }

    public String getDate_updated() {
        return date_updated;
    }

    public void setDate_updated(String date_updated) {
        this.date_updated = date_updated;
    }
}
