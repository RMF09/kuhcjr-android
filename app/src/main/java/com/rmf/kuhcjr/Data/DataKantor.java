package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataKantor {

    @SerializedName("id")
    private int id;
    @SerializedName("lokasi")
    private String lokasi;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("lat")
    private double lat;
    @SerializedName("lng")
    private double lng;


    public DataKantor() {
    }

    public DataKantor(int id, String lokasi, String alamat, double lat, double lng) {
        this.id = id;
        this.lokasi = lokasi;
        this.alamat = alamat;
        this.lat = lat;
        this.lng = lng;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }
}
