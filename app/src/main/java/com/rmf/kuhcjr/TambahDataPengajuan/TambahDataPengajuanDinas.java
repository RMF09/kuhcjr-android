package com.rmf.kuhcjr.TambahDataPengajuan;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.PostPutLembur;
import com.rmf.kuhcjr.Data.PostPutPerjalananDinas;
import com.rmf.kuhcjr.FileUtils;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.SharedPrefs;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TambahDataPengajuanDinas extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 1;
    private Uri fileUri;
    private String filePath;

    private ImageView back;
    private EditText editTanggal,editMulai,editSelesai,editTujuan,editUraian,editFile;
    private Button btnCari;
    private FloatingActionButton btnSimpan;

    private ApiInterface apiInterface;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_data_pengajuan_dinas);
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
        username = SharedPrefs.getInstance(this).LoggedInUser();

        initialUI();
    }

    private void initialUI(){
        back = (ImageView) findViewById(R.id.back);
        editTanggal = (EditText) findViewById(R.id.tgl_perjalanan);
        editMulai = (EditText) findViewById(R.id.tanggal_mulai);
        editSelesai = (EditText) findViewById(R.id.tanggal_selesai);
        editTujuan = (EditText)findViewById(R.id.tujuan);
        editUraian = (EditText) findViewById(R.id.uraian);
        editFile = (EditText) findViewById(R.id.file_formulir);

        btnCari = (Button) findViewById(R.id.btn_cari);
        btnSimpan= (FloatingActionButton) findViewById(R.id.btn_simpan);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        initialDialogPengajuan();
        this.actionUI();
    }
    private void actionUI(){

        editTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TambahDataPengajuanDinas.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                editTanggal.setText(day +"/"+(month+1)+"/"+year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        editMulai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TambahDataPengajuanDinas.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                editMulai.setText(day +"/"+(month+1)+"/"+year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        editSelesai.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(TambahDataPengajuanDinas.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                editSelesai.setText(day +"/"+(month+1)+"/"+year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });

        editFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
            }
        });
        btnCari.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(checkInput(v)){
                    show("Mengajukan data perjalanan dinas...");
                    insertData(fileUri,
                            ubahFormatTanggalKeSQL(editTanggal.getText().toString()),
                            ubahFormatTanggalKeSQL(editMulai.getText().toString()),
                            ubahFormatTanggalKeSQL(editSelesai.getText().toString()),
                            editUraian.getText().toString(),
                            editTujuan.getText().toString());
                }
            }
        });
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(TambahDataPengajuanDinas.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(TambahDataPengajuanDinas.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
            pickFile();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                pickFile();
            }
            else {
                Toast.makeText(TambahDataPengajuanDinas.this,
                        "Gallery Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();

            }
        }
    }
    private void pickFile(){
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/pdf");
        String[] mimetypes = {"application/pdf"};
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        chooseFile = Intent.createChooser(chooseFile,"Pilih File");

        startActivityForResult(chooseFile, STORAGE_PERMISSION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case STORAGE_PERMISSION_CODE:
                if(resultCode==-1){
                    fileUri = data.getData();
                    filePath = fileUri.getLastPathSegment();
                    editFile.setHint(FileUtils.getPath(this,fileUri));
                }
        }
    }
    private boolean checkInput(View v){
        boolean cek = false;
        if(TextUtils.isEmpty(editTanggal.getText().toString())){
            Snackbar.make(v,"Harap isi tanggal lembur",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editMulai.getText().toString())){
            Snackbar.make(v,"Harap isi jam mulai",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editSelesai.getText().toString())){
            Snackbar.make(v,"Harap isi jam selesai",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editUraian.getText().toString())){
            Snackbar.make(v,"Harap isi uraian",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editTujuan.getText().toString())){
            Snackbar.make(v,"Harap isi tujuan",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(filePath)){
            Snackbar.make(v,"Harap pilih file formulir",Snackbar.LENGTH_SHORT).show();
        }
        else{
            cek = true;
        }
        return cek;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void insertData(Uri fileUri, String tanggal, String mulai, String selesai, String uraian, String tujuan){

        try {

            File file = new File(FileUtils.getPath(TambahDataPengajuanDinas.this,fileUri));

            //RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),file);
            RequestBody tanggalBody = RequestBody.create(MediaType.parse("text/plain"), tanggal);
            RequestBody mulaiBody = RequestBody.create(MediaType.parse("text/plain"), mulai);
            RequestBody selesaiBody = RequestBody.create(MediaType.parse("text/plain"), selesai);
            RequestBody uraianBody = RequestBody.create(MediaType.parse("text/plain"), uraian);
            RequestBody tujuanBody = RequestBody.create(MediaType.parse("text/plain"), tujuan);
            RequestBody metodeBody = RequestBody.create(MediaType.parse("text/plain"), "insert");
            RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);

            Call<PostPutPerjalananDinas> PDCall = apiInterface.postPerjalananDinas(requestFile,tanggalBody,mulaiBody,selesaiBody,tujuanBody,uraianBody,metodeBody,usernameBody);
            PDCall.enqueue(new Callback<PostPutPerjalananDinas>() {
                @Override
                public void onResponse(Call<PostPutPerjalananDinas> call, Response<PostPutPerjalananDinas>
                        response) {
                    if(response.isSuccessful()){
                        String status = response.body().getStatus();
                        String message = response.body().getMessage();
                        if(status.equals("gagal")){
                            failure(message);
                        }else{
                            success();
                        }

                    }else{
                        failure("Terjadi masalah pada Server");
                    }
                }

                @Override
                public void onFailure(Call<PostPutPerjalananDinas> call, Throwable t) {
                    Log.e("Retrofit Get", t.toString());
//                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    failure(t.getMessage());
                }
            });
        }catch (NullPointerException e){
            filePath ="";
            editFile.setHint("File Formulir");
            failure("Tidak dapat mengupload file, coba pilih file yang lain");
        }


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

    //Progress Dialog
    AlertDialog alertDialog;
    ProgressBar progressBar;
    ImageView checklist;
    ImageView error;
    TextView text;

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
        text.setText("Berhasil diajukan");
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
