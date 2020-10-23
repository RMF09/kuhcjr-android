package com.rmf.kuhcjr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Adapter.AdapterRVDataCuti;
import com.rmf.kuhcjr.Adapter.AdapterRVDataSKPPegawai;
import com.rmf.kuhcjr.Adapter.SpinnerAdapter;
import com.rmf.kuhcjr.Adapter.SpinnerTahunAdapter;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataCuti;
import com.rmf.kuhcjr.Data.DataKantor;
import com.rmf.kuhcjr.Data.DataSKPTahun;
import com.rmf.kuhcjr.Data.DataSKPTahunPegawai;
import com.rmf.kuhcjr.Data.GetSKP;
import com.rmf.kuhcjr.Data.GetSKPTahun;
import com.rmf.kuhcjr.Data.PostPutCuti;
import com.rmf.kuhcjr.Pengajuan.PengajuanCuti;
import com.rmf.kuhcjr.Pengajuan.PengajuanDinas;
import com.rmf.kuhcjr.Pengajuan.PengajuanLembur;
import com.rmf.kuhcjr.TambahDataPengajuan.TambahDataPengajuanCuti;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengisianSkp extends AppCompatActivity {

    public boolean pindahHalaman=false;
    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnAdd;
    private ProgressBar progressBar;
    private RecyclerView rv;
    private AdapterRVDataSKPPegawai adapterRVDataSKPPegawai;
    private List<DataSKPTahunPegawai> list = new ArrayList<>();
    private ApiInterface mApiInterface;

    //Masalah Jaringan Layout
    private LinearLayout linearMasalahJaringan;
    private TextView textERR,textMuatUlang;

    //Belum Ada data
    private LinearLayout linearBelumAdaData;
    private TextView textBelumAdaData;

    private AlertDialog alertDialog;
    Spinner spinner;
    List<DataSKPTahun> listTahun;
    int tahun_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengisian_skp);

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
        initialDialogPilihPenilaian();
        refreshData();
    }

    private void initialUI(){
        btnAdd = (FloatingActionButton) findViewById(R.id.btn_tambah);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        rv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

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

    }
    private void actionUI(){
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
//                startActivity(new Intent(PengajuanCuti.this, TambahDataPengajuanCuti.class));
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
        Call<GetSKP> SKPCall = mApiInterface.getSKP(username,"ambilData");

        SKPCall.enqueue(new Callback<GetSKP>() {
            @Override
            public void onResponse(Call<GetSKP> call, Response<GetSKP>
                    response) {

                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()){

                    String status = response.body().getStatus();
                    String message= response.body().getMessage();

                    if(status.equals("berhasil")){
                        List<DataSKPTahunPegawai> SKPList = response.body().getListSKP();
                        Collections.reverse(SKPList);
//                        Toast.makeText(PengajuanCuti.this, "Jumlah data Cuti : " +
//                                String.valueOf(CutiList.size()), Toast.LENGTH_SHORT).show();

                        adapterRVDataSKPPegawai = new AdapterRVDataSKPPegawai(SKPList,PengisianSkp.this);
//                        adapterRVDataCuti.notifyDataSetChanged();
//                        adapterRVDataCuti.setHasStableIds(true);
                        rv.setAdapter(adapterRVDataSKPPegawai);
                        rv.setVisibility(View.VISIBLE);
                        btnAdd.setVisibility(View.VISIBLE);

                        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(PengisianSkp.this,R.anim.layout_animation);
                        rv.setLayoutAnimation(layoutAnimationController);
                        rv.getAdapter().notifyDataSetChanged();
                        rv.scheduleLayoutAnimation();
                    }
                    else{
                        linearBelumAdaData.setVisibility(View.VISIBLE);
                        textBelumAdaData.setText("Belum ada data Pengisian SKP, Tap tombol + untuk pengisian SKP baru");
                        btnAdd.setVisibility(View.VISIBLE);
                    }

                    getTahun();
                }
                else{
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada DB server");
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<GetSKP> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText("ERR : "+t.getMessage());
                progressBar.setVisibility(View.GONE);
                rv.setVisibility(View.GONE);
            }
        });
    }

    private void initialDialogPilihPenilaian(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_pilih_tahun_penilaian,null);

        spinner =  view.findViewById(R.id.spinner_tahun);
        TextView textKirim = view.findViewById(R.id.textKirim);
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog = builder.create();

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                DataSKPTahun dataSKPTahun = (DataSKPTahun) parent.getItemAtPosition(position);
                tahun_id = dataSKPTahun.getId();
//                Toast.makeText(Absensi.this, "Lat : "+dataKantor.getLat(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        textKirim.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tahun_id!=0){
                    tambahTahunPenilaian();
                }
            }
        });

    }
    private void getTahun(){
        Call<GetSKPTahun> tahunCall= mApiInterface.getTahun("getTahun");
        tahunCall.enqueue(new Callback<GetSKPTahun>() {
            @Override
            public void onResponse(Call<GetSKPTahun> call, Response<GetSKPTahun> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().equals("berhasil")) {
                        listTahun = response.body().getListSKP();
                        ArrayAdapter<DataSKPTahun> dataAdapter = new SpinnerTahunAdapter(PengisianSkp.this, android.R.layout.simple_spinner_item, listTahun);
                        spinner.setAdapter(dataAdapter);
                    }
                    else{

                        Toast.makeText(PengisianSkp.this, "Tidak dapat mengambil tahun SKP", Toast.LENGTH_SHORT).show();
                    }

                }
                else{
                    Toast.makeText(PengisianSkp.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetSKPTahun> call, Throwable t) {
                Toast.makeText(PengisianSkp.this, "ERR : "+ t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void tambahTahunPenilaian(){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetSKPTahun> tambahCall = mApiInterface.tambahTahun("tambahTahun",username,tahun_id);
        tambahCall.enqueue(new Callback<GetSKPTahun>() {
            @Override
            public void onResponse(Call<GetSKPTahun> call, Response<GetSKPTahun> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().equals("berhasil")){
                        Toast.makeText(PengisianSkp.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        alertDialog.dismiss();
                        refreshData();
                        
                    }else{
                        Toast.makeText(PengisianSkp.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(PengisianSkp.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetSKPTahun> call, Throwable t) {
                Toast.makeText(PengisianSkp.this, "ERR : "+ t.getMessage(), Toast.LENGTH_SHORT).show();
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
        if(pindahHalaman==true){
            refreshData();
            pindahHalaman=false;
        }
    }
}