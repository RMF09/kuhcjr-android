package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class DataPengumuman {
    @SerializedName("id")
    private int id;
    @SerializedName("isi")
    private String isi;
    @SerializedName("file")
    private String file;

    public DataPengumuman(int id, String isi, String file) {
        this.id = id;
        this.isi = isi;
        this.file = file;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsi() {
        return isi;
    }

    public void setIsi(String isi) {
        this.isi = isi;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }
}
