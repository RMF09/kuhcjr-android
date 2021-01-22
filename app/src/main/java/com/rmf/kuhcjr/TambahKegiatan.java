package com.rmf.kuhcjr;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.PostPutKegiatan;
import com.rmf.kuhcjr.Data.PostPutPeminjamanKendaraan;
import com.rmf.kuhcjr.TambahDataPengajuan.TambahDataPengajuanLembur;
import com.rmf.kuhcjr.Utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahKegiatan extends AppCompatActivity {

    private EditText editTanggal,editKegiatan,editHasil,editJumlah,editKeterangan;
    private Spinner spinnerSatuan;
    private FloatingActionButton btnSimpan;

    private ImageView back;

    ApiInterface mApiInterface;

    //Dialog Progress
    AlertDialog alertDialog;
    ProgressBar progressBar;
    ImageView checklist;
    ImageView error;
    TextView text;
    boolean berhasil=false;
    final Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_kegiatan);
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
        initialDialogPengajuan();
        initialUI();
    }

    private void initialUI(){

        back = (ImageView) findViewById(R.id.back);

        editTanggal = (EditText) findViewById(R.id.tgl);
        editKegiatan= (EditText) findViewById(R.id.kegiatan);
        editHasil = (EditText) findViewById(R.id.hasil);
        editJumlah = (EditText) findViewById(R.id.jumlah);
        editJumlah.setHint("0");
        editKeterangan = (EditText) findViewById(R.id.keterangan);

        spinnerSatuan = (Spinner) findViewById(R.id.spinner_satuan);

        btnSimpan = (FloatingActionButton) findViewById(R.id.btn_simpan);

        this.actionUI();

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    private void actionUI(){
        editTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TambahKegiatan.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                editTanggal.setText(day +"/"+(month+1)+"/"+year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if(checkInput(v)){
                    show("Menambah Kegiatan...");
                    insertData(ubahFormatTanggalKeSQL(editTanggal.getText().toString()),
                            editKegiatan.getText().toString(),editHasil.getText().toString(),
                            Integer.parseInt(editJumlah.getText().toString()),
                            spinnerSatuan.getSelectedItem().toString(),
                            editKeterangan.getText().toString());
                }
            }
        });

    }

    private boolean checkInput(View v){
        boolean cek = false;
        if(TextUtils.isEmpty(editTanggal.getText().toString())){
            Snackbar.make(v,"Harap isi tanggal",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editKegiatan.getText().toString())){
            Snackbar.make(v,"Harap isi kegiatan",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editHasil.getText().toString())){
            Snackbar.make(v,"Harap isi hasil",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editJumlah.getText().toString())){
            Snackbar.make(v,"Harap isi jumlah",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editKeterangan.getText().toString())){
            Snackbar.make(v,"Harap isi keterangan",Snackbar.LENGTH_SHORT).show();
        }
        else{
            cek = true;
        }
        return cek;
    }

    private void insertData(String tanggal,String kegiatan,String hasil,int jumlah, String satuan,String keterangan){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<PostPutKegiatan> PKCall = mApiInterface.postKegiatan(tanggal,username,kegiatan,hasil,jumlah,satuan,keterangan, DateUtils.getDateAndTime());
        PKCall.enqueue(new Callback<PostPutKegiatan>() {
            @Override
            public void onResponse(Call<PostPutKegiatan> call, Response<PostPutKegiatan>
                    response) {

                if(response.isSuccessful()){

                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        success();
//                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }else{
                        failure("Gagal menambah kegiatan");
//                        Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
                    }

                    
                }else{
                    failure("Terjadi masalah pada Server");
                }
                timer();

            }

            @Override
            public void onFailure(Call<PostPutKegiatan> call, Throwable t) {
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
//        alertDialog.setCancelable(true);
    }
    public void failure(String teksError){
        progressBar.setVisibility(View.GONE);
        checklist.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        text.setText(teksError);
//        alertDialog.setCancelable(true);
    }

    private String ubahFormatTanggalKeSQL(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        String akhir = output.format(date);

        return akhir;

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
    private void timer(){

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                alertDialog.dismiss();
                finish();
            }
        },2000);
    }
}
