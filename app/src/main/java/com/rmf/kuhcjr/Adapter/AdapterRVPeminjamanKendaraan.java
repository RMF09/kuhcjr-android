package com.rmf.kuhcjr.Adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Data.DataPeminjamanKendaraan;
import com.rmf.kuhcjr.DetailPeminjamanMobil;
import com.rmf.kuhcjr.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterRVPeminjamanKendaraan extends RecyclerView.Adapter<AdapterRVPeminjamanKendaraan.MyViewHolder>{
    List<DataPeminjamanKendaraan> mDPK;

    public AdapterRVPeminjamanKendaraan(List <DataPeminjamanKendaraan> DPK) {
        mDPK = DPK;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_peminjaman_kendaraan, parent, false);
        MyViewHolder mViewHolder = new MyViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder,final int position){
        final String kodeKendaraan = mDPK.get(position).getKode_kendaraan();
        final String merk = mDPK.get(position).getMerk();
        final String tanggal = getDate(mDPK.get(position).getTanggal()) ;
        final String waktu =  getTime(mDPK.get(position).getTime());
        final String nomorPolisi = mDPK.get(position).getNomor_polisi();
        final String foto = mDPK.get(position).getFoto();
        final String peruntukan = mDPK.get(position).getPeruntukan();

        Picasso.get().load(ApiClient.BASE_URL+"assets/images/kendaraan/"+foto).into(holder.imageView);
//        Glide.with(holder.itemView).load("http://kuh.public-cjr.com/assets/images/kendaraan/"+foto).into(holder.imageView);
//        holder.imageView.setIm
//        holder.nomor.setText(String.valueOf(nomor));

        holder.kodeKendaraan.setText( kodeKendaraan);
        holder.merk.setText(merk);
        holder.tanggalPeminjaman.setText(tanggal);
        holder.waktuPeminjaman.setText(waktu);
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailPeminjamanMobil.class);
                intent.putExtra("kategori", "peminjaman");
                intent.putExtra("kode_kendaraan", kodeKendaraan);
                intent.putExtra("merk", merk);
                intent.putExtra("tanggal", tanggal);
                intent.putExtra("waktu", waktu);
                intent.putExtra("nomor_polisi", nomorPolisi);
                intent.putExtra("tujuan", peruntukan);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount () {
        return mDPK.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView kodeKendaraan, merk, tanggalPeminjaman,waktuPeminjaman;
        CardView cardView;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image_mobil);

            waktuPeminjaman = (TextView) itemView.findViewById(R.id.waktu_peminjaman);

            kodeKendaraan = (TextView) itemView.findViewById(R.id.kode_kendaraan);
            merk = (TextView) itemView.findViewById(R.id.merk);
            tanggalPeminjaman = (TextView) itemView.findViewById(R.id.tgl_peminjaman);
            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }
    }
    private String getDate(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy");
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
