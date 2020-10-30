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
import com.rmf.kuhcjr.Utils.DateUtils;
import com.rmf.kuhcjr.Pengajuan.DetailPengajuan;
import com.rmf.kuhcjr.Pengajuan.PengajuanCuti;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.Utils.StatusUtils;

import java.util.List;

public class AdapterRVDataCuti extends RecyclerView.Adapter<AdapterRVDataCuti.Cuti> {
    List<DataCuti> list;
    PengajuanCuti pc;
    public AdapterRVDataCuti(List<DataCuti> list, PengajuanCuti pc) {
        this.list = list;
        this.pc = pc;
    }

    @NonNull
    @Override
    public Cuti onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_pengajuan_cuti, viewGroup,false);

        return new Cuti(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Cuti cuti, int i) {
        final int nomor = 1+i;
        final int id = list.get(i).getId();
        final String tanggal = DateUtils.getDayName(list.get(i).getTanggal());
        final String tanggalMulaiCuti =  list.get(i).getMulai();
        final String tanggalSelesaiCuti =  list.get(i).getSelesai();
        final int status = list.get(i).getStatus();
        final String uraian = list.get(i).getUraian();
        final String alasan = list.get(i).getKeterangan();
        final String namafile = list.get(i).getFile_pengajuan();
        final String suratTugas = list.get(i).getFile();


        cuti.status.setTextColor(Color.parseColor(StatusUtils.checkWarnaFont(status)));

        cuti.nomor.setText(String.valueOf(nomor));
        cuti.tgl.setText(tanggal);
        cuti.tglMulaiCuti.setText(DateUtils.getTime(tanggalMulaiCuti));
        cuti.tglSelesaiCuti.setText(DateUtils.getTime(tanggalSelesaiCuti));
        cuti.status.setText(StatusUtils.checkStatus(status));
        cuti.uraian.setText(uraian);

        cuti.status.setBackgroundColor(Color.parseColor(StatusUtils.checkStatusWarna(status)));

        cuti.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pc.pindahHalaman=true;
                Intent intent = new Intent(v.getContext(),DetailPengajuan.class);
                intent.putExtra("kategori","cuti");
                intent.putExtra("tanggal",tanggal);
                intent.putExtra("status", StatusUtils.checkStatus(status));
                intent.putExtra("mulai",DateUtils.getDayName(tanggalMulaiCuti));
                intent.putExtra("selesai",DateUtils.getDayName(tanggalSelesaiCuti));
                intent.putExtra("uraian",uraian);
                intent.putExtra("alasan",alasan);
                intent.putExtra("id",id);
                intent.putExtra("file",namafile);
                intent.putExtra("tugas",suratTugas);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Cuti extends RecyclerView.ViewHolder {

        TextView tgl,tglMulaiCuti,tglSelesaiCuti,status,uraian,nomor;
        CardView cardView;
        public Cuti(@NonNull View itemView) {
            super(itemView);

            nomor = (TextView) itemView.findViewById(R.id.nomor);
            uraian = (TextView) itemView.findViewById(R.id.uraian);
            tgl = (TextView) itemView.findViewById(R.id.tgl_cuti);
            tglMulaiCuti = (TextView) itemView.findViewById(R.id.tgl_mulai_cuti);
            tglSelesaiCuti = (TextView) itemView.findViewById(R.id.tgl_selesai_cuti);
            status = (TextView) itemView.findViewById(R.id.status);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }
    }


}
