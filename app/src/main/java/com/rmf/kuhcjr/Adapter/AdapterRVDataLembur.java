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

import com.rmf.kuhcjr.Data.DataLembur;
import com.rmf.kuhcjr.Utils.DateUtils;
import com.rmf.kuhcjr.Pengajuan.DetailPengajuan;
import com.rmf.kuhcjr.Pengajuan.PengajuanLembur;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.Utils.StatusUtils;

import java.util.List;

public class AdapterRVDataLembur extends RecyclerView.Adapter<AdapterRVDataLembur.Lembur> {
    List<DataLembur> list;
    PengajuanLembur pl;

    public AdapterRVDataLembur(List<DataLembur> list,PengajuanLembur pl) {
        this.list = list;
        this.pl = pl;
    }

    @NonNull
    @Override
    public Lembur onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_pengajuan_lembur, viewGroup,false);

        return new Lembur(view);
    }


    @Override
    public void onBindViewHolder(@NonNull Lembur lembur, int i) {
        final int id = list.get(i).getId();
        final String tanggal = DateUtils.getDayName(list.get(i).getTanggal());
        final int status = list.get(i).getStatus();
        final String mulai =  DateUtils.getTime(list.get(i).getMulai());
        final String selesai = DateUtils.getTime(list.get(i).getSelesai());
        final String uraian = list.get(i).getUraian();
        final String alasan = list.get(i).getKeterangan();
        final String filePengajuan = list.get(i).getFile_pengajuan();
        final String suratTugas = list.get(i).getFile();




        lembur.nomor.setText(String.valueOf(i+1));
        lembur.tgl.setText(tanggal);
        lembur.uraian.setText(uraian);

        lembur.status.setTextColor(Color.parseColor(StatusUtils.checkWarnaFont(status)));
        lembur.status.setBackgroundColor(Color.parseColor(StatusUtils.checkStatusWarna(status)));
        lembur.status.setText(StatusUtils.checkStatus(status));

        lembur.waktuMulai.setText(mulai);
        lembur.wakatuSelesai.setText(selesai);


        lembur.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                pl.pindahHalaman=true;

                Intent intent = new Intent(v.getContext(),DetailPengajuan.class);
                intent.putExtra("kategori","lembur");
                intent.putExtra("tanggal",tanggal);
                intent.putExtra("status",StatusUtils.checkStatus(status));
                intent.putExtra("mulai",mulai);
                intent.putExtra("selesai",selesai);
                intent.putExtra("uraian",uraian);
                intent.putExtra("alasan",alasan);
                intent.putExtra("file",filePengajuan);
                intent.putExtra("tugas",suratTugas);
                intent.putExtra("id",id);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Lembur extends RecyclerView.ViewHolder {

        TextView tgl,uraian,status,nomor,waktuMulai,wakatuSelesai;
        CardView cardView;

        public Lembur(@NonNull View itemView) {
            super(itemView);
            nomor = (TextView) itemView.findViewById(R.id.nomor);
            tgl = (TextView) itemView.findViewById(R.id.tgl_lembur);
            waktuMulai = (TextView) itemView.findViewById(R.id.waktu_mulai);
            wakatuSelesai = (TextView) itemView.findViewById(R.id.waktu_selesai);

            uraian= (TextView) itemView.findViewById(R.id.uraian);

            status = (TextView) itemView.findViewById(R.id.status);
            cardView = (CardView) itemView.findViewById(R.id.cardview);


        }
    }



}
