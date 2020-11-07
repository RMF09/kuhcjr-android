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
import com.rmf.kuhcjr.Pengumuman;
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
               startActivity(new Intent(getContext(),Pengumuman.class));
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
    private void adaNotif(){
        notifPengumuman.setVisibility(View.VISIBLE);
        notifCard.setVisibility(View.VISIBLE);
    }


    @Override
    public void onResume() {
        super.onResume();
        checkKegiatanIdentitas();

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
                        checkPengumuman();
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
    private void checkPengumuman(){

        Call<GetPengumuman> pengumumanCall = apiInterface.getPengumuman("pengumuman");
        pengumumanCall.enqueue(new Callback<GetPengumuman>() {
            @Override
            public void onResponse(Call<GetPengumuman> call, Response<GetPengumuman> response) {
                if (response.isSuccessful()){
                    if(response.body().getStatus().equals("berhasil")){

                        List<DataPengumuman> list = response.body().getListPengumuman();
                        jumlah_pengumuman = list.size();
                        notifCounter.setText(String.valueOf(jumlahNotif()));
                        adaNotif();
                    }
                    else{
                        tidakAdaNotif();
                    }

                }
            }

            @Override
            public void onFailure(Call<GetPengumuman> call, Throwable t) {

            }
        });
    }
}
