package com.rmf.kuhcjr.Adapter;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmf.kuhcjr.Data.DataCuti;
import com.rmf.kuhcjr.Data.DataSKPTahunPegawai;
import com.rmf.kuhcjr.DetailPengisianSkp;
import com.rmf.kuhcjr.Pengajuan.DetailPengajuan;
import com.rmf.kuhcjr.Pengajuan.PengajuanCuti;
import com.rmf.kuhcjr.PengisianSkp;
import com.rmf.kuhcjr.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterRVDataSKPPegawai extends RecyclerView.Adapter<AdapterRVDataSKPPegawai.SKP> {
    List<DataSKPTahunPegawai> list;
    PengisianSkp p;

    public AdapterRVDataSKPPegawai(List<DataSKPTahunPegawai> list,PengisianSkp p) {
        this.list = list;
        this.p= p;

    }

    @NonNull
    @Override
    public SKP onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_pengisian_skp, viewGroup,false);

        return new SKP(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final SKP skp, int i) {
        final int nomor = 1+i;
        final int id = list.get(i).getId();
        final String tahun =  "Tahun Penilaian " +list.get(i).getTahun();
        final String thn = list.get(i).getTahun();
        final int tahun_id = list.get(i).getTahun_id();
        final int status = list.get(i).getStatus();
        final String perubahanTerakhir = getDayName(list.get(i).getUpdated_at());


        skp.nomor.setText(String.valueOf(nomor));
        skp.status.setTextColor(Color.parseColor(checkWarnaFont(status)));

        skp.tahun.setText(tahun);
        skp.perubahan_terakhir.setText(perubahanTerakhir);
        skp.status.setText(checkStatus(status));

        skp.status.setBackgroundColor(Color.parseColor(checkStatusWarna(status)));

        skp.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              p.pindahHalaman=true;
                Intent intent = new Intent(v.getContext(), DetailPengisianSkp.class);
                intent.putExtra("tahun",thn);
                intent.putExtra("tahun_id",tahun_id);
                intent.putExtra("id",id);
                intent.putExtra("pt",perubahanTerakhir);
                intent.putExtra("status",status);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class SKP extends RecyclerView.ViewHolder {

        TextView tahun,perubahan_terakhir,status,nomor;
        CardView cardView;
        public SKP(@NonNull View itemView) {
            super(itemView);

            nomor = (TextView) itemView.findViewById(R.id.nomor);
            tahun = (TextView) itemView.findViewById(R.id.tahun);
            perubahan_terakhir = (TextView) itemView.findViewById(R.id.perubahan_terakhir);

            status = (TextView) itemView.findViewById(R.id.status);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
    private String checkStatus(int status){
        String hasil="";

        switch (status){

            case 1: hasil = "proses pengukuran";
                break;
            case 2: hasil = "pengajuan proses penilaian";
                break;
            case 3: hasil = "proses penilaian";
                break;
            case 4: hasil = "selesai";
                break;
            default: hasil = "draft";
                break;
        }
        return hasil;
    }

    private String checkWarnaFont(int status){
        String hasil="#FFFFFF";

        switch (status){
            case 1: hasil = "#1f2d3d";
                break;
            case 2: hasil = "#1f2d3d";
                break;
            default: hasil ="#FFFFFF";
                break;
        }
        return hasil;
    }

    private String checkStatusWarna(int status){
        String hasil="#ffc107";

        switch (status){
            case 0: hasil ="#007bff";
                break;
            case 3: hasil = "#28a745";
                break;
            case 4: hasil = "#28a745";
                break;
            default: hasil ="#ffc107";
                break;
        }
        return hasil;
    }
    private String getDayName(String tgl){
        if(tgl==null){
            return "Belum Mengisi";
        }else{

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date =null;
            try {
                date = simpleDateFormat.parse(tgl);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            SimpleDateFormat output = new SimpleDateFormat("EEEE, dd MMMM yyyy HH:mm:ss");
            String akhir = output.format(date);
            akhir += " WAS";

            return akhir;
        }

    }
}
