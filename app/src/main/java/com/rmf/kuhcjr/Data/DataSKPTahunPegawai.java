package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataSKPTahunPegawai {

    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String username;
    @SerializedName("tahun")
    private String tahun;
    @SerializedName("tahun_id")
    private int tahun_id;
    @SerializedName("rerata_pengukuran")
    private float rerata_pengukuran;
    @SerializedName("rerata_penilaian")
    private float rerata_penilaian;
    @SerializedName("status")
    private int status;
    @SerializedName("status_penilaian")
    private int status_penilaian;
    @SerializedName("tgl_pengajuan")
    private String tgl_pengajuan;
    @SerializedName("tgl_penilaian")
    private String tgl_penilaian;
    @SerializedName("created_at")
    private String created_at;
    @SerializedName("updated_at")
    private String updated_at;

    public DataSKPTahunPegawai() {
    }

    public DataSKPTahunPegawai(int id, String username, String tahun, int tahun_id, float rerata_pengukuran, float rerata_penilaian, int status, int status_penilaian, String tgl_pengajuan, String tgl_penilaian, String created_at, String updated_at) {
        this.id = id;
        this.username = username;
        this.tahun = tahun;
        this.tahun_id = tahun_id;
        this.rerata_pengukuran = rerata_pengukuran;
        this.rerata_penilaian = rerata_penilaian;
        this.status = status;
        this.status_penilaian = status_penilaian;
        this.tgl_pengajuan = tgl_pengajuan;
        this.tgl_penilaian = tgl_penilaian;
        this.created_at = created_at;
        this.updated_at = updated_at;
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

    public String getTahun() {
        return tahun;
    }

    public void setTahun(String tahun) {
        this.tahun = tahun;
    }

    public int getTahun_id() {
        return tahun_id;
    }

    public void setTahun_id(int tahun_id) {
        this.tahun_id = tahun_id;
    }

    public float getRerata_pengukuran() {
        return rerata_pengukuran;
    }

    public void setRerata_pengukuran(float rerata_pengukuran) {
        this.rerata_pengukuran = rerata_pengukuran;
    }

    public float getRerata_penilaian() {
        return rerata_penilaian;
    }

    public void setRerata_penilaian(float rerata_penilaian) {
        this.rerata_penilaian = rerata_penilaian;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getStatus_penilaian() {
        return status_penilaian;
    }

    public void setStatus_penilaian(int status_penilaian) {
        this.status_penilaian = status_penilaian;
    }

    public String getTgl_pengajuan() {
        return tgl_pengajuan;
    }

    public void setTgl_pengajuan(String tgl_pengajuan) {
        this.tgl_pengajuan = tgl_pengajuan;
    }

    public String getTgl_penilaian() {
        return tgl_penilaian;
    }

    public void setTgl_penilaian(String tgl_penilaian) {
        this.tgl_penilaian = tgl_penilaian;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}
