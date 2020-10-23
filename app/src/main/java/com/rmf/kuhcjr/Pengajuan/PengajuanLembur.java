package com.rmf.kuhcjr.Pengajuan;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Adapter.AdapterRVDataLembur;
import com.rmf.kuhcjr.Adapter.AdapterRVPeminjamanKendaraan;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Auth;
import com.rmf.kuhcjr.Data.DataLembur;
import com.rmf.kuhcjr.Data.DataPeminjamanKendaraan;
import com.rmf.kuhcjr.Data.GetLembur;
import com.rmf.kuhcjr.Data.GetPeminjamanKendaraan;
import com.rmf.kuhcjr.Data.PostPutLembur;
import com.rmf.kuhcjr.PeminjamanMobil;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.SharedPrefs;
import com.rmf.kuhcjr.TambahDataPengajuan.TambahDataPengajuanLembur;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengajuanLembur extends AppCompatActivity {

    public boolean pindahHalaman=false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnAdd;
    private ProgressBar progressBar;


    private RecyclerView rv;
    private AdapterRVDataLembur adapterRVDataLembur;
    private List<DataLembur> list = new ArrayList<>();

    private ApiInterface mApiInterface;

    //Masalah Jaringan Layout
    private LinearLayout linearMasalahJaringan;
    private TextView textERR,textMuatUlang;

    //Belum Ada data
    private LinearLayout linearBelumAdaData;
    private TextView textBelumAdaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengajuan_lembur);

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
        refreshData();
    }

    private void initialUI(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_tambah_pengajuan);
        progressBar = findViewById(R.id.progress);
        rv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                refreshData();
            }
        });


        //Masalah Jaringan
        linearMasalahJaringan = (LinearLayout) findViewById(R.id.layout_masalah_jaringan);
        textERR = (TextView) findViewById(R.id.text_error_code);
        textMuatUlang = (TextView) findViewById(R.id.text_muat_ulang_data);

        textMuatUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                refreshData();
            }
        });


        //Belum Ada Data
        linearBelumAdaData= (LinearLayout) findViewById(R.id.layout_belum_ada_data);
        textBelumAdaData = (TextView) findViewById(R.id.text_belum_ada_data);

        this.actionUI();
//        auth();

    }



//    private void auth(){
//
//
//        boolean s= Auth.getInstance(PengajuanLembur.this).authenticating("pegawai");
////        Toast.makeText(this, "hasil auth "+String.valueOf(s), Toast.LENGTH_SHORT).show();
//    }

    @SuppressLint("RestrictedApi")
    public void refreshData() {

        btnAdd.setVisibility(View.GONE);
        linearMasalahJaringan.setVisibility(View.GONE);
        linearBelumAdaData.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);

        String username = SharedPrefs.getInstance(this).LoggedInUser();

        final Call<PostPutLembur> LemburCall = mApiInterface.getLembur(username,"get");
        LemburCall.enqueue(new Callback<PostPutLembur>() {
            @Override
            public void onResponse(Call<PostPutLembur> call, Response<PostPutLembur>
                    response) {

                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    String message = response.body().getMessage();
//                    Toast.makeText(PengajuanLembur.this, status, Toast.LENGTH_SHORT).show();
                    if(status.equals("berhasil")){

                        List<DataLembur> LemburList = response.body().getListLembur();
                        Collections.reverse(LemburList);
                        Log.d("Retrofit Get", "Jumlah data Lembur: " +
                                String.valueOf(LemburList.size()));

                            adapterRVDataLembur = new AdapterRVDataLembur(LemburList,PengajuanLembur.this);
//                            adapterRVDataLembur.notifyDataSetChanged();
//                            adapterRVDataLembur.setHasStableIds(true);
                            rv.setAdapter(adapterRVDataLembur);
                            rv.setVisibility(View.VISIBLE);
                            btnAdd.setVisibility(View.VISIBLE);
                        LayoutAnimationController layoutAnimationController =AnimationUtils.loadLayoutAnimation(PengajuanLembur.this,R.anim.layout_animation);
                        rv.setLayoutAnimation(layoutAnimationController);
                        rv.getAdapter().notifyDataSetChanged();
                        rv.scheduleLayoutAnimation();

                    }
                    else{
                        linearBelumAdaData.setVisibility(View.VISIBLE);
                        textBelumAdaData.setText("Belum ada data pengajuan lembur, Tap tombol + untuk pengajuan baru");
                        btnAdd.setVisibility(View.VISIBLE);
                    }
                }else{

                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada DB server");
                    progressBar.setVisibility(View.GONE);


                }
            }

            @Override
            public void onFailure(Call<PostPutLembur> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());

                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText("ERR : "+t.getMessage());
                progressBar.setVisibility(View.GONE);
                rv.setVisibility(View.GONE);

            }
        });
    }


    private void actionUI(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pindahHalaman=true;
                startActivity(new Intent(PengajuanLembur.this, TambahDataPengajuanLembur.class));
            }
        });
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
        if(pindahHalaman){
            refreshData();
            pindahHalaman=false;
        }
    }
}
