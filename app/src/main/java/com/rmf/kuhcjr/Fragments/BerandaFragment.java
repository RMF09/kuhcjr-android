package com.rmf.kuhcjr.Fragments;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.rmf.kuhcjr.Absensi;
import com.rmf.kuhcjr.Adapter.AdapterRVDataPengumuman;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataPengumuman;
import com.rmf.kuhcjr.Data.DataUserLogin;
import com.rmf.kuhcjr.Data.GetPengumuman;
import com.rmf.kuhcjr.Data.GetUserLogin;
import com.rmf.kuhcjr.DetailKegiatan;
import com.rmf.kuhcjr.EditProfile;
import com.rmf.kuhcjr.Services.AlarmReceiver;
import com.rmf.kuhcjr.LaporanKinerja;
import com.rmf.kuhcjr.PeminjamanMobil;
import com.rmf.kuhcjr.Pengajuan.PengajuanCuti;
import com.rmf.kuhcjr.Pengajuan.PengajuanDinas;
import com.rmf.kuhcjr.Pengajuan.PengajuanLembur;
import com.rmf.kuhcjr.PengembalianMobil;
import com.rmf.kuhcjr.PengisianSkp;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.SharedPrefs;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BerandaFragment extends Fragment {
    public static final String MESSAGE_STATUS = "message_status";

    CardView cardPengajuan,cardPeminjamanMobil,cardPengembalianMobil,cardAbsensi,cardLaporan,cardSKP;

    PendingIntent pendingIntent;
    AlertDialog alertDialog,alertDialogPengumuman;

    private View view;
    private TextView notifCounter;
    private ImageView notifPengumuman;
    private CardView notifCard;
    private int jumlah_pengumuman=0;
    private static final int MAX_NOTIF =99;

    private ApiInterface apiInterface;
    //list notif
    List<DataPengumuman> list= new ArrayList<>();
    RecyclerView rv;
    AdapterRVDataPengumuman adapterRVDataPengumuman;

    //Donwload Lampiran
    DownloadZipFileTask downloadZipFileTask;
    public String namafile;
    private  static final String TAG = "BerandaFragment";

    //Dialog Download

    ProgressBar progressBar;
    TextView textProgress,textHeaderDownload;
    TextView textOKDownload,textKeteranganDownload;
    AlertDialog alertDialogDownload;

    //Dialog Peringatan
    AlertDialog alertDialogPeringatan;
    TextView textIsiIdentitas;

    private boolean kegiatan_identitas_diisi=false;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_beranda,container,false);
        this.view =view;
        initialUI(view);
        initialPengumuman();


        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        /* Retrieve a PendingIntent that will perform a broadcast */
        Intent alarmIntent = new Intent(getContext(), AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(getContext(), 0, alarmIntent, 0);
        startAt10();

        askForPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,101);
//        checkKegiatanIdentitas();

        return view;
    }


    private void initialUI(View view){

        cardPengajuan = (CardView) view.findViewById(R.id.card_pengajuan);
        cardPeminjamanMobil =(CardView) view.findViewById(R.id.card_peminjaman_mobil);
        cardPengembalianMobil =(CardView) view.findViewById(R.id.card_pengembalian_mobil);
        cardAbsensi = (CardView) view.findViewById(R.id.card_absensi);
        cardLaporan = (CardView) view.findViewById(R.id.card_laporan_kinerja);
        cardSKP = view.findViewById(R.id.card_skp);

        initialDialogPengajuan();
        initialDialogPengumuman();
        initialDialogDownload();
        initialDialogPeringatan();
        this.actionUI(view);

    }


    private void actionUI(final View view){
        cardPengajuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
        cardPeminjamanMobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), PeminjamanMobil.class));
            }
        });
        cardPengembalianMobil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), PengembalianMobil.class));
            }
        });
        cardAbsensi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                startActivity(new Intent(view.getContext(), Absensi.class));
            }
        });
        cardLaporan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), LaporanKinerja.class));
            }
        });
        cardSKP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(view.getContext(), PengisianSkp.class));
            }
        });
    }

    private void initialDialogPengumuman(){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final View view  = getLayoutInflater().inflate(R.layout.dialog_pengumuman,null);
        rv = (RecyclerView) view.findViewById(R.id.rv_pengumuman);
        rv.setLayoutManager(new LinearLayoutManager(this.view.getContext(),LinearLayoutManager.VERTICAL,false));
        builder.setView(view);
        builder.setCancelable(true);
        alertDialogPengumuman = builder.create();

    }
    private void initialDialogPengajuan(){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final View view = getLayoutInflater().inflate(R.layout.dialog_pilih_pengajuan,null);

        final RadioGroup rg = (RadioGroup) view.findViewById(R.id.rg_pengajuan);
        TextView textBerikutnya = (TextView) view.findViewById(R.id.textBerikutnya);
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog = builder.create();


        textBerikutnya.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pilihan = -1;
                pilihan = rg.getCheckedRadioButtonId();

                if(rg.getCheckedRadioButtonId()!=-1){

                    RadioButton rbPilihan = (RadioButton) view.findViewById(pilihan);
                    String textRb = (String) rbPilihan.getText();
                    alertDialog.dismiss();
                    rg.clearCheck();

                    switch (textRb){
                        case "Lembur" :
                            startActivity(new Intent(view.getContext(), PengajuanLembur.class));
                            break;
                        case "Perjalanan Dinas" :
                            startActivity(new Intent(view.getContext(), PengajuanDinas.class));
                            break;
                        case "Cuti" :
                            startActivity(new Intent(view.getContext(), PengajuanCuti.class));
                            break;
                    }
                }
                else{
                    Snackbar.make(v,"Harap pilih pengajuan",Snackbar.LENGTH_SHORT).setAction("Action",null).show();
                }

            }
        });

    }

    public void startAt10() {
        AlarmManager manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 0);
        if(calendar.getTime().compareTo(new Date())<0){
            calendar.add(Calendar.DAY_OF_MONTH,1);
        }

        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    private void initialPengumuman(){
        notifCounter = view.findViewById(R.id.notif_p_counter);
        notifPengumuman = view.findViewById(R.id.notif_pengumuman);
        notifCard = view.findViewById(R.id.notif_p_card);
        tidakAdaNotif();
        notifPengumuman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogPengumuman.show();
            }
        });
    }
    private int jumlahNotif(){
        if(jumlah_pengumuman>MAX_NOTIF){
            return 99;
        }else{
            return jumlah_pengumuman;
        }
    }

    private void tidakAdaNotif(){
        notifPengumuman.setVisibility(View.GONE);
        notifCard.setVisibility(View.GONE);
    }


    @Override
    public void onResume() {
        super.onResume();
//        checkPengumuman();

    }
    //Download FIle
    public void downloadZipFile(String namafile) {
        alertDialogPengumuman.dismiss();
        alertDialogDownload.show();
        Call<ResponseBody> call = apiInterface.downloadFile("assets/files/pengumuman/"+namafile);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {
                if (response.isSuccessful()) {

//                    Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();

                    downloadZipFileTask = new BerandaFragment.DownloadZipFileTask();
                    downloadZipFileTask.execute(response.body());

                } else {
                    Toast.makeText(getContext(), "File tidak ada", Toast.LENGTH_SHORT).show();
                    alertDialogDownload.dismiss();
//                    textHeaderDownload.setText("Ups Terjadi");
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
//                t.printStackTrace();
//                Log.e(TAG, t.getMessage());
                alertDialogDownload.dismiss();
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
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
        if (ContextCompat.checkSelfPermission(getContext(), permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), permission)) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);

            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{permission}, requestCode);
            }
        } else if (ContextCompat.checkSelfPermission(getContext(), permission) == PackageManager.PERMISSION_DENIED) {
            Toast.makeText(getContext(), "Permission was denied", Toast.LENGTH_SHORT).show();
        }
    }
    private void initialDialogDownload(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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

    private void initialDialogPeringatan(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.dialog_peringatan_identitas,null);

        textIsiIdentitas = (TextView) view.findViewById(R.id.isi_identitas);

        builder.setView(view);
        alertDialogPeringatan = builder.create();
        alertDialogPeringatan.setCancelable(false);

        textIsiIdentitas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogPeringatan.dismiss();
                startActivity(new Intent(getActivity().getApplicationContext(),EditProfile.class));
            }
        });

    }

    private void checkKegiatanIdentitas(){
        String username = SharedPrefs.getInstance(getContext()).LoggedInUser();
        String metode = "ambilData";

        Call<GetUserLogin> getUserLoginCall = apiInterface.getDataProfile(username,metode);
        getUserLoginCall.enqueue(new Callback<GetUserLogin>() {
            @Override
            public void onResponse(Call<GetUserLogin> call, Response<GetUserLogin> response) {
                if(response.isSuccessful()){

                    if(response.body().getStatus().equals("berhasil")){
                        kegiatan_identitas_diisi=true;
                    }else{
                        kegiatan_identitas_diisi=false;
                        alertDialogPeringatan.show();
                    }
                }
                else{
//                   SERVER DOWN
                    Toast.makeText(getContext(), "Terjadi kesalahan pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserLogin> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
