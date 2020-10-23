package com.rmf.kuhcjr.Pengajuan;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataCuti;
import com.rmf.kuhcjr.Data.DataKegiatan;
import com.rmf.kuhcjr.Data.DataLembur;
import com.rmf.kuhcjr.Data.DataPerjalananDinas;
import com.rmf.kuhcjr.Data.GetKegiatan;
import com.rmf.kuhcjr.Data.PostPutCuti;
import com.rmf.kuhcjr.Data.PostPutKegiatan;
import com.rmf.kuhcjr.Data.PostPutLembur;
import com.rmf.kuhcjr.Data.PostPutPerjalananDinas;
import com.rmf.kuhcjr.DetailKegiatan;
import com.rmf.kuhcjr.EditDataPengajuan.EditPengajuanCuti;
import com.rmf.kuhcjr.EditDataPengajuan.EditPengajuanDinas;
import com.rmf.kuhcjr.EditDataPengajuan.EditPengajuanLembur;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.TambahDataPengajuan.TambahDataPengajuanLembur;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetailPengajuan extends AppCompatActivity {

    private LinearLayout linearDBOperasi;
    private ImageView imageUpdate,imageDelete,imageFile;

    private TextView textHeader;
    private TextView textTanggalKategori,textMulaiKategori,textSelesaiKategori;

    private TextView textTanggal,textMulai,textSelesai,textStatus,textAlasan,textUraian,textTujuan,filePengajuan,suratTugas;
    private TableLayout tableLembur;
    private TableRow tableRowFileLembur,tableRowAlasan,tableRowTujuan;

    private String tanggal,mulai,selesai,status,uraian,alasan,kategori,tujuan;
    private int id;

    private ApiInterface apiInterface;

    private boolean pindahKeEdit=false;

    //DialogHapus
    AlertDialog alertDialogHapus;
    ImageView imagehapus;
    TextView textDialog;
    TextView btnHapus;
    TextView btnBatalHapus;
    private boolean dihapus=false;


    //Dialog Download
    ProgressBar progressBar;
    TextView textProgress,textHeaderDownload;
    TextView textOKDownload,textKeteranganDownload;
    AlertDialog alertDialogDownload;
    String namafile,namaSuratTugas;
    private static final String TAG = "DetailPegajuan";
    DownloadZipFileTask downloadZipFileTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_pengajuan);
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


        //Ambil Data dari Adapter

        kategori = getIntent().getStringExtra("kategori");
        tanggal = getIntent().getStringExtra("tanggal");
        mulai = getIntent().getStringExtra("mulai");
        selesai = getIntent().getStringExtra("selesai");
        status = getIntent().getStringExtra("status");
        uraian = getIntent().getStringExtra("uraian");
        alasan = getIntent().getStringExtra("alasan");
        tujuan = getIntent().getStringExtra("tujuan");
        namaSuratTugas= getIntent().getStringExtra("tugas");
        namafile = getIntent().getStringExtra("file");
        id = getIntent().getIntExtra("id",0);


        initialUI();
        checkStatus();
        initialDialogHapus();
        initialDialogDownload();


        //Cek Kategori

        textTanggalKategori.setText("Tanggal Pengajuan");
        if(kategori.equals("lembur")){
            textHeader.setText("Detail Pengajuan Lembur");
        }else if(kategori.equals("dinas")){
            textHeader.setText("Detail Pengajuan Perjalanan Dinas");;
            tableRowTujuan.setVisibility(View.VISIBLE);
            textMulaiKategori.setText("Tanggal Mulai Perjadin");
            textSelesaiKategori.setText("Tanggal Selesai Perjadin");
        }
        else if(kategori.equals("cuti")){
            textHeader.setText("Detail Pengajuan Cuti");

            textMulaiKategori.setText("Mulai Cuti");
            textSelesaiKategori.setText("Selesai Cuti");
        }
    }

    private void checkStatus(){
        if(status.equals("Pending")){
            linearDBOperasi.setVisibility(View.VISIBLE);
            tableRowFileLembur.setVisibility(View.GONE);
            tableRowAlasan.setVisibility(View.GONE);
        }
        if(status.equals("Disetujui")){
            tableRowFileLembur.setVisibility(View.VISIBLE);
            linearDBOperasi.setVisibility(View.GONE);
            tableRowAlasan.setVisibility(View.GONE);
        }
        if(status.equals("Ditolak")){
            tableRowAlasan.setVisibility(View.VISIBLE);
            tableRowFileLembur.setVisibility(View.GONE);
            linearDBOperasi.setVisibility(View.GONE);
        }
    }

    private void initialUI(){

        textHeader = findViewById(R.id.text_header_pengajuan);

        textTanggalKategori = findViewById(R.id.tanggalKategori);
        textMulaiKategori = findViewById(R.id.mulai_kategori);
        textSelesaiKategori = findViewById(R.id.selesai_kategori);


        tableLembur = findViewById(R.id.table_lembur);
        textTanggal = findViewById(R.id.tanggal);
        textMulai = findViewById(R.id.mulai);
        textSelesai = findViewById(R.id.selesai);
        textTujuan =findViewById(R.id.tujuan);
        textStatus = findViewById(R.id.status);
        textAlasan = findViewById(R.id.alasan);
        textUraian = findViewById(R.id.uraian);

        tableRowAlasan = (TableRow) findViewById(R.id.alasan_row);
        tableRowFileLembur = (TableRow) findViewById(R.id.file_row);
        tableRowTujuan = (TableRow) findViewById(R.id.tujuan_row);

        filePengajuan = (TextView) findViewById(R.id.file_pengajuan);
        suratTugas = (TextView) findViewById(R.id.surat_tugas);

        linearDBOperasi = (LinearLayout) findViewById(R.id.linear_db_operasi);
        imageUpdate = (ImageView) findViewById(R.id.edit);
        imageDelete = (ImageView) findViewById(R.id.delete);
        imageFile = (ImageView) findViewById(R.id.file);

        textTanggal.setText(tanggal);
        textMulai.setText(mulai);
        textSelesai.setText(selesai);
        textUraian.setText(uraian);
        textStatus.setText(status);
        textAlasan.setText(alasan);
        textTujuan.setText(tujuan);

        textStatus.setTextColor(Color.parseColor(checkStatusWarna(status)));

//        cek status untuk file
        tableRowFileLembur.setVisibility(View.GONE);
        tableRowAlasan.setVisibility(View.GONE);
        linearDBOperasi.setVisibility(View.GONE);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        this.actionUI();

    }
    private void actionUI(){
        imageUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pindahKeEdit =true;
                if(kategori.equals("lembur")){

                    Intent intent = new Intent(DetailPengajuan.this, EditPengajuanLembur.class);
                    intent.putExtra("id",id);
                    intent.putExtra("tanggal",tanggal);
                    intent.putExtra("mulai",mulai);
                    intent.putExtra("selesai",selesai);
                    intent.putExtra("uraian",uraian);
                    intent.putExtra("file",namafile);
                    startActivity(intent);
                }
                if(kategori.equals("dinas")){

                    Intent intent = new Intent(DetailPengajuan.this, EditPengajuanDinas.class);
                    intent.putExtra("id",id);
                    intent.putExtra("tanggal",tanggal);
                    intent.putExtra("mulai",mulai);
                    intent.putExtra("selesai",selesai);
                    intent.putExtra("uraian",uraian);
                    intent.putExtra("tujuan",tujuan);
                    intent.putExtra("file",namafile);
                    startActivity(intent);
                }
                if(kategori.equals("cuti")){

                    Intent intent = new Intent(DetailPengajuan.this, EditPengajuanCuti.class);
                    intent.putExtra("id",id);
                    intent.putExtra("mulai",mulai);
                    intent.putExtra("selesai",selesai);
                    intent.putExtra("uraian",uraian);
                    intent.putExtra("file",namafile);

                    startActivity(intent);
                }
            }
        });
        imageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(kategori.equals("lembur")){

                    textDialog.setText("Hapus data lembur pada tanggal '"+tanggal+"'?");
                }
                if(kategori.equals("dinas")){

                    textDialog.setText("Hapus data dinas pada tanggal '"+tanggal+"'?");
                }
                if(kategori.equals("cuti")){

                    textDialog.setText("Hapus data cuti pada tanggal '"+tanggal+"'?");
                }
                alertDialogHapus.show();
            }
        });

        imageFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(namafile.isEmpty()){

                    Toast.makeText(DetailPengajuan.this, "File tidak ada", Toast.LENGTH_SHORT).show();
                }
                else{
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101);
                }

            }
        });

        filePengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(namafile.isEmpty()){

                    Toast.makeText(DetailPengajuan.this, "File tidak ada", Toast.LENGTH_SHORT).show();
                }
                else{
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101);
                }

//                Toast.makeText(DetailPengajuan.this, namafile, Toast.LENGTH_SHORT).show();
            }
        });
        suratTugas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(namafile.isEmpty()){

                    Toast.makeText(DetailPengajuan.this, "File tidak ada", Toast.LENGTH_SHORT).show();
                }
                else{
                    checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,102);
                }

            }
        });
    }

    private String checkStatusWarna(String status){
        String hasil="#ffc107";

        switch (status){
            case "Diproses": hasil = "#17a2b8";
                break;
            case "Disetujui": hasil = "#28a745";
                break;
            case "Ditolak": hasil = "#dc3545";
                break;
            default: hasil ="#ffc107";
                break;
        }
        return hasil;
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

    private void loadDataLembur(){
        Call<PostPutLembur> LemburCall = apiInterface.getDetailLembur(id);
        LemburCall.enqueue(new Callback<PostPutLembur>() {
            @Override
            public void onResponse(Call<PostPutLembur> call, Response<PostPutLembur>
                    response) {
                if(response.isSuccessful()){
                    String statuss = response.body().getStatus();

                    if(statuss.equals("berhasil")){

                        List<DataLembur> dataLembur= response.body().getListLembur();

                        tanggal = getDayName(dataLembur.get(0).getTanggal());
                        mulai = getTime(dataLembur.get(0).getMulai());
                        selesai = getTime(dataLembur.get(0).getSelesai());
                        uraian = dataLembur.get(0).getUraian();
                        status = checkStatus(dataLembur.get(0).getStatus());
                        alasan = dataLembur.get(0).getKeterangan();
                        namafile = dataLembur.get(0).getFile_pengajuan();
                        namaSuratTugas = dataLembur.get(0).getFile();

                        textTanggal.setText(tanggal);
                        textMulai.setText(mulai);
                        textSelesai.setText(selesai);
                        textUraian.setText(uraian);
                        textStatus.setText(status);
                        textAlasan.setText(alasan);

                        textStatus.setTextColor(Color.parseColor(checkStatusWarna(status)));
//                        Toast.makeText(DetailPengajuan.this, String.valueOf(id), Toast.LENGTH_SHORT).show();

                        checkStatus();
                    }else{

                        Toast.makeText(getApplicationContext(), "Data mungkin tidak ada", Toast.LENGTH_SHORT).show();
                    }

                }else{
                    Toast.makeText(DetailPengajuan.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
//                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<PostPutLembur> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void loadDataDinas(){
        Call<PostPutPerjalananDinas> DinasCall = apiInterface.getDetailDinas(id);
        DinasCall.enqueue(new Callback<PostPutPerjalananDinas>() {
            @Override
            public void onResponse(Call<PostPutPerjalananDinas> call, Response<PostPutPerjalananDinas>
                    response) {

//                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){

                    String statuss = response.body().getStatus();

                    if(statuss.equals("berhasil")){

                        List<DataPerjalananDinas> dataDinas= response.body().getListPerjalananDinas();

                        tanggal = getDayName(dataDinas.get(0).getTanggal());
                        mulai = getTime(dataDinas.get(0).getMulai());
                        selesai = getTime(dataDinas.get(0).getSelesai());
                        uraian = dataDinas.get(0).getUraian();
                        status = checkStatus(dataDinas.get(0).getStatus());
                        alasan = dataDinas.get(0).getKeterangan();
                        tujuan = dataDinas.get(0).getTujuan();
                        namafile = dataDinas.get(0).getFile_pengajuan();
                        namaSuratTugas = dataDinas.get(0).getFile();

                        textTanggal.setText(tanggal);
                        textMulai.setText(mulai);
                        textSelesai.setText(selesai);
                        textUraian.setText(uraian);
                        textStatus.setText(status);
                        textAlasan.setText(alasan);
                        textTujuan.setText(tujuan);

                        textStatus.setTextColor(Color.parseColor(checkStatusWarna(status)));
//                        Toast.makeText(DetailPengajuan.this, String.valueOf(id), Toast.LENGTH_SHORT).show();

                        checkStatus();
                    }else{

                        Toast.makeText(getApplicationContext(), "Data mungkin tidak ada", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(DetailPengajuan.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<PostPutPerjalananDinas> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadDataCuti(){
        Call<PostPutCuti> CutiCall = apiInterface.getDetailCuti(id);
        CutiCall.enqueue(new Callback<PostPutCuti>() {
            @Override
            public void onResponse(Call<PostPutCuti> call, Response<PostPutCuti>
                    response) {
                if(response.isSuccessful()){
                    String statuss = response.body().getStatus();

                    if(statuss.equals("berhasil")){

                        List<DataCuti> dataCuti= response.body().getListCuti();


                        mulai = getDayName(dataCuti.get(0).getMulai());
                        selesai = getDayName(dataCuti.get(0).getSelesai());
                        uraian = dataCuti.get(0).getUraian();
                        status = checkStatus(dataCuti.get(0).getStatus());
                        alasan = dataCuti.get(0).getKeterangan();
                        namafile = dataCuti.get(0).getFile_pengajuan();
                        namaSuratTugas = dataCuti.get(0).getFile();

                        textMulai.setText(mulai);
                        textSelesai.setText(selesai);
                        textUraian.setText(uraian);
                        textStatus.setText(status);
                        textAlasan.setText(alasan);


                        textStatus.setTextColor(Color.parseColor(checkStatusWarna(status)));
//                        Toast.makeText(DetailPengajuan.this, String.valueOf(id), Toast.LENGTH_SHORT).show();

                        checkStatus();
                    }else{

                        Toast.makeText(getApplicationContext(), "Data mungkin tidak ada", Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(DetailPengajuan.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
//                progressBar.setVisibility(View.GONE);

            }

            @Override
            public void onFailure(Call<PostPutCuti> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getDayName(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        String akhir = output.format(date);

        return akhir;

    }
    private String getTime(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date =null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("HH:mm");
        String akhir = output.format(date);

        return akhir;

    }
    private String checkStatus(int status){
        String hasil="";

        switch (status){

            case 1: hasil = "Diproses";
                break;
            case 2: hasil = "Disetujui";
                break;
            case 3: hasil = "Ditolak";
                break;
            default: hasil = "Pending";
                break;
        }
        return hasil;
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
                    if(kategori.equals("lembur")){

                        hapusDataLembur();
                    }
                    if(kategori.equals("dinas")){

                        hapusDataDinas();
                    }
                    if(kategori.equals("cuti")){

                        hapusDataCuti();
                    }
                }
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

    public void downloadZipFile(final String filename, int requestCode) {

        alertDialogDownload.show();
        Call<ResponseBody> call= apiInterface.downloadFile("assets/files/pengajuan/"+filename);;
        if(requestCode==101){
             call = apiInterface.downloadFile("assets/files/pengajuan/"+filename);
        }
        else if(requestCode==102){
            call = apiInterface.downloadFile("assets/files/surat/"+filename);
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {

//                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

                    downloadZipFileTask = new DownloadZipFileTask(filename);
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
                Toast.makeText(DetailPengajuan.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class DownloadZipFileTask extends AsyncTask<ResponseBody, Pair<Integer, Long>, String> {

        String status="";
        String filename="";

        public DownloadZipFileTask(String filename){
            this.filename= filename;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(ResponseBody... urls) {
            //Copy you logic to calculate progress and call
            saveToDisk(urls[0], filename);
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
                textKeteranganDownload.setText("File '"+filename+"' disimpan di folder Download pada Penyimpanan Internal.");
            }
            else{
                textHeaderDownload.setText("File gagal diunduh");
                textKeteranganDownload.setText("Gagal mengunduh file");
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
                Toast.makeText(DetailPengajuan.this,"Gagal menyimpan file",Toast.LENGTH_SHORT);
                return;
            } finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.d(TAG, "Failed to save the file!");
            Toast.makeText(DetailPengajuan.this,"Gagal menyimpan file",Toast.LENGTH_SHORT);
            return;
        }
    }

    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(DetailPengajuan.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(DetailPengajuan.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
            if(requestCode==101){

                alertDialogDownload.show();
                downloadZipFile(namafile,requestCode);
            }
            else if(requestCode==102){
                alertDialogDownload.show();
                downloadZipFile(namaSuratTugas,requestCode);
            }

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode ==101) {
            alertDialogDownload.show();
            downloadZipFile(namafile,requestCode);
        }
        else if (requestCode ==102) {
            alertDialogDownload.show();
            downloadZipFile(namaSuratTugas,requestCode);
        }
        else {
                Toast.makeText(DetailPengajuan.this,
                        "Gallery Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();

        }
    }


    private void hapusDataLembur(){
        textDialog.setText("Harap tunggu...");
        Call<PostPutLembur> LemburCall = apiInterface.deleteLembur(id,namafile);
        LemburCall.enqueue(new Callback<PostPutLembur>() {
            @Override
            public void onResponse(Call<PostPutLembur> call, Response<PostPutLembur>
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
            public void onFailure(Call<PostPutLembur> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                imagehapus.setImageResource(R.drawable.ic_error_black_24dp);
                textDialog.setText(t.getMessage());
//                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hapusDataDinas(){
        textDialog.setText("Harap tunggu...");
        Call<PostPutPerjalananDinas> DinasCall = apiInterface.deletePerjalananDinas(id,namafile);
        DinasCall.enqueue(new Callback<PostPutPerjalananDinas>() {
            @Override
            public void onResponse(Call<PostPutPerjalananDinas> call, Response<PostPutPerjalananDinas>
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
            public void onFailure(Call<PostPutPerjalananDinas> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                imagehapus.setImageResource(R.drawable.ic_error_black_24dp);
                textDialog.setText(t.getMessage());
//                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hapusDataCuti(){
        textDialog.setText("Harap tunggu...");
        Call<PostPutCuti> CutiCall = apiInterface.deleteCuti(id,namafile);
        CutiCall.enqueue(new Callback<PostPutCuti>() {
            @Override
            public void onResponse(Call<PostPutCuti> call, Response<PostPutCuti>
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
            public void onFailure(Call<PostPutCuti> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                imagehapus.setImageResource(R.drawable.ic_error_black_24dp);
                textDialog.setText(t.getMessage());
//                Toast.makeText(getApplicationContext(), "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(kategori.equals("lembur")){

            if(pindahKeEdit){
                loadDataLembur();
                pindahKeEdit=false;
            }
        }
        if(kategori.equals("dinas")){

            if(pindahKeEdit){
                loadDataDinas();
                pindahKeEdit=false;
            }
        }
        if(kategori.equals("cuti")){

            if(pindahKeEdit){
                loadDataCuti();
                pindahKeEdit=false;
            }
        }
    }
}
