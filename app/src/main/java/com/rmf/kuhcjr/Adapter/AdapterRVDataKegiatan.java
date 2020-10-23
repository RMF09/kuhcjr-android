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
import com.rmf.kuhcjr.Data.DataKegiatan;
import com.rmf.kuhcjr.DetailKegiatan;
import com.rmf.kuhcjr.Pengajuan.DetailPengajuan;
import com.rmf.kuhcjr.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterRVDataKegiatan extends RecyclerView.Adapter<AdapterRVDataKegiatan.Kegiatan> {
    List<DataKegiatan> list;

    public AdapterRVDataKegiatan(List<DataKegiatan> list) {
        this.list = list;
    }

    @NonNull
    @Override
    public Kegiatan onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_kegiatan2, viewGroup,false);

        return new Kegiatan(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Kegiatan kegiatans, int i) {
        final int id = list.get(i).getId();
        final String tanggal =  getDayName(list.get(i).getTanggal());
        final String kegiatan =  list.get(i).getKegiatan();
        final String hasil =  list.get(i).getHasil();
        final String jumlah = list.get(i).getJumlah();
        final String satuan = list.get(i).getSatuan();
        final String keterangan = list.get(i).getKeterangan();

        kegiatans.nomor.setText(String.valueOf(i+1));
        kegiatans.tgl.setText(tanggal);
        kegiatans.kegiatan.setText(kegiatan);
        kegiatans.hasil.setText(hasil);
        kegiatans.jumlah.setText(jumlah);
        kegiatans.satuan.setText(satuan);

        kegiatans.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailKegiatan.class);
                intent.putExtra("tgl",tanggal);
                intent.putExtra("id",id);
                intent.putExtra("kegiatan",kegiatan);
                intent.putExtra("hasil",hasil);
                intent.putExtra("jumlah",jumlah);
                intent.putExtra("satuan",satuan);
                intent.putExtra("keterangan",keterangan);

                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Kegiatan extends RecyclerView.ViewHolder {

        TextView nomor,tgl,kegiatan,hasil,jumlah,satuan;
        CardView cardView;
        public Kegiatan(@NonNull View itemView) {
            super(itemView);

            nomor = (TextView) itemView.findViewById(R.id.nomor);
            tgl = (TextView) itemView.findViewById(R.id.tgl);
            kegiatan = (TextView) itemView.findViewById(R.id.kegiatan);
            hasil = (TextView) itemView.findViewById(R.id.hasil);
            jumlah = (TextView) itemView.findViewById(R.id.jumlah);
            satuan = (TextView) itemView.findViewById(R.id.satuan);

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
}
