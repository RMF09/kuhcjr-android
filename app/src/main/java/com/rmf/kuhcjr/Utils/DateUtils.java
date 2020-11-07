package com.rmf.kuhcjr.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtils {
    public static String HARI_KOMA_TGL_BLN_TAHUN = "EEEE, dd MMMM yyyy";
    public static String MIN_TGL_BLN_TAHUN = "dd-MM-yyy";

    public static String getDayName(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        return output.format(date);
    }

    public static String getTime(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date =null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("dd-MM-yyyy");
        return output.format(date);

    }

    public static String convertDateWSlash(String tgl,String pattern){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
        return output.format(date);
    }

    public static String getWaktuAbsen(String waktu){

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date =null;
        try {
            date = simpleDateFormat.parse(waktu);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("HH:mm");
        return output.format(date);

    }
    public static String getTimeNow(){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return  sdf.format(new Date());
    }

}
