package com.rmf.kuhcjr;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
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
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.PostPutKegiatan;
import com.rmf.kuhcjr.Utils.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditKegiatan extends AppCompatActivity {

    private EditText editTanggal,editKegiatan,editHasil,editJumlah,editKeterangan;
    private Spinner spinnerSatuan;
    private FloatingActionButton btnSimpan;

    private ImageView back;
    private String tgl,kegiatan,hasil,jumlah,satuan,keterangan;
    private int id;

    ApiInterface mApiInterface;

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
        setContentView(R.layout.activity_edit_kegiatan);
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


        id = getIntent().getIntExtra("id",0);
        tgl = getIntent().getStringExtra("tgl");
        kegiatan = getIntent().getStringExtra("kegiatan");
        hasil = getIntent().getStringExtra("hasil");
        jumlah = getIntent().getStringExtra("jumlah");
        satuan = getIntent().getStringExtra("satuan");
        keterangan = getIntent().getStringExtra("keterangan");

        initialUI();
        initialDialogPengajuan();
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



        editTanggal.setText(formatTgl(tgl));
        editKegiatan.setText(kegiatan);
        editHasil.setText(hasil);
        editJumlah.setText(jumlah);
        editKeterangan.setText(keterangan);

        if(satuan.equals("Surat")){
            spinnerSatuan.setSelection(0,true);
        }
        else if(satuan.equals("Dokumen")){
            spinnerSatuan.setSelection(1,true);
        }
        else if(satuan.equals("Berkas")){
            spinnerSatuan.setSelection(2,true);
        }
        else if(satuan.equals("Lembar")){
            spinnerSatuan.setSelection(3,true);
        }
        else if(satuan.equals("Kegiatan")){
            spinnerSatuan.setSelection(4,true);
        }

    }

    private void actionUI(){
        editTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditKegiatan.this,
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
                    updateData(ubahFormatTanggalKeSQL(editTanggal.getText().toString()),
                            editKegiatan.getText().toString(),editHasil.getText().toString(),
                            Integer.parseInt(editJumlah.getText().toString()),
                            spinnerSatuan.getSelectedItem().toString(),
                            editKeterangan.getText().toString(),id);
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

    private void updateData(final String tanggal,final String kegiatan,final String hasil,final int jumlah,
                            final String satuan, final String keterangan, final int id){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<PostPutKegiatan> PKCall = mApiInterface.putKegiatan(tanggal,username,kegiatan,hasil,jumlah,satuan,keterangan,id, DateUtils.getDateAndTime());
        PKCall.enqueue(new Callback<PostPutKegiatan>() {
            @Override
            public void onResponse(Call<PostPutKegiatan> call, Response<PostPutKegiatan>
                    response) {

                if(response.isSuccessful()){

                    String status = response.body().getStatus();
                    String message = response.body().getMessage();
                    if(status.equals("berhasil")){
                        Toast.makeText(getApplicationContext(), "Data kegiatan berhasil diubah", Toast.LENGTH_SHORT).show();
//                        success();
                        finish();

                    }
                    else{

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(EditKegiatan.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostPutKegiatan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :" + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private String formatTgl(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");

        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("d/M/yyyy");
        String akhir = output.format(date);

        return akhir;

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


        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(berhasil){
                    finish();
                }
            }
        });
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
        text.setText("Berhasil diperbarui");
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
