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
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Adapter.AdapterRVDataLembur;
import com.rmf.kuhcjr.Adapter.AdapterRVDataPerjalananDinas;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataLembur;
import com.rmf.kuhcjr.Data.DataPerjalananDinas;
import com.rmf.kuhcjr.Data.GetLembur;
import com.rmf.kuhcjr.Data.GetPerjalananDinas;
import com.rmf.kuhcjr.Data.PostPutPerjalananDinas;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.SharedPrefs;
import com.rmf.kuhcjr.TambahDataPengajuan.TambahDataPengajuanDinas;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengajuanDinas extends AppCompatActivity {
    public boolean pindahHalaman=false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnAdd;
    private ProgressBar progressBar;
    private RecyclerView rv;
    private AdapterRVDataPerjalananDinas adapterRVDataPerjalananDinas;
    private List<DataPerjalananDinas> list = new ArrayList<>();

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
        setContentView(R.layout.activity_pengajuan_dinas);

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



//        load();
    }

    private void initialUI(){
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_tambah_pengajuan);
        progressBar = (ProgressBar) findViewById(R.id.progress);
        rv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);

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


        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        this.actionUI();
    }
    private void actionUI(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pindahHalaman=true;
                startActivity(new Intent(PengajuanDinas.this, TambahDataPengajuanDinas.class));
            }
        });
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                refreshData();
            }
        });
    }

    @SuppressLint("RestrictedApi")
    public void refreshData() {

        btnAdd.setVisibility(View.GONE);
        linearMasalahJaringan.setVisibility(View.GONE);
        linearBelumAdaData.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<PostPutPerjalananDinas> PDCall = mApiInterface.getDinas(username,"get");
        PDCall.enqueue(new Callback<PostPutPerjalananDinas>() {
            @Override
            public void onResponse(Call<PostPutPerjalananDinas> call, Response<PostPutPerjalananDinas>
                    response) {

                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    String message= response.body().getMessage();

                    if(status.equals("berhasil")){

                        List<DataPerjalananDinas> PDList = response.body().getListPerjalananDinas();
                        Collections.reverse(PDList);

                        adapterRVDataPerjalananDinas = new AdapterRVDataPerjalananDinas(PDList,PengajuanDinas.this);
//                        adapterRVDataPerjalananDinas.notifyDataSetChanged();
//                        adapterRVDataPerjalananDinas.setHasStableIds(true);
                        rv.setAdapter(adapterRVDataPerjalananDinas);
                        rv.setVisibility(View.VISIBLE);
                        btnAdd.setVisibility(View.VISIBLE);

                        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(PengajuanDinas.this,R.anim.layout_animation);
                        rv.setLayoutAnimation(layoutAnimationController);
                        rv.getAdapter().notifyDataSetChanged();
                        rv.scheduleLayoutAnimation();
                    }
                    else{
                        linearBelumAdaData.setVisibility(View.VISIBLE);
                        textBelumAdaData.setText("Belum ada data pengajuan perjalanan dinas, Tap tombol + untuk pengajuan baru");
                        btnAdd.setVisibility(View.VISIBLE);
                    }

                }else{
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada DB server");
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<PostPutPerjalananDinas> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());

                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText("ERR : "+t.getMessage());
                progressBar.setVisibility(View.GONE);
                rv.setVisibility(View.GONE);
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
