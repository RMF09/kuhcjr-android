package com.rmf.kuhcjr.Adapter;

import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rmf.kuhcjr.Data.DataPeminjamanKendaraan;
import com.rmf.kuhcjr.DetailPeminjamanMobil;
import com.rmf.kuhcjr.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AdapterRVPengembalianKendaraan extends RecyclerView.Adapter<AdapterRVPengembalianKendaraan.MyViewHolder>{
    List<DataPeminjamanKendaraan> mDPK;


    public AdapterRVPengembalianKendaraan(List <DataPeminjamanKendaraan> DPK) {
        mDPK = DPK;
    }

    @Override
    public MyViewHolder onCreateViewHolder (ViewGroup parent, int viewType){
        View mView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_data_pengembalian_kendaraan2, parent, false);
        MyViewHolder mViewHolder = new MyViewHolder(mView);
        return mViewHolder;
    }

    @Override
    public void onBindViewHolder (MyViewHolder holder,final int position){
        final String kodeKendaraan = mDPK.get(position).getKode_kendaraan();
        final String merk = mDPK.get(position).getMerk();
        final String tanggal = checkDate(mDPK.get(position).getTanggal_pengembalian());
        final String waktu = checkTime(mDPK.get(position).getTime_pengembalian());
        final String nomorPolisi = mDPK.get(position).getNomor_polisi();
        final String foto = mDPK.get(position).getFoto();


        Picasso.get().load("http://kuh.public-cjr.com/assets/images/kendaraan/"+foto).into(holder.imageView);
//        Glide.with(holder.itemView).load("http://kuh.public-cjr.com/assets/images/kendaraan/"+foto).into(holder.imageView);

        holder.kodeKendaraan.setText("Kode Kendaraan : " + kodeKendaraan);
        holder.merk.setText("Merk : " + merk);
        holder.tanggalPengembalian.setText( tanggal);
        holder.waktuPengembalian.setText(waktu);
        holder.cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetailPeminjamanMobil.class);
                intent.putExtra("kategori", "pengembalian");
                intent.putExtra("kode_kendaraan", kodeKendaraan);
                intent.putExtra("merk", merk);
                intent.putExtra("waktu", waktu);
                intent.putExtra("tanggal", tanggal);
                intent.putExtra("nomor_polisi", nomorPolisi);
                view.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount () {
        return mDPK.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView kodeKendaraan, merk, tanggalPengembalian,waktuPengembalian;
        CardView cardview;
        ImageView imageView;

        public MyViewHolder(View itemView) {
            super(itemView);

            imageView = (ImageView) itemView.findViewById(R.id.image_mobil);

            kodeKendaraan = (TextView) itemView.findViewById(R.id.kode_kendaraan);
            merk = (TextView) itemView.findViewById(R.id.merk);
            tanggalPengembalian = (TextView) itemView.findViewById(R.id.tgl_pengembalian);
            waktuPengembalian= (TextView) itemView.findViewById(R.id.waktu_pengembalian);
            cardview =(CardView)itemView.findViewById(R.id.cardview);
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
    private String checkDate(String tanggal){
        String hasil="-";
        if(tanggal!=null){
            hasil = getDate(tanggal);

        }
        return  hasil;
    }
    private String checkTime(String waktu){
        String hasil="-";
        if(waktu!=null){
            hasil = getTime(waktu);

        }
        return  hasil;
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
