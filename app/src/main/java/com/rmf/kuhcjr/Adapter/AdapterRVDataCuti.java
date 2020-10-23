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
import com.rmf.kuhcjr.Data.DataLembur;
import com.rmf.kuhcjr.Pengajuan.DetailPengajuan;
import com.rmf.kuhcjr.Pengajuan.PengajuanCuti;
import com.rmf.kuhcjr.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_pengajuan_cuti2, viewGroup,false);

        return new Cuti(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Cuti cuti, int i) {
        final int nomor = 1+i;
        final int id = list.get(i).getId();
        final String tanggal =  getDayName(list.get(i).getTanggal());
        final String tanggalMulaiCuti =  list.get(i).getMulai();
        final String tanggalSelesaiCuti =  list.get(i).getSelesai();
        final int status = list.get(i).getStatus();
        final String uraian = list.get(i).getUraian();
        final String alasan = list.get(i).getKeterangan();
        final String namafile = list.get(i).getFile_pengajuan();
        final String suratTugas = list.get(i).getFile();


        cuti.status.setTextColor(Color.parseColor(checkWarnaFont(status)));

        cuti.nomor.setText(String.valueOf(nomor));
        cuti.tgl.setText(tanggal);
        cuti.tglMulaiCuti.setText(getTime(tanggalMulaiCuti));
        cuti.tglSelesaiCuti.setText(getTime(tanggalSelesaiCuti));
        cuti.status.setText(checkStatus(status));
        cuti.uraian.setText(uraian);

        cuti.status.setBackgroundColor(Color.parseColor(checkStatusWarna(status)));

        cuti.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pc.pindahHalaman=true;
                Intent intent = new Intent(v.getContext(),DetailPengajuan.class);
                intent.putExtra("kategori","cuti");
                intent.putExtra("tanggal",tanggal);
                intent.putExtra("status",checkStatus(status));
                intent.putExtra("mulai",getDayName(tanggalMulaiCuti));
                intent.putExtra("selesai",getDayName(tanggalSelesaiCuti));
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
    private String checkStatus(int status){
        String hasil="";

        switch (status){

            case 1: hasil = "Diproses";
                break;
            case 2: hasil = "Disetujui";
                break;
            case 3: hasil = "Ditolak";
                break;
            default: hasil = "Pending";
                break;
        }
        return hasil;
    }

    private String checkWarnaFont(int status){
        String hasil="#383838";

        switch (status){
            case 0: hasil = "#383838";
                break;
            default: hasil ="#FFFFFF";
                break;
        }
        return hasil;
    }

    private String checkStatusWarna(int status){
        String hasil="#ffc107";

        switch (status){
            case 1: hasil = "#17a2b8";
                break;
            case 2: hasil = "#28a745";
                break;
            case 3: hasil = "#dc3545";
                break;
            default: hasil ="#ffc107";
                break;
        }
        return hasil;
    }
    private String getDayName(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("EEEE, dd MMMM yyyy");
        String akhir = output.format(date);

        return akhir;

    }
    private String getTime(String time){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date =null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
        String akhir = output.format(date);

        return akhir;

    }
}
