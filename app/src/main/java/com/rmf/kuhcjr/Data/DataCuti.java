package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataCuti {

    @SerializedName("id")
    private int id;
    @SerializedName("username")
    private String username;
    @SerializedName("nama")
    private String nama;
    @SerializedName("nik")
    private String nik;
    @SerializedName("jabatan")
    private String jabatan;
    @SerializedName("file_pengajuan")
    private String file_pengajuan;
    @SerializedName("uraian")
    private String uraian;
    @SerializedName("tanggal")
    private String tanggal;
    @SerializedName("mulai")
    private String mulai;
    @SerializedName("selesai")
    private String selesai;
    @SerializedName("disetujui")
    private String disetujui;
    @SerializedName("tanggal_surat")
    private String tanggal_surat;
    @SerializedName("file")
    private String file;
    @SerializedName("nomor_surat")
    private String nomor_surat;
    @SerializedName("status")
    private int status;
    @SerializedName("keterangan")
    private String keterangan;
    @SerializedName("date_added")
    private String date_added;
    @SerializedName("date_updated")
    private String date_updated;

    public DataCuti() {
    }

    public DataCuti(int id, String username, String nama, String nik, String jabatan, String file_pengajuan, String uraian, String tanggal, String mulai, String selesai, String disetujui, String tanggal_surat, String file, String nomor_surat, int status, String keterangan, String date_added, String date_updated) {
        this.id = id;
        this.username = username;
        this.nama = nama;
        this.nik = nik;
        this.jabatan = jabatan;
        this.file_pengajuan = file_pengajuan;
        this.uraian = uraian;
        this.tanggal = tanggal;
        this.mulai = mulai;
        this.selesai = selesai;
        this.disetujui = disetujui;
        this.tanggal_surat = tanggal_surat;
        this.file = file;
        this.nomor_surat = nomor_surat;
        this.status = status;
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

    public String getFile_pengajuan() {
        return file_pengajuan;
    }

    public void setFile_pengajuan(String file_pengajuan) {
        this.file_pengajuan = file_pengajuan;
    }

    public String getUraian() {
        return uraian;
    }

    public void setUraian(String uraian) {
        this.uraian = uraian;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getMulai() {
        return mulai;
    }

    public void setMulai(String mulai) {
        this.mulai = mulai;
    }

    public String getSelesai() {
        return selesai;
    }

    public void setSelesai(String selesai) {
        this.selesai = selesai;
    }

    public String getDisetujui() {
        return disetujui;
    }

    public void setDisetujui(String disetujui) {
        this.disetujui = disetujui;
    }

    public String getTanggal_surat() {
        return tanggal_surat;
    }

    public void setTanggal_surat(String tanggal_surat) {
        this.tanggal_surat = tanggal_surat;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getNomor_surat() {
        return nomor_surat;
    }

    public void setNomor_surat(String nomor_surat) {
        this.nomor_surat = nomor_surat;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
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
