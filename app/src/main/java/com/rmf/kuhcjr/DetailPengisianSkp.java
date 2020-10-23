package com.rmf.kuhcjr;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.URLUtil;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataHasilPenilaian;
import com.rmf.kuhcjr.Data.DataKegiatan;
import com.rmf.kuhcjr.Data.DataSKPTahunPegawai;
import com.rmf.kuhcjr.Data.GetHasilPenilaian;
import com.rmf.kuhcjr.Data.GetKegiatan;
import com.rmf.kuhcjr.Data.GetSKP;
import com.rmf.kuhcjr.TambahDataPengajuan.TambahDataPengajuanCuti;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPengisianSkp extends AppCompatActivity {


    private String tahun,pt;
    private int status,tahun_id,id;
    private TextView textTahun,textPT,textStatus;
    private ImageView imageIsiSKP,imageHP,imagePrint;
    private ApiInterface mApiInterface;
    private WebView webView;
    private String url =ApiClient.BASE_URL+"Pegawai/cetak_skp_mobile?";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengisian_skp);

        tahun = getIntent().getStringExtra("tahun");
        pt = getIntent().getStringExtra("pt");
        status = getIntent().getIntExtra("status",0);
        tahun_id = getIntent().getIntExtra("tahun_id",0);
        id = getIntent().getIntExtra("id",0);

//        Toast.makeText(this, "tahun_id : "+tahun_id, Toast.LENGTH_SHORT).show();

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



        initialUI();
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(DetailPengisianSkp.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(DetailPengisianSkp.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
//            pickFile();
            webView.loadUrl(url);
            Toast.makeText(DetailPengisianSkp.this, "Harap tunggu, sedang mendownload file...", Toast.LENGTH_SHORT).show();


        }
    }

    public void initialUI(){
        textTahun = findViewById(R.id.tahun_penilaian);
        textPT = findViewById(R.id.perubahan_terakhir);
        textStatus = findViewById(R.id.status);

        imageIsiSKP = findViewById(R.id.isi_skp);
        imageHP =  findViewById(R.id.hasil_penilaian);
        imagePrint = findViewById(R.id.print_skp);

        textTahun.setText(tahun);
        textPT.setText(pt);
        textStatus.setText(checkStatus(status));
        textStatus.setTextColor(Color.parseColor(checkStatusWarna(status)));

        if(status>2){
            imageIsiSKP.setVisibility(View.GONE);
        }
        if(status !=4){
            imageHP.setVisibility(View.GONE);
        }

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        this.actionUI();

        webView = findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setLoadsImagesAutomatically(true);
        webView.getSettings().setJavaScriptEnabled(true);
        String username = SharedPrefs.getInstance(this).LoggedInUser();

        url +="username="+username+"&tahun_id="+tahun_id;
//        Toast.makeText(this, "username : "+username+ "tahun_id :"+tahun_id+" url :"+url, Toast.LENGTH_SHORT).show();

//        String postData="username="+username+"&tahun_id="+String.valueOf(tahun_id);
//        try {
//            postData = "username=" + URLEncoder.encode("pegawai", "UTF-8") + "&tahun_id=" + URLEncoder.encode("1", "UTF-8");
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        webView.postUrl(url, postData.getBytes());
//        webView.loadUrl(url);
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                request.setMimeType(mimetype);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie",cookies);
                request.addRequestHeader("User-Agent",userAgent);
                request.setDescription("Downloading File...");
                request.setTitle("hasil-penilaian-skp.pdf");
                request.allowScanningByMediaScanner();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,"hasil-penilaian-skp.pdf"
                );
                DownloadManager dm =  (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                dm.enqueue(request);
                
                Toast.makeText(DetailPengisianSkp.this, "Silahkan Lihat Proses Download di Notifikasi", Toast.LENGTH_LONG).show();
            }
        });
    }
    private void actionUI(){
        imageHP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keHasilPenilaian();
            }
        });
        imageIsiSKP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(status==0){

                    Intent intent = new Intent(DetailPengisianSkp.this,IsiPenilaian.class);
                    intent.putExtra("tahun_id",tahun_id);
                    intent.putExtra("tahun",tahun);

                    startActivity(intent);
                }
                if(status==1 || status==2){
                    Toast.makeText(DetailPengisianSkp.this, "Mengalihkan halaman...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(DetailPengisianSkp.this,AjukanPenilaian.class);
                    intent.putExtra("tahun_id",tahun_id);
                    startActivity(intent);
                }
            }
        });
        imagePrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
            }
        });
    }

    private void keHasilPenilaian(){
        Toast.makeText(this, "Mengalihkan ke Hasil Penilaian", Toast.LENGTH_SHORT).show();
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetHasilPenilaian> HasilPenilaianCall = mApiInterface.getHasilPenilaian(username,tahun_id,"hasilPenilaian");
        HasilPenilaianCall.enqueue(new Callback<GetHasilPenilaian>() {
            @Override
            public void onResponse(Call<GetHasilPenilaian> call, Response<GetHasilPenilaian>
                    response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        String dataHTML = response.body().getIsiHTML();
                        Intent intent = new Intent(DetailPengisianSkp.this,HasilPenilaian.class);
                        intent.putExtra("data",dataHTML);
                        startActivity(intent);


                    }else{
                        Toast.makeText(DetailPengisianSkp.this,"Gagal mengambil data",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(DetailPengisianSkp.this,"Terjadi masalah pada Server",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetHasilPenilaian> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void refreshData(){
        Call<GetSKP> SKPCall = mApiInterface.getDetailSKP("getDetailSKP",id);
        SKPCall.enqueue(new Callback<GetSKP>() {
            @Override
            public void onResponse(Call<GetSKP> call, Response<GetSKP> response) {
                if(response.isSuccessful()){
                    String statuss = response.body().getStatus();
                    if(statuss.equals("berhasil")){
                        List<DataSKPTahunPegawai> list = response.body().getListSKP();
                        textPT.setText(getDayName(list.get(0).getUpdated_at()));
                        status = list.get(0).getStatus();
                        textStatus.setText(checkStatus(status));
                        textStatus.setTextColor(Color.parseColor(checkStatusWarna(status)));

                        imageHP.setVisibility(View.VISIBLE);
                        imageIsiSKP.setVisibility(View.VISIBLE);
                        if(status>2){
                            imageIsiSKP.setVisibility(View.GONE);
                        }else
                        if(status !=4){
                            imageHP.setVisibility(View.GONE);
                        }
                    }
                    else{
                        Toast.makeText(DetailPengisianSkp.this, "Data mungkin tidak ada", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<GetSKP> call, Throwable t) {
                Toast.makeText(DetailPengisianSkp.this, "ERR : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String checkStatusWarna(int status){
        String hasil="#ffc107";

        switch (status){
            case 0: hasil ="#007bff";
                break;
            case 3: hasil = "#28a745";
                break;
            case 4: hasil = "#28a745";
                break;
            default: hasil ="#ffc107";
                break;
        }
        return hasil;
    }
    private String checkStatus(int status){
        String hasil="";

        switch (status){

            case 1: hasil = "proses pengukuran";
                break;
            case 2: hasil = "pengajuan proses penilaian";
                break;
            case 3: hasil = "proses penilaian";
                break;
            case 4: hasil = "selesai";
                break;
            default: hasil = "draft";
                break;
        }
        return hasil;
    }

    private String getDayName(String tgl){
        if(tgl==null){
            return "Belum Mengisi";
        }else{

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =null;
            try {
                date = simpleDateFormat.parse(tgl);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat output = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
            String akhir = output.format(date);
            akhir += " WAS";

            return akhir;
        }

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

    @Override
    protected void onResume() {
        super.onResume();
        refreshData();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == 1) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                webView.loadUrl(url);
                Toast.makeText(DetailPengisianSkp.this, "Harap tunggu, sedang mendownload file...", Toast.LENGTH_SHORT).show();

            }
            else {
                Toast.makeText(DetailPengisianSkp.this,
                        "Gallery Permission Denied, Harap izinkan aplikasi untuk menulis ke penyimpanan",
                        Toast.LENGTH_SHORT)
                        .show();

            }
        }

    }

}