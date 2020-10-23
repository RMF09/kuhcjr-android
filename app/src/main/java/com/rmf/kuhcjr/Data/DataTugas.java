package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataTugas {

    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String username;
    @SerializedName("tahun_id")
    private int tahun_id;
    @SerializedName("kegiatan")
    private String kegiatan;
    @SerializedName("target_kuant")
    private int target_kuant;
    @SerializedName("target_output")
    private String target_output;
    @SerializedName("target_kual")
    private int target_kual;
    @SerializedName("target_waktu")
    private int target_waktu;
    @SerializedName("target_satuan_waktu")
    private String target_satuan_waktu;
    @SerializedName("target_biaya")
    private int target_biaya;


    public DataTugas() {
    }

    public DataTugas(int id, String username, int tahun_id, String kegiatan, int target_kuant, String target_output, int target_kual, int target_waktu, String target_satuan_waktu, int target_biaya) {
        this.id = id;
        this.username = username;
        this.tahun_id = tahun_id;
        this.kegiatan = kegiatan;
        this.target_kuant = target_kuant;
        this.target_output = target_output;
        this.target_kual = target_kual;
        this.target_waktu = target_waktu;
        this.target_satuan_waktu = target_satuan_waktu;
        this.target_biaya = target_biaya;
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

    public int getTahun_id() {
        return tahun_id;
    }

    public void setTahun_id(int tahun_id) {
        this.tahun_id = tahun_id;
    }

    public String getKegiatan() {
        return kegiatan;
    }

    public void setKegiatan(String kegiatan) {
        this.kegiatan = kegiatan;
    }

    public int getTarget_kuant() {
        return target_kuant;
    }

    public void setTarget_kuant(int target_kuant) {
        this.target_kuant = target_kuant;
    }

    public String getTarget_output() {
        return target_output;
    }

    public void setTarget_output(String target_output) {
        this.target_output = target_output;
    }

    public int getTarget_kual() {
        return target_kual;
    }

    public void setTarget_kual(int target_kual) {
        this.target_kual = target_kual;
    }

    public int getTarget_waktu() {
        return target_waktu;
    }

    public void setTarget_waktu(int target_waktu) {
        this.target_waktu = target_waktu;
    }

    public String getTarget_satuan_waktu() {
        return target_satuan_waktu;
    }

    public void setTarget_satuan_waktu(String target_satuan_waktu) {
        this.target_satuan_waktu = target_satuan_waktu;
    }

    public int getTarget_biaya() {
        return target_biaya;
    }

    public void setTarget_biaya(int target_biaya) {
        this.target_biaya = target_biaya;
    }
}
