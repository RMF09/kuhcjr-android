package com.rmf.kuhcjr.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.rmf.kuhcjr.Adapter.AdapterRVDataTugas;
import com.rmf.kuhcjr.Adapter.AdapterRVDataTugasTambahan;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataTugas;
import com.rmf.kuhcjr.Data.GetTugas;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.SharedPrefs;
import com.rmf.kuhcjr.TambahKegiatanJabatan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TugasTambahan extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private RecyclerView rv;
    private List<DataTugas> list = new ArrayList<>();
    private AdapterRVDataTugasTambahan adapterRVDataTugas;
    private ApiInterface mApiInterface;

    //Masalah Jaringan Layout
    private LinearLayout linearMasalahJaringan;
    private TextView textERR,textMuatUlang;

    //Belum Ada data
    private LinearLayout linearBelumAdaData;
    private TextView textBelumAdaData;

    private View view;

    public int tahun_id;
    public String dariActivity="tugas_tambahan";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_tugas_pokok,container,false);
        this.view =view;
        tahun_id = getActivity().getIntent().getIntExtra("tahun_id",0);

        initialUI(view);
//        loadData(view);

        return view;
    }

    private void initialUI(final View view){


        fab = (FloatingActionButton) view.findViewById(R.id.btn_tambah_kegiatan);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh);
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        rv = (RecyclerView) view.findViewById(R.id.rv);
        RecyclerView.LayoutManager lm = new LinearLayoutManager(view.getContext(),LinearLayoutManager.VERTICAL,false);
        rv.setLayoutManager(lm);
        rv.setHasFixedSize(true);
        rv.setItemViewCacheSize(20);


        //Masalah Jaringan
        linearMasalahJaringan = (LinearLayout) view.findViewById(R.id.layout_masalah_jaringan);
        textERR = (TextView) view.findViewById(R.id.text_error_code);
        textMuatUlang = (TextView) view.findViewById(R.id.text_muat_ulang_data);

        textMuatUlang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData(view);
            }
        });


        //Belum Ada Data
        linearBelumAdaData= (LinearLayout) view.findViewById(R.id.layout_belum_ada_data);
        textBelumAdaData = (TextView) view.findViewById(R.id.text_belum_ada_data);



        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        this.actionUI(view);
    }

    private void actionUI(final View v) {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                loadData(v);
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), TambahKegiatanJabatan.class);
                intent.putExtra("menu","tugas_tambahan");
                intent.putExtra("tahun_id",tahun_id);
                v.getContext().startActivity(intent);
            }
        });


    }


    @SuppressLint("RestrictedApi")
    private void loadData(final View view){

        fab.setVisibility(View.GONE);
        linearMasalahJaringan.setVisibility(View.GONE);
        linearBelumAdaData.setVisibility(View.GONE);

        progressBar.setVisibility(View.VISIBLE);
        rv.setVisibility(View.GONE);

        String username = SharedPrefs.getInstance(view.getContext()).LoggedInUser();

        Call<GetTugas> TugasPokokCall = mApiInterface.getTugas("ambilTugasTambahan",username,tahun_id);
        TugasPokokCall.enqueue(new Callback<GetTugas>() {
            @Override
            public void onResponse(Call<GetTugas> call, Response<GetTugas>
                    response) {

                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    String status = response.body().getStatus();

                    if(status.equals("berhasil")){
                         list = response.body().getListDataTugas();
//                Collections.reverse(KegiatanList);

                        adapterRVDataTugas = new AdapterRVDataTugasTambahan(list, TugasTambahan.this);

                        rv.setAdapter(adapterRVDataTugas);
                        rv.setVisibility(View.VISIBLE);
                        fab.setVisibility(View.VISIBLE);

                        LayoutAnimationController layoutAnimationController = AnimationUtils.loadLayoutAnimation(getContext(),R.anim.layout_animation);
                        rv.setLayoutAnimation(layoutAnimationController);
                        rv.getAdapter().notifyDataSetChanged();
                        rv.scheduleLayoutAnimation();

                    }else{
                        linearBelumAdaData.setVisibility(View.VISIBLE);
                        textBelumAdaData.setText("Belum ada data kegiatan, Tap tombol + untuk kegiatan baru");
                        fab.setVisibility(View.VISIBLE);
                    }

                }else{
                  //not successful
                    linearMasalahJaringan.setVisibility(View.VISIBLE);
                    textERR.setText("ERR : Terjadi masalah pada DB server");
                    progressBar.setVisibility(View.GONE);
                }

            }

            @Override
            public void onFailure(Call<GetTugas> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                linearMasalahJaringan.setVisibility(View.VISIBLE);
                textERR.setText("ERR : "+t.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        loadData(view);
    }
}
