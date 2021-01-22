package com.rmf.kuhcjr;

import android.Manifest;
import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Adapter.AdapterRVDataFileKegiatan;
import com.rmf.kuhcjr.Adapter.AdapterRVDataKegiatan;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataFileKegiatan;
import com.rmf.kuhcjr.Data.DataKegiatan;
import com.rmf.kuhcjr.Data.GetKegiatan;
import com.rmf.kuhcjr.Data.GetPostPutFileKegiatan;
import com.rmf.kuhcjr.Data.PostPutCuti;
import com.rmf.kuhcjr.Data.PostPutKegiatan;
import com.rmf.kuhcjr.TambahDataPengajuan.TambahDataPengajuanCuti;
import com.rmf.kuhcjr.Utils.DateUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailKegiatan extends AppCompatActivity {


    private static final int STORAGE_PERMISSION_CODE = 1;
    private static final String TAG="DetailKegiatan";
    private Uri fileUri;
    private String filePath="";
    //UI
    private TextView textTgl,textKegiatan,textHasil,textJumlah,textSatuan,textKeterangan;
    private ImageView fileUpload,edit,delete;
    private AlertDialog alertDialogHapus,alertDialogUpload;
    private String tgl,kegiatan,hasil,jumlah,satuan,keterangan;
    private int id;
    private boolean dihapus=false;

    //Dialog Hapus UI
    ImageView imagehapus;
    TextView textDialog;
    TextView btnHapus;
    TextView btnBatalHapus;
    ApiInterface mApiInterface;

    //File Kegiatan
    CardView cardFileKegiatan;
    AdapterRVDataFileKegiatan adapterRVDataFileKegiatan;
    RecyclerView rv;

    //Dialog Upload
    EditText editUpload;
    LinearLayout linearCariFile,linearProgressUpload,linearBtnUpload;
    TextView textHeaderUpload,textStatusUpload,textBatalUpload,textUpload;
    ProgressBar progressBarUpload;
    ImageView imageStatusUpload;
    boolean diupload=false;

    //Dialog Download

    ProgressBar progressBar;
    TextView textProgress,textHeaderDownload;
    TextView textOKDownload,textKeteranganDownload;
    AlertDialog alertDialogDownload;

    public String namafile;

    DownloadZipFileTask downloadZipFileTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_kegiatan);

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

        initialDialogHapus();
        initialDialogUpload();
        initialDialogDownload();
        initialUI();
        loadDataFileKegiatan();

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101);

    }

    private void initialUI(){
        textTgl = (TextView) findViewById(R.id.tgl);
        textKegiatan= (TextView) findViewById(R.id.kegiatan);
        textHasil = (TextView) findViewById(R.id.hasil);
        textJumlah= (TextView) findViewById(R.id.jumlah);
        textSatuan= (TextView) findViewById(R.id.satuan);
        textKeterangan = (TextView) findViewById(R.id.keterangan);

        fileUpload = (ImageView) findViewById(R.id.file_upload);

        edit = (ImageView) findViewById(R.id.edit);
        delete = (ImageView) findViewById(R.id.delete);

        cardFileKegiatan = (CardView) findViewById(R.id.card_file_kegiatan);
        rv = (RecyclerView) findViewById(R.id.rv);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(lm);


        textTgl.setText(tgl);
        textKegiatan.setText(kegiatan);
        textHasil.setText(hasil);
        textJumlah.setText(jumlah);
        textSatuan.setText(satuan);
        textKeterangan.setText(keterangan);

        this.actionUI();
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

    }
    private void actionUI(){
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textDialog.setText("Hapus data kegiatan '"+kegiatan+"'?");
                alertDialogHapus.show();
            }
        });
        fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialogUpload.show();
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditKegiatan.class);
                intent.putExtra("id",id) ;
                intent.putExtra("tgl",tgl);
                intent.putExtra("kegiatan",kegiatan);
                intent.putExtra("hasil",hasil);
                intent.putExtra("jumlah",jumlah);
                intent.putExtra("satuan",satuan);
                intent.putExtra("keterangan",keterangan);

                startActivity(intent);
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
                }else {
                    hapusData();
                }
            }
        });
    }

    private void initialDialogUpload(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_upload,null);

        textUpload = (TextView)  view.findViewById(R.id.upload);
        textBatalUpload = (TextView)  view.findViewById(R.id.batal_upload);
        editUpload = (EditText) view.findViewById(R.id.edit_nama_file);

        linearCariFile = (LinearLayout) view.findViewById(R.id.linear_cari_file);
        linearProgressUpload= (LinearLayout) view.findViewById(R.id.linear_upload_progress);
        linearBtnUpload = (LinearLayout) view.findViewById(R.id.linear_btn_upload);

        textHeaderUpload  = (TextView) view.findViewById(R.id.text_header_upload);
        textStatusUpload = (TextView) view.findViewById(R.id.text_status_upload);
        progressBarUpload = (ProgressBar) view.findViewById(R.id.progress_upload);
        imageStatusUpload = (ImageView) view.findViewById(R.id.image_status_upload);

        builder.setView(view);
        alertDialogUpload = builder.create();
        alertDialogUpload.setCancelable(false);

        textBatalUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogUpload.dismiss();
                loadDataFileKegiatan();
            }
        });
        textUpload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {

                if(!diupload){

                    if(filePath.equals("")){
                        Snackbar.make(v,"Harap pilih file terlebih dahulu",Snackbar.LENGTH_SHORT).show();
                    }
                    else {

                        insertDataFileKegiatan(fileUri,String.valueOf(id));
                    }
                }
                else{
                    textHeaderUpload.setText("Upload file kegiatan");
                    imageStatusUpload.setVisibility(View.GONE);
                    progressBarUpload.setVisibility(View.VISIBLE);
                    linearProgressUpload.setVisibility(View.GONE);
                    linearCariFile.setVisibility(View.VISIBLE);
                    linearBtnUpload.setVisibility(View.VISIBLE);
                    textStatusUpload.setText("Sedang mengupload file, harap tunggu...");
                    textUpload.setText("UPLOAD");
                    diupload=false;
                }
            }
        });
        editUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
            }
        });
    }

    private void initialDialogDownload(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_download,null);

        progressBar = (ProgressBar)  view.findViewById(R.id.progress);
        textProgress = (TextView)  view.findViewById(R.id.text_progress);
        textKeteranganDownload = (TextView) view.findViewById(R.id.text_keterangan_download);
        textOKDownload = (TextView) view.findViewById(R.id.ok);
        textHeaderDownload= (TextView) view.findViewById(R.id.text_header_download);

        builder.setView(view);
        alertDialogDownload = builder.create();
        alertDialogDownload.setCancelable(false);

        textOKDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogDownload.dismiss();
                textProgress.setText("0");
                textHeaderDownload.setText("Mengunduh file, harap menunggu...");
                progressBar.setProgress(0);
                textOKDownload.setVisibility(View.GONE);
                textKeteranganDownload.setVisibility(View.GONE);

            }
        });

    }
    private void loadData(){
//        progressBar.setVisibility(View.VISIBLE);
        String username= SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetKegiatan> KegiatanCall = mApiInterface.getDetailKegiatan("detail",username,id);
        KegiatanCall.enqueue(new Callback<GetKegiatan>() {
            @Override
            public void onResponse(Call<GetKegiatan> call, Response<GetKegiatan>
                    response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
//                        progressBar.setVisibility(View.GONE);
                        List<DataKegiatan> dataKegiatan = response.body().getListDataKegiatan();
                        //textTgl.setText(dataKegiatan.get(0).getTanggal());
                        textKegiatan.setText(dataKegiatan.get(0).getKegiatan());
                        textHasil.setText(dataKegiatan.get(0).getHasil());
                        textJumlah.setText(dataKegiatan.get(0).getJumlah());
                        textSatuan.setText(dataKegiatan.get(0).getSatuan());
                        textKeterangan.setText(dataKegiatan.get(0).getKeterangan());

//                Toast.makeText(getApplicationContext(), "Check Detail :"+dataKegiatan.get(0).getKegiatan(), Toast.LENGTH_SHORT).show();

                    }else{
                        Toast.makeText(DetailKegiatan.this,"Gagal mengambil data kegiatan",Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(DetailKegiatan.this,"Terjadi masalah pada Server",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetKegiatan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setSuccessDelete(){
        btnBatalHapus.setVisibility(View.GONE);
        btnHapus.setText("OK");
        imagehapus.setImageResource(R.drawable.ic_check_black_24dp);
        textDialog.setText("Data kegiatan berhasil dihapus");
    }
    private void setFailedDelete(String message){
        imagehapus.setImageResource(R.drawable.ic_error_black_24dp);
        textDialog.setText(message);
    }
    private void hapusData(){
//        progressBar.setVisibility(View.VISIBLE);
        textDialog.setText("Harap tunggu...");
        Call<PostPutKegiatan> KegiatanCall = mApiInterface.deleteKegiatan(id);
        KegiatanCall.enqueue(new Callback<PostPutKegiatan>() {
            @Override
            public void onResponse(Call<PostPutKegiatan> call, Response<PostPutKegiatan>
                    response) {
                if(response.isSuccessful()){

                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        dihapus=true;
                        setSuccessDelete();

                    }else{
                        setFailedDelete("Gagal menghapus data kegiatan");
                    }
                }else{
                    setFailedDelete("Terjadi masalah pada Server");
                }
            }

            @Override
            public void onFailure(Call<PostPutKegiatan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                setFailedDelete(t.getMessage());
//                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void loadDataFileKegiatan(){
//        progressBar.setVisibility(View.VISIBLE);
        cardFileKegiatan.setVisibility(View.GONE);
        final Call<GetPostPutFileKegiatan> FileKegiatanCall = mApiInterface.getFileKegiatan("file",id);
        FileKegiatanCall.enqueue(new Callback<GetPostPutFileKegiatan>() {
            @Override
            public void onResponse(Call<GetPostPutFileKegiatan> call, Response<GetPostPutFileKegiatan>
                    response) {
                if(response.isSuccessful()){
                    String status=  response.body().getStatus();
                    if(status.equals("berhasil")){
                        List<DataFileKegiatan> FileKegiatanList = response.body().getListDataFileKegiatan();
                        Collections.reverse(FileKegiatanList);
                        Toast.makeText(getApplicationContext(), "Jumlah data FIle : " +
                                String.valueOf(FileKegiatanList.size()), Toast.LENGTH_SHORT).show();
                        if(FileKegiatanList.size()>0){

                            adapterRVDataFileKegiatan = new AdapterRVDataFileKegiatan(FileKegiatanList,DetailKegiatan.this,DetailKegiatan.this);


                            rv.setAdapter(adapterRVDataFileKegiatan);
                            cardFileKegiatan.setVisibility(View.VISIBLE);
                        }
                    }else{
//                        Toast.makeText(DetailKegiatan.this, "Gagal mengambil file kegiatan", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DetailKegiatan.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<GetPostPutFileKegiatan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    //Download FIle
    public void downloadZipFile(String namafile) {

        alertDialogDownload.show();
        Call<ResponseBody> call = mApiInterface.downloadFile("assets/files/kinerja/kegiatan/"+namafile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {

//                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

                    downloadZipFileTask = new DownloadZipFileTask();
                    downloadZipFileTask.execute(response.body());

                } else {
                    Toast.makeText(getApplicationContext(), "File tidak ada", Toast.LENGTH_SHORT).show();
                    alertDialogDownload.dismiss();
//                    textHeaderDownload.setText("Ups Terjadi");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//                Log.e(TAG, t.getMessage());
                alertDialogDownload.dismiss();
                Toast.makeText(DetailKegiatan.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class DownloadZipFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {

        String status="";

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call
            saveToDisk(urls[0], namafile);
            return null;
        }

        protected void onProgressUpdate(Pair<Integer, Long>... progress) {

            Log.d("API123", progress[0].second + " ");

            if (progress[0].first == 100)
//                Toast.makeText(getApplicationContext(), "File downloaded successfully", Toast.LENGTH_SHORT).show();
                status = "berhasil";

            if (progress[0].second > 0) {
                int currentProgress = (int) ((double) progress[0].first / (double) progress[0].second * 100);
                progressBar.setProgress(currentProgress);

                textProgress.setText(currentProgress + "%");

            }

            if (progress[0].first == -1) {
//                Toast.makeText(getApplicationContext(), "Download failed", Toast.LENGTH_SHORT).show();
                status = "gagal";
            }

        }

        public void doProgress(Pair<Integer, Long> progressDetails) {
            publishProgress(progressDetails);
        }

        @Override
        protected void onPostExecute(String result) {
            textKeteranganDownload.setVisibility(View.VISIBLE);
            textOKDownload.setVisibility(View.VISIBLE);
            if(status.equals("berhasil")){
                textHeaderDownload.setText("File berhasil diunduh");
                textKeteranganDownload.setText("File '"+namafile+"' disimpan di folder Download pada Penyimpanan Internal.");
            }
            else{
                textHeaderDownload.setText("File gagal diunduh");
                textKeteranganDownload.setText("Harap periksa jaringan internet dan ruang bebas penyimpanan.");
            }
        }
    }

    public void saveToDisk(ResponseBody body, String filename) {
        try {

            File destinationFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), filename);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(destinationFile);
                byte data[] = new byte[4096];
                int count;
                int progress = 0;
                long fileSize = body.contentLength();
//                Log.d(TAG, "File Size=" + fileSize);
                while ((count = inputStream.read(data)) != -1) {
                    outputStream.write(data, 0, count);
                    progress += count;
                    Pair<Integer, Long> pairs = new Pair<>(progress, fileSize);
                    downloadZipFileTask.doProgress(pairs);
                    Log.d(TAG, "Progress: " + progress + "/" + fileSize + " >>>> " + (float) progress / fileSize);
                }

                outputStream.flush();

                Log.d(TAG, destinationFile.getParent());
                Pair<Integer, Long> pairs = new Pair<>(100, 100L);
                downloadZipFileTask.doProgress(pairs);
                return;
            } catch (IOException e) {
                e.printStackTrace();
                Pair<Integer, Long> pairs = new Pair<>(-1, Long.valueOf(-1));
                downloadZipFileTask.doProgress(pairs);
                Log.d(TAG, "Failed to save the file!");
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            return;
        }
    }

    private void askForPermission(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(DetailKegiatan.this, permission) != PackageManager.PERMISSION_GRANTED) {


            if (ActivityCompat.shouldShowRequestPermissionRationale(DetailKegiatan.this, permission)) {
                ActivityCompat.requestPermissions(DetailKegiatan.this, new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(DetailKegiatan.this, new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(DetailKegiatan.this, permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getApplicationContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED) {

            if (requestCode == 101) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            }
            else if (requestCode == STORAGE_PERMISSION_CODE) {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    pickFile();
                }
            }
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(DetailKegiatan.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(DetailKegiatan.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
            pickFile();

        }
    }
    private void pickFile(){
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("*/*");
        String[] mimetypes = {"application/pdf","image/*"};
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
                    editUpload.setHint(FileUtils.getPath(this,fileUri));
                }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void insertDataFileKegiatan(Uri fileUri, String idKegiatan){

        try {
            File file = new File(FileUtils.getPath(DetailKegiatan.this,fileUri));

            //RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),file);
            RequestBody idKegiatanBody = RequestBody.create(MediaType.parse("text/plain"), idKegiatan);
            RequestBody dateAddedBody = RequestBody.create(MediaType.parse("text/plain"), DateUtils.getDateAndTime());

            MultipartBody.Part body =
                    MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            textHeaderUpload.setText("Mengupload file...");
            linearCariFile.setVisibility(View.GONE);
            linearProgressUpload.setVisibility(View.VISIBLE);
            linearBtnUpload.setVisibility(View.GONE);

            Call<GetPostPutFileKegiatan> fileCall = mApiInterface.postFileKegiatan(body,idKegiatanBody,dateAddedBody);
            fileCall.enqueue(new Callback<GetPostPutFileKegiatan>() {
                @Override
                public void onResponse(Call<GetPostPutFileKegiatan> call, Response<GetPostPutFileKegiatan>
                        response) {

                    if(response.isSuccessful()){
                        String status = response.body().getStatus();
                        String message = response.body().getMessage();
                        if(status.equals("gagal")){
                            diupload=true;
                            setStatusUpload("Gagal upload file",message,
                                    R.drawable.ic_close_black_24dp);
                        }
                        else{
                            diupload=true;
                            setStatusUpload("Berhasil diupload","File berhasil diupload.",
                                    R.drawable.ic_check_black_24dp);
                        }

                    }else{
                        diupload=true;
                        setStatusUpload("Gagal upload file","Terjadi masalah pada Server",
                                R.drawable.ic_close_black_24dp);
                    }
                }

                @Override
                public void onFailure(Call<GetPostPutFileKegiatan> call, Throwable t) {
                    Log.e("Retrofit Get", t.toString());

                    diupload=false;
                    setStatusUpload("Gagal upload file",t.getMessage(),
                            R.drawable.ic_close_black_24dp);
                }
            });
        }
        catch (NullPointerException e){
            filePath ="";
            editUpload.setHint("Cari file");
//            failure("Tidak dapat mengupload file, coba pilih file yang lain");
            Toast.makeText(this, "Tidak dapat mengupload file, coba pilih file yang lain", Toast.LENGTH_SHORT).show();
        }
    }
    private void setStatusUpload(String textHeader,String textStatus, int drawable){

        textHeaderUpload.setText(textHeader);
        textStatusUpload.setText(textStatus);
        progressBarUpload.setVisibility(View.GONE);
        imageStatusUpload.setImageResource(drawable);
        imageStatusUpload.setVisibility(View.VISIBLE);
        linearBtnUpload.setVisibility(View.VISIBLE);

        textUpload.setText("UPLOAD LAGI");


    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
    }
}
