package com.rmf.kuhcjr.Data;

import com.google.gson.annotations.SerializedName;

public class SisaCuti {
    @SerializedName("status")
    private String status;
    @SerializedName("sisa_cuti")
    private int sisa_cuti;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSisa_cuti() {
        return sisa_cuti;
    }

    public void setSisa_cuti(int sisa_cuti) {
        this.sisa_cuti = sisa_cuti;
    }
}
