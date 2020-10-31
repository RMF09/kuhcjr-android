package com.rmf.kuhcjr;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.rmf.kuhcjr.Adapter.AdapterRVPeminjamanKendaraan;
import com.rmf.kuhcjr.Adapter.SpinnerAdapter;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataAbsensiPegawai;
import com.rmf.kuhcjr.Data.DataKantor;
import com.rmf.kuhcjr.Data.DataLembur;
import com.rmf.kuhcjr.Data.DataPeminjamanKendaraan;
import com.rmf.kuhcjr.Data.GetAbsensiPegawai;
import com.rmf.kuhcjr.Data.GetKantor;
import com.rmf.kuhcjr.Data.GetPeminjamanKendaraan;
import com.rmf.kuhcjr.Data.PostPutLembur;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Absensi extends AppCompatActivity implements OnMapReadyCallback {

    private final String pesanTidakBisaAbsen="Anda tidak diperkenankan untuk absen masuk. Absen masuk dimulai dari jam 5 - 10 siang. Terima kasih.";
    private MapView mapView;
    private GoogleMap gmap;
    private boolean dalamJangkauan =false,libur=false,initial=false,checkInOut=false,adaLembur=false;
    private String statusAbsensi,statusAbsensiLembur,tanggalLembur,mulaiLembur,selesaiLembur;
    private int id=0,idAbsensiLembur=0, jamAwalAbsen=5,jamAkhirAbsen=10,statusLembur;
    private String tanggalSekarang;
    private String actionAbsen;

    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final int REQUEST_CODE = 101;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";


    String namaKantor = "Kantor 1";
    double lat = -6.801684, longt = 107.090028;
    //UI
    private Spinner spinnerKantor;
    private Button btnAbsen,btnAbsenLembur;
    private CardView cardCheckYourLocation, cardCheckOfficeLocation;


    //0 = saya,  1= kantor
    int lihatLokasi = 0;

    //API
    ApiInterface mApiInterface;
    List<DataKantor> kantorList;

    AlertDialog alertDialog;
    ImageView imageViewDialog;
    ProgressBar progressBarDialog;
    TextView textOKDialog,textDialog;

    //Dialog Absen
    AlertDialog alertDialogAbsen;
    ImageView imageViewDialogAbsen;
    TextView textYaDialogAbsesn,textDialogAbsen,textBatalDialogAbsen,textTitleDialogAbsen;

    //Masalah Jaringan Layout
    private LinearLayout linearMasalahJaringan;
    private TextView textERR,textMuatUlang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absensi);

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
        initialDialog();
        initialDialogAbsen();


        checkJadwalLibur();
        //1. Check Jadwal Libur
        //2. Check Absensi Hari ini
        //3. Check Jadwal Lembur
        //4. Check Tanggal Skerang
        //5. Jika ada lembur -> Check Absensi Lembur
        //6. Load Data kantor


        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAP_VIEW_BUNDLE_KEY);
        }

        mapView = findViewById(R.id.map_view);
        mapView.onCreate(mapViewBundle);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//        fetchLocation();

//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
//            return;
//        }
//        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//                2000,
//                10, locationListenerGPS);


    }

    private void initialUI(){
        btnAbsen =(Button) findViewById(R.id.btn_absen);
        btnAbsenLembur =(Button) findViewById(R.id.btn_absen_lembur);
        spinnerKantor = (Spinner) findViewById(R.id.spinner_lokasi);
        cardCheckYourLocation = (CardView) findViewById(R.id.card_check_your_location);
        cardCheckOfficeLocation = (CardView) findViewById(R.id.card_check_office_location);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        //Masalah Jaringan
        linearMasalahJaringan = (LinearLayout) findViewById(R.id.layout_masalah_jaringan);
        textERR = (TextView) findViewById(R.id.text_error_code);
        textMuatUlang = (TextView) findViewById(R.id.text_muat_ulang_data);

        textMuatUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkJadwalLibur();
            }
        });


        this.actionUI();
        btnAbsen.setEnabled(false);

    }

    private void actionUI(){
        spinnerKantor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                DataKantor dataKantor = (DataKantor) parent.getItemAtPosition(position);
                namaKantor = dataKantor.getLokasi();
                aturLokasiKantorDipilih(dataKantor.getLokasi(),dataKantor.getLat(),dataKantor.getLng(),1);
//                Toast.makeText(Absensi.this, "Lat : "+dataKantor.getLat(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        cardCheckYourLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lihatLokasi =0;
                fetchLocation();
            }
        });
        cardCheckOfficeLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lihatLokasi =1;
                fetchLocation();
            }
        });





        btnAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dalamJangkauan){
                    checkInOut=true;
                    if(statusAbsensi.equals("sudah absen")){
                        actionAbsen = "check_out";
                        showDialogAbsen();
//                        checkOUT();
                    }
                    else{
                        //checkin
                        actionAbsen = "check_in";
                        showDialogAbsen();
//                        checkWaktu();
                    }
                }else{
                    Toast.makeText(Absensi.this, "Tidak dapat mengabsen karena Anda diluar jangkauan kantor", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnAbsenLembur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adaLembur){
                    if(statusAbsensi.equals("sudah absen")){
                        if(statusAbsensiLembur.equals("belum absen")){
                            actionAbsen ="check_in_lembur";
                            showDialogAbsen();
//                            checkWaktuLembur("in");
                        }else if(statusAbsensiLembur.equals("sudah absen")){
                            actionAbsen ="check_out_lembur";
                            showDialogAbsen();
//                            checkWaktuLembur("out");
                        }
                    }else {
                        Toast.makeText(Absensi.this, "Silahkan absen masuk kantor terlebih dahulu", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
    private void initialDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_absensi, null);

        builder.setView(view);
        alertDialog = builder.create();

        imageViewDialog = (ImageView) view.findViewById(R.id.image_peringatan);
        progressBarDialog = (ProgressBar) view.findViewById(R.id.progress);
        textDialog = (TextView) view.findViewById(R.id.text_dialog);
        textOKDialog = (TextView) view.findViewById(R.id.ok);

        alertDialog.setCancelable(false);

        textOKDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(libur){
                    finish();
                }
            }
        });


    }
    private void initialDialogAbsen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = getLayoutInflater().inflate(R.layout.dialog_check_absen, null);

        builder.setView(view);
        textTitleDialogAbsen = (TextView) view.findViewById(R.id.text_title_absen);
        imageViewDialogAbsen = (ImageView) view.findViewById(R.id.image_peringatan_absen);
        textDialogAbsen = (TextView) view.findViewById(R.id.text_dialog_absen);
        textYaDialogAbsesn= (TextView) view.findViewById(R.id.ya_absen);
        textBatalDialogAbsen =(TextView) view.findViewById(R.id.batal_absen);
        alertDialogAbsen = builder.create();

        alertDialogAbsen.setCancelable(false);

        textBatalDialogAbsen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogAbsen.dismiss();
            }
        });
        textYaDialogAbsesn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(actionAbsen.equals("check_in")){
                    checkWaktu();
                }else if(actionAbsen.equals("check_out")){
                    checkOUT();
                }else if(actionAbsen.equals("check_in_lembur")){
                    checkWaktuLembur("in");
                }
                else if(actionAbsen.equals("check_out_lembur")){
                    checkWaktuLembur("out");
                }else{
                    //OK button
                    alertDialogAbsen.dismiss();
                    textBatalDialogAbsen.setVisibility(View.INVISIBLE);
                    textYaDialogAbsesn.setText("YA");
                }
            }
        });


    }
    private void showDialogAbsen(){
        imageViewDialogAbsen.setImageResource(R.drawable.ic_beenhere_black_24dp);

        if(actionAbsen.equals("check_in")){
            textTitleDialogAbsen.setText("Konfirmasi absen masuk kantor");
            textDialogAbsen.setText("Mulai absensi masuk kantor?");
        }
        if(actionAbsen.equals("check_out")){
            textTitleDialogAbsen.setText("Konfirmasi absen pulang kantor");
            textDialogAbsen.setText("Mulai absensi pulang kantor?");
        }
        if(actionAbsen.equals("check_in_lembur")){
            textTitleDialogAbsen.setText("Konfirmasi absen masuklembur ");
            textDialogAbsen.setText("Mulai absensi masuk lembur?");
        }
        if(actionAbsen.equals("check_out_lembur")){
            textTitleDialogAbsen.setText("Konfirmasi absen selesai lembur");
            textDialogAbsen.setText("Mulai absensi selesai lembur?");
        }
        textBatalDialogAbsen.setVisibility(View.VISIBLE);
        alertDialogAbsen.show();
    }
    private void setSuccessDialogAbsen(){
        imageViewDialogAbsen.setImageResource(R.drawable.ic_beenhere_green_24dp);

        if(actionAbsen.equals("check_in")){
            textTitleDialogAbsen.setText("Konfirmasi absen masuk kantor");
            textDialogAbsen.setText("Berhasil absensi masuk kantor");
        }
        if(actionAbsen.equals("check_out")){
            textTitleDialogAbsen.setText("Konfirmasi absen pulang kantor");
            textDialogAbsen.setText("Berhasil absensi pulang kantor");
        }
        if(actionAbsen.equals("check_in_lembur")){
            textTitleDialogAbsen.setText("Konfirmasi absen masuk lembur ");
            textDialogAbsen.setText("Berhasil absensi masuk lembur");
        }
        if(actionAbsen.equals("check_out_lembur")){
            textTitleDialogAbsen.setText("Konfirmasi absen selesai lembur");
            textDialogAbsen.setText("Berhasil absensi selesai lembur");
        }
        actionAbsen="OK";
        textBatalDialogAbsen.setVisibility(View.INVISIBLE);
        textYaDialogAbsesn.setText("OK");
    }
    private void setFailedDialogAbsen(String message){
        imageViewDialogAbsen.setImageResource(R.drawable.ic_error_black_24dp);

        if(actionAbsen.equals("check_in")){
            textTitleDialogAbsen.setText("Gagal absen masuk kantor");
            textDialogAbsen.setText(message);
        }
        if(actionAbsen.equals("check_out")){
            textTitleDialogAbsen.setText("Konfirmasi absen pulang kantor");
            textDialogAbsen.setText(message);
        }
        if(actionAbsen.equals("check_in_lembur")){
            textTitleDialogAbsen.setText("Konfirmasi absen masuk lembur ");
            textDialogAbsen.setText(message);
        }
        if(actionAbsen.equals("check_out_lembur")){
            textTitleDialogAbsen.setText("Konfirmasi absen selesai lembur");
            textDialogAbsen.setText(message);
        }
        actionAbsen="OK";
        textBatalDialogAbsen.setVisibility(View.INVISIBLE);
        textYaDialogAbsesn.setText("OK");
    }


    private void aturLokasiKantorDipilih(String namaKantor, double lat, double longt,int lihatLokasi){
        this.namaKantor = namaKantor;
        this.lat = lat;
        this.longt = longt;
        this.lihatLokasi = lihatLokasi;
        fetchLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);



        Bundle mapViewBundle = outState.getBundle(MAP_VIEW_BUNDLE_KEY);
        if (mapViewBundle == null) {
            mapViewBundle = new Bundle();
            outState.putBundle(MAP_VIEW_BUNDLE_KEY, mapViewBundle);
        }

        mapView.onSaveInstanceState(mapViewBundle);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if(initial){
            checkAbsensiHariIni();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mapView.onStop();
    }
    @Override
    protected void onPause() {
        mapView.onPause();
        super.onPause();
    }
    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {

        googleMap.clear();

        LatLng latLng2 = new LatLng(lat,longt);
        MarkerOptions markerOptions2 = new MarkerOptions().position(latLng2).title(namaKantor);

        //Circle
        CircleOptions circleOptions = new CircleOptions();

        circleOptions.center(latLng2);
//        5km
//        circleOptions.radius(500)
        circleOptions.radius(35);
        circleOptions.fillColor(Color.parseColor("#6817A2B8"));
        circleOptions.strokeWidth(1);
        circleOptions.strokeColor(Color.parseColor("#17a2b8"));

        LatLng latLng = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions().position(latLng).title("I am here!");

        googleMap.addMarker(markerOptions);
        googleMap.addMarker(markerOptions2);
        googleMap.addCircle(circleOptions);

        if(lihatLokasi==0) {
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18));
        }
        else{
            googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng2));
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng2, 18));
        }

        float[] jarak  = new float[2];

        Location.distanceBetween(markerOptions.getPosition().latitude,markerOptions.getPosition().longitude,circleOptions.getCenter().latitude,circleOptions.getCenter().longitude,jarak);

        if(jarak[0] > circleOptions.getRadius()){
//            Toast.makeText(this, "Diluar jangkauan", Toast.LENGTH_SHORT).show();
            dalamJangkauan=false;
        }
        else{
//            Toast.makeText(this, "Didalam jangkauan", Toast.LENGTH_SHORT).show();
            dalamJangkauan=true;
        }
    }
    private void fetchLocation() {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
//                    Toast.makeText(getApplicationContext(), currentLocation.getLatitude() + "" + currentLocation.getLongitude(), Toast.LENGTH_SHORT).show();
                    mapView.getMapAsync(Absensi.this);

                }
            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    fetchLocation();
                }
                break;
        }
    }

    LocationListener locationListenerGPS=new LocationListener() {
        @Override
        public void onLocationChanged(android.location.Location location) {
            double latitude=location.getLatitude();
            double longitude=location.getLongitude();
            String msg="New Latitude: "+latitude + "New Longitude: "+longitude;
//            Toast.makeText(Absensi.this,msg,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void loadDataKantor(){
        textDialog.setText("Memuat data lokasi kantor");
        Call<GetKantor> KantorCall = mApiInterface.getKantor();
        KantorCall.enqueue(new Callback<GetKantor>() {
            @Override
            public void onResponse(Call<GetKantor> call, Response<GetKantor>
                    response) {

                if(response.isSuccessful()){
                    if(response.body().getStatus().equals("berhasil")){

                        kantorList = response.body().getListDataKantor();
                        ArrayAdapter<DataKantor> dataAdapter = new SpinnerAdapter(Absensi.this, android.R.layout.simple_spinner_item, kantorList);
                        spinnerKantor.setAdapter(dataAdapter);
//                        aturLokasiKantorDipilih(dataKantor.getLokasi(),dataKantor.getLat(),dataKantor.getLng(),1);
                        aturLokasiKantorDipilih(kantorList.get(0).getLokasi(),kantorList.get(0).getLat(),kantorList.get(0).getLng(),1);
                        if(alertDialog.isShowing()){
                            alertDialog.dismiss();
                        }
                        mapView.setVisibility(View.VISIBLE);
                        linearMasalahJaringan.setVisibility(View.GONE);

                    }
                }else {
                    mapView.setVisibility(View.GONE);
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada saat memuat data lokasi kantor dari server");
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetKantor> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                mapView.setVisibility(View.GONE);
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText(t.getMessage());
                alertDialog.dismiss();
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


    private void checkWaktu(){
        int jamSekarang  = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if(jamSekarang<jamAkhirAbsen && jamSekarang>=jamAwalAbsen){
            checkIN();
        }
        else{
            setFailedDialogAbsen(pesanTidakBisaAbsen);
        }
    }

//    private void checkWaktu(){
//        textDialogAbsen.setText("Harap tunggu...");
//        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.getCheckWaktu("waktu");
//        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
//            @Override
//            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
//                if(response.isSuccessful()){
//                    String status = response.body().getStatus();
//                    if(status.equals("berhasil")){
//                        String message = response.body().getMessage();
//                        if(statusAbsensi.equals("belum absen")){
//                            if(Integer.valueOf(ambilJam(message,"y/M/d H:m:s","H"))<jamAkhirAbsen && Integer.valueOf(ambilJam(message,"y/M/d H:m:s","H"))>jamAwalAbsen ){
//                                //CheckIN
//                                checkIN();
//                            }
//                            else{
//                                setFailedDialogAbsen(pesanTidakBisaAbsen);
////                                Toast.makeText(Absensi.this, pesanTidakBisaAbsen, Toast.LENGTH_SHORT).show();
//                            }
//                        }
//
//                    }else{
//                        setFailedDialogAbsen("Gagal cek waktu server");
//                    }
//                }
//                else {
//                    setFailedDialogAbsen("Terjadi masalah pada Server");
////                    Toast.makeText(Absensi.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//            @Override
//            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
//                setFailedDialogAbsen(t.getMessage());
////                Toast.makeText(Absensi.this, t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    private void checkAbsensiHariIni(){
        textDialog.setText("Memeriksa status absensi");
        btnAbsen.setEnabled(false);
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.getCheckAbsen(username);
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    statusAbsensi=status;
                    btnAbsen.setEnabled(true);

//                    Toast.makeText(Absensi.this, statusAbsensi, Toast.LENGTH_SHORT).show();
                    if(statusAbsensi.equals("belum absen")){
                        btnAbsen.setText("Absen Masuk Kantor");
                    }else if(statusAbsensi.equals("sudah absen")){
                        btnAbsen.setText("Absen Pulang Kantor");
                        List<DataAbsensiPegawai> ambilID = response.body().getListDataAbsensiPegawai();
                        id= ambilID.get(0).getId();
//                        Toast.makeText(Absensi.this, "ID : "+id, Toast.LENGTH_SHORT).show();
                    }else if(statusAbsensi.equals("sudah pulang")){
                        btnAbsen.setText("Anda Sudah Pulang");
                        btnAbsen.setEnabled(false);
                    }
                    checkJadwalLembur();


                }
                else {
                    mapView.setVisibility(View.GONE);
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada saat memeriksa status absensi dari server");
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
                mapView.setVisibility(View.GONE);
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText(t.getMessage());
                alertDialog.dismiss();
            }
        });
    }
    private void checkIN(){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.CheckIn("in",username,String.valueOf(lat),String.valueOf(longt),namaKantor);
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        checkAbsensiHariIni();

//                        Toast.makeText(Absensi.this, "Berhasil check in", Toast.LENGTH_SHORT).show();
                        setSuccessDialogAbsen();

                    }
                }
                else {
//                    Toast.makeText(Absensi.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                    setFailedDialogAbsen("Terjadi masalah pada Server");
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
                setFailedDialogAbsen(t.getMessage());
//                Toast.makeText(Absensi.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkOUT(){
        textDialogAbsen.setText("Harap tunggu...");
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.CheckOut("out",id,username,String.valueOf(lat),String.valueOf(longt),namaKantor);
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        checkAbsensiHariIni();
                        setSuccessDialogAbsen();
//                        Toast.makeText(Absensi.this, "Berhasil check out", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    setFailedDialogAbsen("Terjadi masalah pada Server");
//                    Toast.makeText(Absensi.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
                setFailedDialogAbsen(t.getMessage());
//                Toast.makeText(Absensi.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkINLembur(){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.CheckIn("inlembur",username,String.valueOf(lat),String.valueOf(longt),namaKantor);
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){

                        checkAbsensiLembur();
                        setSuccessDialogAbsen();
//                        Toast.makeText(Absensi.this, "Berhasil check in lembur", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    setFailedDialogAbsen("Terjadi masalah pada Server");
//                    Toast.makeText(Absensi.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
                setFailedDialogAbsen(t.getMessage());
//                Toast.makeText(Absensi.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void checkOUTLembur(){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.CheckOut("outlembur",idAbsensiLembur,username,String.valueOf(lat),String.valueOf(longt),namaKantor);
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        checkAbsensiLembur();
                        setSuccessDialogAbsen();
//                        Toast.makeText(Absensi.this, "Berhasil check out lembur", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    setFailedDialogAbsen("Terjadi masalah pada Server");
//                    Toast.makeText(Absensi.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
//                Toast.makeText(Absensi.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                setFailedDialogAbsen(t.getMessage());
            }
        });
    }

    private void checkJadwalLibur(){

        linearMasalahJaringan.setVisibility(View.GONE);
        textDialog.setText("Memeriksa jadwal libur");
        alertDialog.show();
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.CheckJadwaLibur("libur");
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String message = response.body().getMessage();
                    if(message.equals("Fri")){
                        libur = true;

                        peringatanLibur();

                    }else{
                        checkAbsensiHariIni();


                    }

                }
                else {
                    mapView.setVisibility(View.GONE);
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada saat mengambil jadwal libur dari server");
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
                mapView.setVisibility(View.GONE);
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText(t.getMessage());
                alertDialog.dismiss();
            }
        });
    }


    private String ambilJam(String tgl,String polaAwal, String polaAkhir){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(polaAwal);

        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat(polaAkhir);
        String akhir = output.format(date);

        return akhir;

    }

    private void peringatanLibur(){
        progressBarDialog.setVisibility(View.GONE);
        imageViewDialog.setImageResource(R.drawable.ic_mood_black_24dp);
        imageViewDialog.setVisibility(View.VISIBLE);
        textOKDialog.setVisibility(View.VISIBLE);
        textDialog.setText("Hari ini libur, tap ok untuk kembali");

    }
    private void checkJadwalLembur(){
        textDialog.setText("Memeriksa jadwal lembur");
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<PostPutLembur> PKCall = mApiInterface.CheckJadwaLembur("lembur",username);
        PKCall.enqueue(new Callback<PostPutLembur>() {
            @Override
            public void onResponse(Call<PostPutLembur> call, Response<PostPutLembur>
                    response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){

//

                        List<DataLembur> datalembur = response.body().getListLembur();
                        statusLembur = datalembur.get(0).getStatus();
                        if(statusLembur==2){
                            adaLembur =true;
                            tanggalLembur = datalembur.get(0).getTanggal();
                            mulaiLembur = datalembur.get(0).getMulai();
                            selesaiLembur = datalembur.get(0).getSelesai();

                            checkTanggalSekarang();
                        }
                        else{
                            adaLembur=false;
                            btnAbsenLembur.setVisibility(View.GONE);
                            adaLembur=false;
                            //Belum Ada data lembur yang Disetujui
                            initial=true;
                            if(checkInOut==false){
                                loadDataKantor();
                            }
                        }
                    }else{
                        btnAbsenLembur.setVisibility(View.GONE);
                        adaLembur=false;
                        //Belum Ada data lembur
                       initial=true;
                       if(checkInOut==false){
                            loadDataKantor();
                        }

                    }

                }else{
                    mapView.setVisibility(View.GONE);
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada saat memeriksa jadwal lembur dari server");
                    alertDialog.dismiss();
                }


            }

            @Override
            public void onFailure(Call<PostPutLembur> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                mapView.setVisibility(View.GONE);
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText(t.getMessage());
                alertDialog.dismiss();
            }
        });
    }
    private void checkAbsensiLembur(){
        textDialog.setText("Memeriksa status absensi lembur");
        btnAbsenLembur.setEnabled(false);
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.CheckAbsensiLembur("absensi_lembur","",username);
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    statusAbsensiLembur=status;
                    btnAbsenLembur.setEnabled(true);

//                    Toast.makeText(Absensi.this, statusAbsensi, Toast.LENGTH_SHORT).show();
                    if(statusAbsensiLembur.equals("belum absen")){
                        btnAbsenLembur.setText("Absen Lembur");
                    }else if(statusAbsensiLembur.equals("sudah absen")){
                        btnAbsenLembur.setText("Absen Selesai Lembur");
                        List<DataAbsensiPegawai> ambilID = response.body().getListDataAbsensiPegawai();
                        idAbsensiLembur= ambilID.get(0).getId();
//                        Toast.makeText(Absensi.this, "ID Absensi Lembur : "+idAbsensiLembur, Toast.LENGTH_SHORT).show();
                    }else if(statusAbsensiLembur.equals("sudah pulang")){
                        btnAbsenLembur.setText("Sudah Lembur");
                        btnAbsenLembur.setEnabled(false);
                    }


                }
                else {
                    mapView.setVisibility(View.GONE);
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada saat memeriksa status absensi dari server");
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
                mapView.setVisibility(View.GONE);
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText(t.getMessage());
                alertDialog.dismiss();
            }
        });
    }
    private void checkTanggalSekarang(){
        textDialog.setText("Memeriksa tanggal sekarang untuk absen lembur");
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.getCheckWaktu("waktu");
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        String message = response.body().getMessage();
                        tanggalSekarang = ambilTanggal(message);
                        if(tanggalSekarang.equals(tanggalLembur)){
                            adaLembur=true;
                            btnAbsenLembur.setVisibility(View.VISIBLE);
                            checkAbsensiLembur();
                        }
                        else{
                            adaLembur=false;
                            btnAbsenLembur.setVisibility(View.GONE);
                        }
                        initial=true;
                        if(checkInOut==false){
                            loadDataKantor();
                        }

                    }else{

                        mapView.setVisibility(View.GONE);
                        linearMasalahJaringan.setVisibility(View.VISIBLE);
                        textERR.setText("ERR : Gagal memeriksa tanggal sekarang dari server");
                        alertDialog.dismiss();
                    }
                }
                else {
                    mapView.setVisibility(View.GONE);
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada saat memeriksa tanggal sekarang dari server");
                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
                mapView.setVisibility(View.GONE);
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText(t.getMessage());
                alertDialog.dismiss();
            }
        });
    }
    private void checkWaktuLembur(final String check){
        textDialogAbsen.setText("Harap tunggu...");
        Call<GetAbsensiPegawai> callAbsensi = mApiInterface.getCheckWaktu("waktu");
        callAbsensi.enqueue(new Callback<GetAbsensiPegawai>() {
            @Override
            public void onResponse(Call<GetAbsensiPegawai> call, Response<GetAbsensiPegawai> response) {
                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    if(status.equals("berhasil")){
                        String message = response.body().getMessage();

                        if(check.equals("in")){
                            //Check jam mulai lembur
                            if(Integer.valueOf(ambilJam(mulaiLembur,"H:m:s","H"))==Integer.valueOf(ambilJam(message,"y/M/d H:m:s","H"))){
                                //menit
                                if(Integer.valueOf(ambilJam(message,"y/M/d H:m:s","m"))>= Integer.valueOf(ambilJam(mulaiLembur,"H:m:s","m"))){
                                    checkINLembur();
                                }else{
                                    setFailedDialogAbsen("Anda dapat check in lembur pada pukul "+mulaiLembur);
//                                    Toast.makeText(Absensi.this, "Anda dapat check in lembur pada pukul "+mulaiLembur, Toast.LENGTH_SHORT).show();
                                }

                            }else{
                                setFailedDialogAbsen("Anda dapat check in lembur pada pukul "+mulaiLembur);
//                                Toast.makeText(Absensi.this, "Anda dapat check in lembur pada pukul "+mulaiLembur, Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if(check.equals("out")){
                            if(Integer.valueOf(ambilJam(message,"y/M/d H:m:s","H"))==Integer.valueOf(ambilJam(selesaiLembur,"H:m:s","H"))){
                                //menit
                                if(Integer.valueOf(ambilJam(message,"y/M/d H:m:s","m"))>= Integer.valueOf(ambilJam(selesaiLembur,"H:m:s","m"))){
                                    checkOUTLembur();
                                }else{
                                    setFailedDialogAbsen("Anda dapat check out lembur pada pukul "+selesaiLembur);
//                                    Toast.makeText(Absensi.this, "Anda dapat check out lembur pada pukul "+selesaiLembur, Toast.LENGTH_SHORT).show();
                                }


                            }else{
                                setFailedDialogAbsen("Anda dapat check out lembur pada pukul "+selesaiLembur);
//                                Toast.makeText(Absensi.this, "Anda dapat check out lembur pada pukul "+selesaiLembur, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }else{
                        setFailedDialogAbsen("Gagal cek waktu lembur");
//                        Toast.makeText(Absensi.this, "Gagal cek waktu lembur", Toast.LENGTH_SHORT).show();


                    }
                }
                else {
                    setFailedDialogAbsen("Terjadi masalah pada saat memeriksa waktu lembur dari server");
//                    mapView.setVisibility(View.GONE);
//                    linearMasalahJaringan.setVisibility(View.VISIBLE);
//                    textERR.setText("ERR : Terjadi masalah pada saat memeriksa tanggal sekarang dari server");
//                    alertDialog.dismiss();
                }
            }

            @Override
            public void onFailure(Call<GetAbsensiPegawai> call, Throwable t) {
                mapView.setVisibility(View.GONE);
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText(t.getMessage());
                alertDialog.dismiss();
            }
        });
    }

    private String ambilTanggal(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("y/M/d H:m:s");

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



}
