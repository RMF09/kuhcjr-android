package com.rmf.kuhcjr.Utils;

public class StatusUtils {

    public static String checkStatus(int status){
        String hasil="";

        switch (status){

            case 1: hasil = "Diproses";
                break;
            case 2: hasil = "Disetujui";
                break;
            case 3: hasil = "Ditolak";
                break;
            default: hasil = "Pending";
                break;
        }
        return hasil;
    }

    public static String checkWarnaFont(int status){
        String hasil="#383838";

        switch (status){
            case 0: hasil = "#383838";
                break;
            default: hasil ="#FFFFFF";
                break;
        }
        return hasil;
    }

    public static String checkStatusWarna(int status){
        String hasil="#ffc107";

        switch (status){
            case 1: hasil = "#17a2b8";
                break;
            case 2: hasil = "#28a745";
                break;
            case 3: hasil = "#dc3545";
                break;
            default: hasil ="#ffc107";
                break;
        }
        return hasil;
    }
}
