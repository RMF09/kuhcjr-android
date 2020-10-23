package com.rmf.kuhcjr.Fragments;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.rmf.kuhcjr.Adapter.AdapterRVDataKegiatan;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.CetakPreview;
import com.rmf.kuhcjr.Data.DataKegiatan;
import com.rmf.kuhcjr.Data.GetCetak;
import com.rmf.kuhcjr.Data.GetKegiatan;
import com.rmf.kuhcjr.Data.PostPutCuti;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.TambahDataPengajuan.TambahDataPengajuanCuti;
import com.rmf.kuhcjr.TambahKegiatan;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Laporan extends Fragment {


    private EditText editDariTanggal, editSampaiTanggal;
    private FloatingActionButton btnCetak;
    private ApiInterface mApiInterface;

    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_laporan,container,false);
        this.view =view;
        initialUI(view);


        return view;
    }

    private void initialUI(View view){

        editDariTanggal = (EditText) view.findViewById(R.id.cetak_dari_tanggal);
        editSampaiTanggal = (EditText) view.findViewById(R.id.cetak_sampai_tanggal);

        btnCetak = (FloatingActionButton) view.findViewById(R.id.btn_cetak_laporan);

        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        this.actionUI(view);
    }

    private void actionUI(final View v) {
       editDariTanggal.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Calendar calendar = Calendar.getInstance();
               int year = calendar.get(Calendar.YEAR);
               int month = calendar.get(Calendar.MONTH);
               final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

               DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                       new DatePickerDialog.OnDateSetListener() {
                           @Override
                           public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                               editDariTanggal.setText(day +"/"+(month+1)+"/"+year);
                           }
                       }, year, month, dayOfMonth);
               datePickerDialog.show();
           }
       });

        editSampaiTanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(v.getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                editSampaiTanggal.setText(day +"/"+(month+1)+"/"+year);
                            }
                        }, year, month, dayOfMonth);

                datePickerDialog.show();
            }
        });

        btnCetak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkInput(v)){

                   Toast.makeText(v.getContext(), "Mohon tunggu...", Toast.LENGTH_LONG).show();
                   cetakLaporan();
               }
            }
        });

    }

    private boolean checkInput(View v){

        boolean cek = false;

        if(TextUtils.isEmpty(editDariTanggal.getText().toString())){
            Snackbar.make(v,"Harap isi dari tanggal",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editSampaiTanggal.getText().toString())){
            Snackbar.make(v,"Harap isi sampai tanggal",Snackbar.LENGTH_SHORT).show();
        }

        else{
            cek = true;
        }
        return cek;
    }

    private void cetakLaporan(){
        String daritgl = ubahFormatTanggalKeSQL(editDariTanggal.getText().toString());
        String sampaitgl = ubahFormatTanggalKeSQL(editSampaiTanggal.getText().toString());

        Call<GetCetak> cetakCall = mApiInterface.cetakLaporan("pegawai",daritgl,sampaitgl);
        cetakCall.enqueue(new Callback<GetCetak>() {
            @Override
            public void onResponse(Call<GetCetak> call, Response<GetCetak>
                    response) {


                if(response.isSuccessful()){

                    String status = response.body().getStatus();
                    String message = response.body().getMessage();
                    String data = response.body().getIsiHTML();
                    if(status.equals("gagal")){
//                    failure(message);
                        Toast.makeText(view.getContext(), message, Toast.LENGTH_LONG).show();
                    }
                    else{
//                    success();
                        Intent intent = new Intent(view.getContext(),CetakPreview.class);
                        intent.putExtra("data",data);
                        startActivity(intent);
                    }
                }
                else{
                    Toast.makeText(view.getContext(), "Terjadi masalah pada DB server", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<GetCetak> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(view.getContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
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




}
