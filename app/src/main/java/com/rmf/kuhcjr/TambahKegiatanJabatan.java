package com.rmf.kuhcjr;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.GetTugas;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahKegiatanJabatan extends AppCompatActivity {
    private TextView textHeader;
    private EditText editKegiatan,editKuant,editOutput,editKual,editWaktu,editSatuanWaktu,editBiaya;
    private FloatingActionButton fab;
    private String dariActivity;
    private int tahun_id;

    private ApiInterface apiInterface;

    //Dialog Progress
    AlertDialog alertDialog;
    ProgressBar progressBar;
    ImageView checklist;
    ImageView error;
    TextView text;
    boolean berhasil=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kegiatan_jabatan);
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
        tahun_id = getIntent().getIntExtra("tahun_id",0);
        dariActivity = getIntent().getStringExtra("menu");
        this.initialUI();
        initialDialogPengajuan();

    }

    private void initialUI(){

        textHeader = findViewById(R.id.text_header);

        editKegiatan = findViewById(R.id.kegiatan_tugas_jabatan);
        editKuant = findViewById(R.id.kuant);
        editOutput = findViewById(R.id.output);
        editKual = findViewById(R.id.kual);
        editWaktu = findViewById(R.id.waktu);
        editSatuanWaktu = findViewById(R.id.satuan_waktu);
        editBiaya = findViewById(R.id.biaya);

        fab = findViewById(R.id.btn_simpan);

        if(dariActivity.equals("tugas_tambahan")){
            textHeader.setText("Tambah Tugas Tambahan");
        }else{
            textHeader.setText("Tambah Tugas Pokok");
        }

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        this.actionUI();
    }

    private void actionUI() {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Toast.makeText(TambahKegiatanJabatan.this, "sungguh aneh tapi nyaan", Toast.LENGTH_SHORT).show();
                if(checkForm(v)){
                    if(dariActivity.equals("tugas_tambahan")){
                        show("Menambah tugas tambahan...");
                        tambahTugas("tambahTugasTambahan",
                                editKegiatan.getText().toString(),
                                Integer.parseInt(editKuant.getText().toString()),
                                editOutput.getText().toString(),
                                Integer.parseInt(editKual.getText().toString()),
                                Integer.parseInt(editWaktu.getText().toString()),
                                editSatuanWaktu.getText().toString(),
                                Integer.parseInt(editBiaya.getText().toString())
                        );
                    }
                    else{
                        show("Menambah tugas pokok...");
                        tambahTugas("tambahTugasPokok",
                                editKegiatan.getText().toString(),
                                Integer.parseInt(editKuant.getText().toString()),
                                editOutput.getText().toString(),
                                Integer.parseInt(editKual.getText().toString()),
                                Integer.parseInt(editWaktu.getText().toString()),
                                editSatuanWaktu.getText().toString(),
                                Integer.parseInt(editBiaya.getText().toString())
                        );
                    }
                }
            }
        });
    }
    public boolean checkForm(View v){
        boolean cek =false;
        if(TextUtils.isEmpty(editKegiatan.getText().toString())){
            Snackbar.make(v,"Harap isi kegiatan",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editKuant.getText().toString())){
            Snackbar.make(v,"Harap isi kuantitas",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editOutput.getText().toString())){
            Snackbar.make(v,"Harap isi output",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editKual.getText().toString())){
            Snackbar.make(v,"Harap isi kualitas",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editWaktu.getText().toString())){
            Snackbar.make(v,"Harap isi waktu",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editSatuanWaktu.getText().toString())){
            Snackbar.make(v,"Harap isi satuan waktu",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editBiaya.getText().toString())){
            Snackbar.make(v,"Harap isi biaya",Snackbar.LENGTH_SHORT).show();
        }
        else{
            cek=true;
        }
        return cek;
    }

    private void tambahTugas(String metode,String kegiatan, int kuant, String output, int kual, int waktu,String satuan_waktu,int biaya){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetTugas> tugasCall = apiInterface.tambahTugas(metode,username,tahun_id,kegiatan,kuant,output,kual,waktu,satuan_waktu,biaya);

        tugasCall.enqueue(new Callback<GetTugas>() {
            @Override
            public void onResponse(Call<GetTugas> call, Response<GetTugas> response) {
                if(response.isSuccessful()){

                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        success();
//                        Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    }else{
//                        Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                        failure("Gagal menambah kegiatan");
                    }
                }else{
                    failure("Terjadi masalah pada Server");
                }
            }

            @Override
            public void onFailure(Call<GetTugas> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initialDialogPengajuan(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.progress_dialog,null);

        //initial ui
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        checklist = (ImageView) view.findViewById(R.id.image_progress_status_checklist);
        error = (ImageView) view.findViewById(R.id.image_progress_status_error);
        text = (TextView) view.findViewById(R.id.text_loading);

        builder.setView(view);
        alertDialog= builder.create();



    }
    public void show(String teks){
        text.setText(teks);

        checklist.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    public void dismiss(){
        alertDialog.dismiss();
    }

    public void success(){
        progressBar.setVisibility(View.GONE);
        checklist.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        text.setText("Berhasil ditambahkan");
        alertDialog.setCancelable(true);
    }
    public void failure(String teksError){
        progressBar.setVisibility(View.GONE);
        checklist.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        text.setText(teksError);
        alertDialog.setCancelable(true);
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