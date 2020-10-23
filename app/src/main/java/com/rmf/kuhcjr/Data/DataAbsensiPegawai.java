package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataAbsensiPegawai {

    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String username;
    @SerializedName("nama")
    private String nama;
    @SerializedName("tanggal")
    private String tanggal;
    @SerializedName("check_in")
    private String check_in;
    @SerializedName("check_out")
    private String check_out;
    @SerializedName("lat_ci")
    private String lat_ci;
    @SerializedName("lng_ci")
    private String lng_ci;
    @SerializedName("lokasi_ci")
    private String lokasi_ci;
    @SerializedName("lat_co")
    private String lat_co;
    @SerializedName("lng_co")
    private String lng_co;
    @SerializedName("lokasi_co")
    private String lokasi_co;
    @SerializedName("date_added")
    private String date_added;


    public DataAbsensiPegawai() {
    }

    public DataAbsensiPegawai(int id, String username, String nama, String tanggal, String check_in, String check_out, String lat_ci, String lng_ci, String lokasi_ci, String lat_co, String lng_co, String lokasi_co, String date_added) {
        this.id = id;
        this.username = username;
        this.nama = nama;
        this.tanggal = tanggal;
        this.check_in = check_in;
        this.check_out = check_out;
        this.lat_ci = lat_ci;
        this.lng_ci = lng_ci;
        this.lokasi_ci = lokasi_ci;
        this.lat_co = lat_co;
        this.lng_co = lng_co;
        this.lokasi_co = lokasi_co;
        this.date_added = date_added;
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

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getCheck_in() {
        return check_in;
    }

    public void setCheck_in(String check_in) {
        this.check_in = check_in;
    }

    public String getCheck_out() {
        return check_out;
    }

    public void setCheck_out(String check_out) {
        this.check_out = check_out;
    }

    public String getLat_ci() {
        return lat_ci;
    }

    public void setLat_ci(String lat_ci) {
        this.lat_ci = lat_ci;
    }

    public String getLng_ci() {
        return lng_ci;
    }

    public void setLng_ci(String lng_ci) {
        this.lng_ci = lng_ci;
    }

    public String getLokasi_ci() {
        return lokasi_ci;
    }

    public void setLokasi_ci(String lokasi_ci) {
        this.lokasi_ci = lokasi_ci;
    }

    public String getLat_co() {
        return lat_co;
    }

    public void setLat_co(String lat_co) {
        this.lat_co = lat_co;
    }

    public String getLng_co() {
        return lng_co;
    }

    public void setLng_co(String lng_co) {
        this.lng_co = lng_co;
    }

    public String getLokasi_co() {
        return lokasi_co;
    }

    public void setLokasi_co(String lokasi_co) {
        this.lokasi_co = lokasi_co;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }
}
