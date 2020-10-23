package com.rmf.kuhcjr;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class DetailPeminjamanMobil extends AppCompatActivity {


    private String kodeKendaraan,merk,nomorPolisi,tanggal,waktu,kategori;
    //UI
    private TextView textHeader;
    private TextView textKodeKendaraan,textMerk,textNomorPolisi,textTanggal,textWaktu, fieldTanggal,fieldWaktu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_peminjaman_mobil);
        //        Sistem
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }

        kodeKendaraan = getIntent().getStringExtra("kode_kendaraan");
        merk = getIntent().getStringExtra("merk");
        nomorPolisi = getIntent().getStringExtra("nomor_polisi");
        tanggal = getIntent().getStringExtra("tanggal");
        waktu= getIntent().getStringExtra("waktu");
        kategori= getIntent().getStringExtra("kategori");

        initialUI();
    }
    private void initialUI(){

        textHeader = (TextView) findViewById(R.id.text_header);

        textKodeKendaraan =(TextView) findViewById(R.id.kode_kendaraan);
        textMerk =(TextView) findViewById(R.id.merk);
        textNomorPolisi=(TextView) findViewById(R.id.nomor_polisi);
        textTanggal =(TextView) findViewById(R.id.tanggal_peminjaman);
        textWaktu =(TextView) findViewById(R.id.waktu_peminjaman);

        fieldTanggal =(TextView) findViewById(R.id.tanggal);
        fieldWaktu =(TextView) findViewById(R.id.waktu);

        if(kategori.equals("pengembalian")){
            textHeader.setText("Detail Pengembalian Mobil");
            fieldTanggal.setText("Tanggal Pengembalian");
            fieldWaktu.setText("Waktu Pengembalian");
        }

        textKodeKendaraan.setText(kodeKendaraan);
        textMerk.setText(merk);
        textNomorPolisi.setText(nomorPolisi);
        textTanggal.setText(tanggal);
        textWaktu.setText(waktu);
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
