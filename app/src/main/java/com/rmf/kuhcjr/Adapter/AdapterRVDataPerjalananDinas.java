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
import com.rmf.kuhcjr.Data.DataPerjalananDinas;
import com.rmf.kuhcjr.Pengajuan.DetailPengajuan;
import com.rmf.kuhcjr.Pengajuan.PengajuanDinas;
import com.rmf.kuhcjr.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterRVDataPerjalananDinas extends RecyclerView.Adapter<AdapterRVDataPerjalananDinas.Dinas> {
    List<DataPerjalananDinas> list;
    PengajuanDinas pd;
    public AdapterRVDataPerjalananDinas(List<DataPerjalananDinas> list, PengajuanDinas pd) {
        this.list = list;
        this.pd = pd;
    }

    @NonNull
    @Override
    public Dinas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_pengajuan_perjalanan_dinas_2, viewGroup,false);

        return new Dinas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Dinas dinas, int i) {
        final int nomor = i+1;
        final int id = list.get(i).getId();
        final String tanggal =  getDayName(list.get(i).getTanggal());
        final int status = list.get(i).getStatus();
        final String tujuan = list.get(i).getTujuan();
        final String mulai = getTime(list.get(i).getMulai());
        final String selesai = getTime(list.get(i).getSelesai());
        final String uraian = list.get(i).getUraian();
        final String alasan = list.get(i).getKeterangan();
        final String namafile = list.get(i).getFile_pengajuan();
        final String suratTugas = list.get(i).getFile();


        dinas.nomor.setText(String.valueOf(nomor));
        dinas.tgl.setText( tanggal);
        dinas.uraian.setText(uraian);
        dinas.tujuan.setText(tujuan);
        dinas.status.setText(checkStatus(status));
        dinas.waktuMulai.setText(mulai);
        dinas.waktuSelesai.setText(selesai);

        dinas.status.setTextColor(Color.parseColor(checkWarnaFont(status)));
        dinas.status.setBackgroundColor(Color.parseColor(checkStatusWarna(status)));

        dinas.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pd.pindahHalaman=true;
                Intent intent = new Intent(v.getContext(),DetailPengajuan.class);
                intent.putExtra("kategori","dinas");
                intent.putExtra("tanggal",tanggal);
                intent.putExtra("status",checkStatus(status));
                intent.putExtra("mulai",mulai);
                intent.putExtra("selesai",selesai);
                intent.putExtra("uraian",uraian);
                intent.putExtra("alasan",alasan);
                intent.putExtra("tujuan",tujuan);
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

    public class Dinas extends RecyclerView.ViewHolder {

        TextView nomor,tgl,tujuan,status,uraian,waktuMulai,waktuSelesai;
        CardView cardView;

        public Dinas(@NonNull View itemView) {
            super(itemView);

            nomor = (TextView) itemView.findViewById(R.id.nomor);
            tgl = (TextView) itemView.findViewById(R.id.tgl_perjalanan_dinas);
            uraian = (TextView) itemView.findViewById(R.id.uraian);
            tujuan = (TextView) itemView.findViewById(R.id.tujuan);
            status = (TextView) itemView.findViewById(R.id.status);
            waktuMulai = (TextView) itemView.findViewById(R.id.waktu_mulai);
            waktuSelesai = (TextView) itemView.findViewById(R.id.waktu_selesai);
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
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date =null;
        try {
            date = simpleDateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("HH:mm");
        String akhir = output.format(date);

        return akhir;

    }
}
