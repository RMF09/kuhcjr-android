package com.rmf.kuhcjr.Fragments;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.CetakPreview;
import com.rmf.kuhcjr.Data.DataUserLogin;
import com.rmf.kuhcjr.Data.GetCetak;
import com.rmf.kuhcjr.Data.GetUserLogin;
import com.rmf.kuhcjr.EditProfile;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.SharedPrefs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProfileFragment extends Fragment {


    private ImageView imageProfile;
    private ApiInterface mApiInterface;

    private View view;
    private AlertDialog alertDialog;
    private FloatingActionButton fab;

    private TextView textInstansi,textNIK,textSatuanKerja,textJabatan,textNama;
    LinearLayout linearLogOut;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profil,container,false);
        this.view =view;
        initialUI(view);
        initialDialog();

        return view;
    }

    private void initialUI(View view){

        fab = (FloatingActionButton) view.findViewById(R.id.btn_logout);
        imageProfile = (ImageView) view.findViewById(R.id.image_profile);

        textNama= (TextView) view.findViewById(R.id.text_nama_profile);
        textInstansi = (TextView) view.findViewById(R.id.text_instansi);
        textNIK= (TextView) view.findViewById(R.id.text_nik);
        textSatuanKerja= (TextView) view.findViewById(R.id.text_satuan_kerja);
        textJabatan = (TextView) view.findViewById(R.id.text_jabatan);
        linearLogOut = view.findViewById(R.id.linear_logout);



        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        this.actionUI(view);
    }

    private void initialDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                getContext());

        // set title dialog
        alertDialogBuilder.setTitle("Logout")
                .setMessage("Anda yakin ingin keluar?");
        // set pesan dari dialog
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        SharedPrefs.getInstance(getContext()).logout();
                        getActivity().finish();
                    }
                })
                .setNegativeButton("Tidak",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        // jika tombol diklik, maka akan menutup activity ini
                        dialog.cancel();
                    }
                });


        // membuat alert dialog dari builder
        alertDialog = alertDialogBuilder.create();

        // menampilkan alert dialog

    }

    private void actionUI(final View view) {
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                alertDialog.show();
                startActivity(new Intent(view.getContext(), EditProfile.class));
            }
        });

        linearLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });
    }



    private void loadDataProfile(){

        hideData();

        String username = SharedPrefs.getInstance(view.getContext()).LoggedInUser();
        String metode = "ambilData";

        Call<GetUserLogin> getUserLoginCall = mApiInterface.getDataProfile(username,metode);
        getUserLoginCall.enqueue(new Callback<GetUserLogin>() {
            @Override
            public void onResponse(Call<GetUserLogin> call, Response<GetUserLogin>
                    response) {
                String status = response.body().getStatus();
                String message = response.body().getMessage();
                List<DataUserLogin> listDataProfile = response.body().getData();
                if(status.equals("berhasil")){

                    Glide.with(view)
                    .load(ApiClient.BASE_URL+"assets/files/kinerja/identitas/"+listDataProfile.get(0).getFile())
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .apply(RequestOptions.circleCropTransform()).into(imageProfile);

                    aturData(listDataProfile.get(0).getInstansi(),
                            listDataProfile.get(0).getNip(),
                            listDataProfile.get(0).getSatuan_kerja(),
                            listDataProfile.get(0).getJabatan(),listDataProfile.get(0).getNama());

                }
            }


            @Override
            public void onFailure(Call<GetUserLogin> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void aturData(String instansi,String nik,String satuanKerja,String jabatan,String nama ){
        textInstansi.setText(instansi);
        textNIK.setText(nik);
        textSatuanKerja.setText(satuanKerja);
        textJabatan.setText(jabatan);
        textNama.setText(nama);

        //Visiblity
        textNama.setVisibility(View.VISIBLE);
        textInstansi.setVisibility(View.VISIBLE);
        textNIK.setVisibility(View.VISIBLE);
        textSatuanKerja.setVisibility(View.VISIBLE);
        textJabatan.setVisibility(View.VISIBLE);

    }

    private void hideData(){
        textNama.setVisibility(View.INVISIBLE);
        textInstansi.setVisibility(View.INVISIBLE);
        textNIK.setVisibility(View.INVISIBLE);
        textSatuanKerja.setVisibility(View.INVISIBLE);
        textJabatan.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadDataProfile();
    }
}
