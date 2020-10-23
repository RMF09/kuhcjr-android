package com.rmf.kuhcjr;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.GetSKP;
import com.rmf.kuhcjr.Fragments.Kegiatan;
import com.rmf.kuhcjr.Fragments.Laporan;
import com.rmf.kuhcjr.Fragments.TugasPokok;
import com.rmf.kuhcjr.Fragments.TugasTambahan;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class IsiPenilaian extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TextView textHeader;
    private FloatingActionButton fab;
    private String tahun;
    private int tahun_id;

    private AlertDialog alertDialog;
    private ApiInterface apiInterface;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_isi_penilaian);
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
        tahun = getIntent().getStringExtra("tahun");
        tahun_id = getIntent().getIntExtra("tahun_id",0);

        this.initialUI();
        this.initialDialog();
    }
    private void initialUI(){
        textHeader = findViewById(R.id.text_header);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);

        textHeader.setText("Pengisian SKP Tahun "+tahun);
        fab = findViewById(R.id.btn_pengukuran);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.show();
            }
        });

    }
    private void initialDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set title dialog
        alertDialogBuilder.setTitle("Proses Realisasi Pengukuran")
                .setMessage("Apakah anda yakin akan melakukan proses penilaian realisasi SKP?");
        // set pesan dari dialog
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Ya",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog,int id) {
                        prosesPengukuran();
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

    }

    private void prosesPengukuran(){
        String username = SharedPrefs.getInstance(this).LoggedInUser();
        Call<GetSKP>  prosesPengukuranCall= apiInterface.prosesPengukuran("prosesPengukuran",username,tahun_id);
        prosesPengukuranCall.enqueue(new Callback<GetSKP>() {
            @Override
            public void onResponse(Call<GetSKP> call, Response<GetSKP> response) {
                if(response.isSuccessful()){
                    if(response.body().getStatus().equals("berhasil")){
                        Toast.makeText(IsiPenilaian.this, "Berhasil memproses pengukuran", Toast.LENGTH_SHORT).show();
                        finish();
                    }else{
                        Toast.makeText(IsiPenilaian.this, "Gagal memproses pengukuran", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(IsiPenilaian.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetSKP> call, Throwable t) {
                Toast.makeText(IsiPenilaian.this, "ERR : "+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void setupViewPager(ViewPager viewPager) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new TugasPokok(), "Tugas Pokok");
        adapter.addFragment(new TugasTambahan(), "Tugas Tambahan");

        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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