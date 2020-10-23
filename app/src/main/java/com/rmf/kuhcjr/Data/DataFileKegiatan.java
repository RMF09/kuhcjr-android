package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataFileKegiatan {

    @SerializedName("id")
    private int id;
    @SerializedName("id_kegiatan")
    private int id_kegiatan;
    @SerializedName("file")
    private String file;
    @SerializedName("status")
    private String status;
    @SerializedName("date_added")
    private String date_added;
    @SerializedName("date_updated")
    private String date_updated;

    public DataFileKegiatan() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId_kegiatan() {
        return id_kegiatan;
    }

    public void setId_kegiatan(int id_kegiatan) {
        this.id_kegiatan = id_kegiatan;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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
