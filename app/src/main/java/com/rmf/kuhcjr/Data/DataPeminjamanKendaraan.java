package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataPeminjamanKendaraan {

    @SerializedName("id")
    private int id;
    @SerializedName("id_kendaraan")
    private int id_kendaraan;
    @SerializedName("username")
    private String username;
    @SerializedName("tanggal")
    private String tanggal;
    @SerializedName("time")
    private String time;
    @SerializedName("date_added")
    private String date_added;
    @SerializedName("date_updated")
    private String date_updated;
    @SerializedName("tanggal_pengembalian")
    private String tanggal_pengembalian;
    @SerializedName("time_pengembalian")
    private String time_pengembalian;
    @SerializedName("peruntukan")
    private String peruntukan;
    @SerializedName("status")
    private int status;

    @SerializedName("kode_kendaraan")
    private String kode_kendaraan;
    @SerializedName("merk")
    private String merk;
    @SerializedName("nomor_polisi")
    private String nomor_polisi;

    @SerializedName("foto")
    private String foto;

    public  DataPeminjamanKendaraan(){}

    public DataPeminjamanKendaraan(int id, int id_kendaraan, String username, String tanggal, String time, String date_added, String date_updated, String tanggal_pengembalian, String time_pengembalian, String peruntukan, int status, String kode_kendaraan, String merk, String nomor_polisi, String foto) {
        this.id = id;
        this.id_kendaraan = id_kendaraan;
        this.username = username;
        this.tanggal = tanggal;
        this.time = time;
        this.date_added = date_added;
        this.date_updated = date_updated;
        this.tanggal_pengembalian = tanggal_pengembalian;
        this.time_pengembalian = time_pengembalian;
        this.peruntukan = peruntukan;
        this.status = status;
        this.kode_kendaraan = kode_kendaraan;
        this.merk = merk;
        this.nomor_polisi = nomor_polisi;
        this.foto = foto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_kendaraan() {
        return id_kendaraan;
    }

    public void setId_kendaraan(int id_kendaraan) {
        this.id_kendaraan = id_kendaraan;
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

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
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

    public String getTanggal_pengembalian() {
        return tanggal_pengembalian;
    }

    public void setTanggal_pengembalian(String tanggal_pengembalian) {
        this.tanggal_pengembalian = tanggal_pengembalian;
    }

    public String getTime_pengembalian() {
        return time_pengembalian;
    }

    public void setTime_pengembalian(String time_pengembalian) {
        this.time_pengembalian = time_pengembalian;
    }

    public String getPeruntukan() {
        return peruntukan;
    }

    public void setPeruntukan(String peruntukan) {
        this.peruntukan = peruntukan;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getKode_kendaraan() {
        return kode_kendaraan;
    }

    public void setKode_kendaraan(String kode_kendaraan) {
        this.kode_kendaraan = kode_kendaraan;
    }

    public String getMerk() {
        return merk;
    }

    public void setMerk(String merk) {
        this.merk = merk;
    }

    public String getNomor_polisi() {
        return nomor_polisi;
    }

    public void setNomor_polisi(String nomor_polisi) {
        this.nomor_polisi = nomor_polisi;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }
}
