package com.rmf.kuhcjr;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.rmf.kuhcjr.Adapter.AdapterRVPeminjamanKendaraan;
import com.rmf.kuhcjr.Adapter.AdapterRVPengembalianKendaraan;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataPeminjamanKendaraan;
import com.rmf.kuhcjr.Data.GetPeminjamanKendaraan;
import com.rmf.kuhcjr.Data.PostPutPeminjamanKendaraan;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PengembalianMobil extends AppCompatActivity {

    private SwipeRefreshLayout swipeRefreshLayout;
    private FloatingActionButton btnPindai;
    private ProgressBar progressBar;

    private IntentIntegrator qrScan;

    ApiInterface mApiInterface;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter2;
    private RecyclerView.LayoutManager mLayoutManager;

    AlertDialog alertDialogWarning;

    //Masalah Jaringan Layout
    private LinearLayout linearMasalahJaringan;
    private TextView textERR,textMuatUlang;

    //Belum Ada data
    private LinearLayout linearBelumAdaData;
    private TextView textBelumAdaData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengembalian_mobil);
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
        initialDialogWarning();

        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false);
        qrScan.setPrompt("");
    }

    private void initialUI(){

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);

        btnPindai = (FloatingActionButton) findViewById(R.id.btn_pindai);
        progressBar = (ProgressBar) findViewById(R.id.progress);

        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mLayoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemViewCacheSize(20);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

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
        refreshData();
    }
    private void actionUI(){
        btnPindai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               checkDataTerakhir();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());

                    String idKendaraan = obj.getString("id");
//                    String namaKendaraan= obj.getString("namaKendaraan");

                    Toast.makeText(this, "ID : "+idKendaraan, Toast.LENGTH_SHORT).show();
                    kembalikanMobil(idKendaraan);
                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
    @SuppressLint("RestrictedApi")
    public void refreshData() {
        btnPindai.setVisibility(View.GONE);

        linearMasalahJaringan.setVisibility(View.GONE);
        linearBelumAdaData.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        String username  = SharedPrefs.getInstance(this).LoggedInUser();
        Call<PostPutPeminjamanKendaraan> PKCall = mApiInterface.getPeminjamanKendaraan("get",username);

        PKCall.enqueue(new Callback<PostPutPeminjamanKendaraan>() {
            @Override
            public void onResponse(Call<PostPutPeminjamanKendaraan> call, Response<PostPutPeminjamanKendaraan>
                    response) {

                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    String status = response.body().getStatus();

                    if(status.equals("berhasil")){
                        List<DataPeminjamanKendaraan> PKList = response.body().getDataPeminjamanKendaraan();
                        Collections.reverse(PKList);
                        mAdapter2 = new AdapterRVPengembalianKendaraan(PKList);
//                        mAdapter2.notifyDataSetChanged();
//                        mAdapter2.setHasStableIds(true);
                        mRecyclerView.setAdapter(mAdapter2);

                        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(PengembalianMobil.this,R.anim.layout_animation);
                        mRecyclerView.setLayoutAnimation(layoutAnimationController);
                        mRecyclerView.getAdapter().notifyDataSetChanged();
                        mRecyclerView.scheduleLayoutAnimation();

                        mRecyclerView.setVisibility(View.VISIBLE);
                        btnPindai.setVisibility(View.VISIBLE);
                    }
                    else{
                        mRecyclerView.setVisibility(View.GONE);
                        linearBelumAdaData.setVisibility(View.VISIBLE);
                        textBelumAdaData.setText("Belum ada data peminjaman kendaraan, silahkan ke menu peminjaman mobil ");
                        btnPindai.setVisibility(View.GONE);
                    }
                }
                else{
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada DB server");
                    progressBar.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<PostPutPeminjamanKendaraan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText("ERR : "+t.getMessage());
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
            }
        });
    }
    private void kembalikanMobil(String idKendaraan){
        Call<PostPutPeminjamanKendaraan> PKCall = mApiInterface.postPeminjamanKendaraan("1","pegawai",idKendaraan,"");
        PKCall.enqueue(new Callback<PostPutPeminjamanKendaraan>() {
            @Override
            public void onResponse(Call<PostPutPeminjamanKendaraan> call, Response<PostPutPeminjamanKendaraan>
                    response) {
                if(response.isSuccessful()){

                    String status = response.body().getStatus();
                    if(status.equals("mobil berhasil dikembalikan")){

                        Toast.makeText(PengembalianMobil.this, status, Toast.LENGTH_SHORT).show();
                        refreshData();
                    }else{
                        Toast.makeText(PengembalianMobil.this, "mobil gagal dikembalikan", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(PengembalianMobil.this, "Terjadi masala pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostPutPeminjamanKendaraan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(PengembalianMobil.this, "ERR :" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkDataTerakhir(){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<PostPutPeminjamanKendaraan> PKCall = mApiInterface.postPeminjamanKendaraan("check",username,"","");
        PKCall.enqueue(new Callback<PostPutPeminjamanKendaraan>() {
            @Override
            public void onResponse(Call<PostPutPeminjamanKendaraan> call, Response<PostPutPeminjamanKendaraan>
                    response) {

                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){

                    String status= response.body().getStatus();
                    if(status.equals("Belum ada")){
                        Toast.makeText(PengembalianMobil.this, "Silahkan pinjam kendaraan terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                    else{

                        List<DataPeminjamanKendaraan> dataPK = response.body().getDataPeminjamanKendaraan();

                        String date_updated= dataPK.get(0).getDate_updated();

                        if(date_updated!=null){
                            alertDialogWarning.show();
                        }
                        else{
                            //Pengembalian Mobil
                            qrScan.initiateScan();
                            //open qr camera
                        }
                    }
                }else{
                    Toast.makeText(PengembalianMobil.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onFailure(Call<PostPutPeminjamanKendaraan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText("ERR : "+t.getMessage());
                progressBar.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.GONE);
            }
        });
    }
    private void initialDialogWarning() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.dialog_peringatan, null);

        ImageView image = (ImageView) view.findViewById(R.id.image_peringatan);

        TextView textDialog = (TextView) view.findViewById(R.id.text_dialog);
        TextView textOK = (TextView) view.findViewById(R.id.ok);

        builder.setView(view);
        builder.setCancelable(true);
        alertDialogWarning = builder.create();

        String teks = "Sistem mendeteksi jika tidak ada kendaraan yang sedang anda pinjam.";
        textDialog.setText(teks);

        textOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogWarning.dismiss();

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

}
