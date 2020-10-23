package com.rmf.kuhcjr.Fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.rmf.kuhcjr.Adapter.AdapterRVDataCuti;
import com.rmf.kuhcjr.Adapter.AdapterRVDataKegiatan;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataCuti;
import com.rmf.kuhcjr.Data.DataKegiatan;
import com.rmf.kuhcjr.Data.GetCuti;
import com.rmf.kuhcjr.Data.GetKegiatan;
import com.rmf.kuhcjr.PeminjamanMobil;
import com.rmf.kuhcjr.Pengajuan.PengajuanCuti;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.SharedPrefs;
import com.rmf.kuhcjr.TambahKegiatan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class Kegiatan extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private RecyclerView rv;
    private List<DataKegiatan> list = new ArrayList<>();
    private AdapterRVDataKegiatan adapterRVDataKegiatan;
    private ApiInterface mApiInterface;

    //Masalah Jaringan Layout
    private LinearLayout linearMasalahJaringan;
    private TextView textERR,textMuatUlang;

    //Belum Ada data
    private LinearLayout linearBelumAdaData;
    private TextView textBelumAdaData;

    private View view;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_kegiatan,container,false);
        this.view =view;
        initialUI(view);
        loadData(view);

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
                Intent intent = new Intent(v.getContext(), TambahKegiatan.class);
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

        Call<GetKegiatan> KegiatanCall = mApiInterface.getKegiatan("daftar",username);
        KegiatanCall.enqueue(new Callback<GetKegiatan>() {
            @Override
            public void onResponse(Call<GetKegiatan> call, Response<GetKegiatan>
                    response) {

                progressBar.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    String status = response.body().getStatus();

                    if(status.equals("berhasil")){
                        List<DataKegiatan> KegiatanList = response.body().getListDataKegiatan();
//                Collections.reverse(KegiatanList);

                        adapterRVDataKegiatan = new AdapterRVDataKegiatan(KegiatanList);
//                        adapterRVDataKegiatan.notifyDataSetChanged();
//                        adapterRVDataKegiatan.setHasStableIds(true);
                        rv.setAdapter(adapterRVDataKegiatan);
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
            public void onFailure(Call<GetKegiatan> call, Throwable t) {
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
