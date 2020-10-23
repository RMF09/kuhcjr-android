package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataUserLogin {

    @SerializedName("password")
    private String password;
    @SerializedName("username")
    private String username;
    @SerializedName("nama")
    private String nama;
    @SerializedName("instansi")
    private String instansi;
    @SerializedName("satuan_kerja")
    private String satuan_kerja;
    @SerializedName("nip")
    private String nip;
    @SerializedName("jabatan")
    private String jabatan;
    @SerializedName("unit_kerja")
    private String unit_kerja;
    @SerializedName("sub_unit_kerja")
    private String sub_unit_kerja;
    @SerializedName("alamat")
    private String alamat;
    @SerializedName("nomor_identitas")
    private String nomor_identitas;
    @SerializedName("no_tlp")
    private String no_tlp;
    @SerializedName("pendidikan_terakhir")
    private String pendidikan_terakhir;
    @SerializedName("no_passport")
    private String no_passport;
    @SerializedName("nama_atasan")
    private String nama_atasan;
    @SerializedName("nip_atasan")
    private String nip_atasan;
    @SerializedName("jabatan_atasan")
    private String jabatan_atasan;
    @SerializedName("file")
    private String file;

    public DataUserLogin() {
    }

    public DataUserLogin(String password, String username, String nama, String instansi, String satuan_kerja, String nip, String jabatan, String unit_kerja, String sub_unit_kerja, String alamat, String nomor_identitas, String no_tlp, String pendidikan_terakhir, String no_passport, String nama_atasan, String nip_atasan, String jabatan_atasan, String file) {
        this.password = password;
        this.username = username;
        this.nama = nama;
        this.instansi = instansi;
        this.satuan_kerja = satuan_kerja;
        this.nip = nip;
        this.jabatan = jabatan;
        this.unit_kerja = unit_kerja;
        this.sub_unit_kerja = sub_unit_kerja;
        this.alamat = alamat;
        this.nomor_identitas = nomor_identitas;
        this.no_tlp = no_tlp;
        this.pendidikan_terakhir = pendidikan_terakhir;
        this.no_passport = no_passport;
        this.nama_atasan = nama_atasan;
        this.nip_atasan = nip_atasan;
        this.jabatan_atasan = jabatan_atasan;
        this.file = file;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public String getInstansi() {
        return instansi;
    }

    public void setInstansi(String instansi) {
        this.instansi = instansi;
    }

    public String getSatuan_kerja() {
        return satuan_kerja;
    }

    public void setSatuan_kerja(String satuan_kerja) {
        this.satuan_kerja = satuan_kerja;
    }

    public String getNip() {
        return nip;
    }

    public void setNip(String nip) {
        this.nip = nip;
    }

    public String getJabatan() {
        return jabatan;
    }

    public void setJabatan(String jabatan) {
        this.jabatan = jabatan;
    }

    public String getUnit_kerja() {
        return unit_kerja;
    }

    public void setUnit_kerja(String unit_kerja) {
        this.unit_kerja = unit_kerja;
    }

    public String getSub_unit_kerja() {
        return sub_unit_kerja;
    }

    public void setSub_unit_kerja(String sub_unit_kerja) {
        this.sub_unit_kerja = sub_unit_kerja;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getNomor_identitas() {
        return nomor_identitas;
    }

    public void setNomor_identitas(String nomor_identitas) {
        this.nomor_identitas = nomor_identitas;
    }

    public String getNo_tlp() {
        return no_tlp;
    }

    public void setNo_tlp(String no_tlp) {
        this.no_tlp = no_tlp;
    }

    public String getPendidikan_terakhir() {
        return pendidikan_terakhir;
    }

    public void setPendidikan_terakhir(String pendidikan_terakhir) {
        this.pendidikan_terakhir = pendidikan_terakhir;
    }

    public String getNo_passport() {
        return no_passport;
    }

    public void setNo_passport(String no_passport) {
        this.no_passport = no_passport;
    }

    public String getNama_atasan() {
        return nama_atasan;
    }

    public void setNama_atasan(String nama_atasan) {
        this.nama_atasan = nama_atasan;
    }

    public String getNip_atasan() {
        return nip_atasan;
    }

    public void setNip_atasan(String nip_atasan) {
        this.nip_atasan = nip_atasan;
    }

    public String getJabatan_atasan() {
        return jabatan_atasan;
    }

    public void setJabatan_atasan(String jabatan_atasan) {
        this.jabatan_atasan = jabatan_atasan;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
