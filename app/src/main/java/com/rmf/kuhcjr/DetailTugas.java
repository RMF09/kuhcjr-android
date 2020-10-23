package com.rmf.kuhcjr;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Adapter.AdapterRVDataTugas;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataTugas;
import com.rmf.kuhcjr.Data.GetTugas;
import com.rmf.kuhcjr.Data.PostPutLembur;
import com.rmf.kuhcjr.Fragments.TugasPokok;
import com.rmf.kuhcjr.Pengajuan.DetailPengajuan;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailTugas extends AppCompatActivity {

    private TextView textKegiatan,textKuant,textKual,textWaktu,textBiaya;
    private ImageView imageEdit,imageHapus;

    private String kegiatan,output,satuanWaktu;
    private int kuant,kual,waktu,biaya,id;

    //Dialog hapus
    private AlertDialog alertDialogHapus;
    private boolean dihapus=false;
    private ImageView imagehapus;
    private TextView btnHapus,btnBatalHapus,textDialog;
    private ApiInterface apiInterface;
    private String dariActivity;
    private int tahun_id;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tugas);

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
        dariActivity = getIntent().getStringExtra("menu");
        id = getIntent().getIntExtra("id",0);
        tahun_id = getIntent().getIntExtra("tahun_id",0);
        kegiatan = getIntent().getStringExtra("kegiatan");
        kuant = getIntent().getIntExtra("kuant",0);
        output = getIntent().getStringExtra("output");
        kual = getIntent().getIntExtra("kual",0);
        waktu = getIntent().getIntExtra("waktu",0);
        satuanWaktu = getIntent().getStringExtra("satuan_waktu");
        biaya = getIntent().getIntExtra("biaya",0);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        this.initialUI();
        initialDialogHapus();
    }

    private void initialUI() {
        textKegiatan = findViewById(R.id.kegiatan_tugas_jabatan);
        textKuant = findViewById(R.id.kuant);
        textKual = findViewById(R.id.kual);
        textWaktu = findViewById(R.id.waktu);
        textBiaya = findViewById(R.id.biaya);

        imageEdit = findViewById(R.id.edit);
        imageHapus = findViewById(R.id.delete);

        textKegiatan.setText(kegiatan);
        textKuant.setText(String.valueOf(kuant) + " "+ output);
        textKual.setText(String.valueOf(kual)+" %");
        textWaktu.setText(String.valueOf(waktu)+" "+ satuanWaktu);
        textBiaya.setText(String.valueOf(biaya));
        this.actionUI();
    }

    private void actionUI() {
        imageHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog.setText("Hapus data kegiatan ini?");
                alertDialogHapus.show();
            }
        });
        imageEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailTugas.this, EditTugas.class);
                intent.putExtra("menu",dariActivity);
                intent.putExtra("id",id);
                intent.putExtra("tahun_id",tahun_id);
                intent.putExtra("kegiatan",kegiatan);
                intent.putExtra("kuant",kuant);
                intent.putExtra("output",output);
                intent.putExtra("kual",kual);
                intent.putExtra("waktu",waktu);
                intent.putExtra("satuan_waktu",satuanWaktu);
                intent.putExtra("biaya",biaya);
                startActivity(intent);
            }
        });
    }
    private void initialDialogHapus(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_hapus,null);

        imagehapus = (ImageView) view.findViewById(R.id.image_peringatan);
        btnHapus = (TextView)  view.findViewById(R.id.hapus);
        btnBatalHapus = (TextView)  view.findViewById(R.id.batal);
        textDialog = (TextView) view.findViewById(R.id.text_dialog);

        builder.setView(view);
        alertDialogHapus = builder.create();
        alertDialogHapus.setCancelable(false);


        btnBatalHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogHapus.dismiss();
            }
        });
        btnHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dihapus){
                    finish();
                }
                else{
                    if(dariActivity.equals("tugas_pokok")){
                        hapusData("hapusTugasPokok");
                    }
                    else{
                        hapusData("hapusTugasTambahan");
                    }
                }
            }
        });
    }
    private void hapusData(String metode){
        textDialog.setText("Harap tunggu...");

        Call<GetTugas> TugasCall = apiInterface.hapusTugas(metode,id);
        TugasCall.enqueue(new Callback<GetTugas>() {
            @Override
            public void onResponse(Call<GetTugas> call, Response<GetTugas>
                    response) {

                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    String message= response.body().getMessage();
                    if(status.equals("berhasil")){
                        dihapus=true;
                        btnBatalHapus.setVisibility(View.GONE);
                        btnHapus.setText("OK");
                        imagehapus.setImageResource(R.drawable.ic_check_black_24dp);

                    }else{
                        imagehapus.setImageResource(R.drawable.ic_error_black_24dp);
                    }
                    textDialog.setText(message);

                }else{
                    imagehapus.setImageResource(R.drawable.ic_error_black_24dp);
                    textDialog.setText("Terjadi masalah pada Server");
                }


            }

            @Override
            public void onFailure(Call<GetTugas> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                imagehapus.setImageResource(R.drawable.ic_error_black_24dp);
                textDialog.setText(t.getMessage());
//                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadData(String metode){


        Call<GetTugas> TugasPokokCall = apiInterface.getDetailTugas(metode,id);
        TugasPokokCall.enqueue(new Callback<GetTugas>() {
            @Override
            public void onResponse(Call<GetTugas> call, Response<GetTugas>
                    response) {

                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        List<DataTugas> list = response.body().getListDataTugas();
                        textKegiatan.setText(list.get(0).getKegiatan());
                        textKuant.setText(String.valueOf(list.get(0).getTarget_kuant())+" "+list.get(0).getTarget_output());
                        textKual.setText(String.valueOf(list.get(0).getTarget_kual()));
                        textWaktu.setText(String.valueOf(list.get(0).getTarget_waktu()+" "+list.get(0).getTarget_satuan_waktu()));
                        textBiaya.setText(String.valueOf(list.get(0).getTarget_biaya()));



                    }else{
                        Toast.makeText(getApplicationContext(), "Data mungkin tidak ada", Toast.LENGTH_SHORT).show();

                    }

                }else{
                    //not successful
                    Toast.makeText(DetailTugas.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetTugas> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(dariActivity.equals("tugas_pokok")){
            loadData("detailTugasPokok");
        }
        else{
            loadData("detailTugasTambahan");
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
}