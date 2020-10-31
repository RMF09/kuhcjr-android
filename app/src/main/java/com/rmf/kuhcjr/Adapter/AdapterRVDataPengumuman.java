package com.rmf.kuhcjr.Adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rmf.kuhcjr.Data.DataPengumuman;
import com.rmf.kuhcjr.Fragments.BerandaFragment;
import com.rmf.kuhcjr.Pengumuman;
import com.rmf.kuhcjr.R;

import java.util.List;

public class AdapterRVDataPengumuman extends RecyclerView.Adapter<AdapterRVDataPengumuman.Pengumuman> {

    List<DataPengumuman> list;
    com.rmf.kuhcjr.Pengumuman pengumumanActivity;

    public AdapterRVDataPengumuman(List<DataPengumuman> list, com.rmf.kuhcjr.Pengumuman pengumumanActivity) {
        this.list = list;
        this.pengumumanActivity = pengumumanActivity;
    }

    @NonNull
    @Override
    public Pengumuman onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_notif,viewGroup,false);
        return new Pengumuman(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Pengumuman pengumuman, int i) {

        final String file = list.get(i).getFile();
        pengumuman.textIsi.setText(list.get(i).getIsi());
//        Log.e("AdapterPengumuman",file);
        if(file==null){
            pengumuman.linearLayoutLampiranFile.setVisibility(View.GONE);
        }

        pengumuman.linearLayoutLampiranFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pengumumanActivity.setNamafile(file);
                pengumumanActivity.downloadZipFile(pengumumanActivity.getNamafile());
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Pengumuman extends RecyclerView.ViewHolder {
        TextView textIsi;
        LinearLayout linearLayoutLampiranFile;

        public Pengumuman(@NonNull View itemView) {
            super(itemView);
            textIsi = itemView.findViewById(R.id.isi_pengumuman);
            linearLayoutLampiranFile = itemView.findViewById(R.id.linear_layout_lampiran_file);
        }
    }
}
