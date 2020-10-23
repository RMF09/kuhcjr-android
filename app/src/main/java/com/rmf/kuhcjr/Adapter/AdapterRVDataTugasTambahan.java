package com.rmf.kuhcjr.Adapter;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.rmf.kuhcjr.Data.DataTugas;
import com.rmf.kuhcjr.DetailTugas;
import com.rmf.kuhcjr.Fragments.TugasPokok;
import com.rmf.kuhcjr.Fragments.TugasTambahan;
import com.rmf.kuhcjr.R;

import java.util.List;

public class AdapterRVDataTugasTambahan extends RecyclerView.Adapter<AdapterRVDataTugasTambahan.Tugas> {
    List<DataTugas> list;
    TugasTambahan tp;

    public AdapterRVDataTugasTambahan(List<DataTugas> list, TugasTambahan tp) {
        this.list = list;
        this.tp =tp;
    }

    @NonNull
    @Override
    public Tugas onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_tugas, viewGroup,false);

        return new Tugas(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final Tugas tugas, int i) {
        final int id = list.get(i).getId();
        final String kegiatan =  list.get(i).getKegiatan();
        final int kuant =  list.get(i).getTarget_kuant();
        final String output = list.get(i).getTarget_output();
        final int kual = list.get(i).getTarget_kual();
        final int waktu = list.get(i).getTarget_waktu();
        final String satuanWaktu = list.get(i).getTarget_satuan_waktu();
        final int biaya = list.get(i).getTarget_biaya();

        tugas.nomor.setText(String.valueOf(i+1));
        tugas.kegiatan.setText(kegiatan);
        tugas.kuant.setText(String.valueOf(kuant) + " "+output);
        tugas.kual.setText(String.valueOf(kual)+"%");
        tugas.waktu.setText(String.valueOf(waktu) + " "+satuanWaktu);


        tugas.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), DetailTugas.class);
                intent.putExtra("kegiatan",kegiatan);
                intent.putExtra("id",id);
                intent.putExtra("kuant",kuant);
                intent.putExtra("output",output);
                intent.putExtra("kual",kual);
                intent.putExtra("waktu",waktu);
                intent.putExtra("satuan_waktu",satuanWaktu);
                intent.putExtra("biaya",biaya);
                intent.putExtra("menu","tugas_pokok");
                intent.putExtra("tahun_id",tp.tahun_id);
                intent.putExtra("menu",tp.dariActivity);


                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class Tugas extends RecyclerView.ViewHolder {

        TextView nomor,kegiatan,kuant,kual,waktu;
        CardView cardView;
        public Tugas(@NonNull View itemView) {
            super(itemView);

            nomor = (TextView) itemView.findViewById(R.id.nomor);
            kegiatan = (TextView) itemView.findViewById(R.id.kegiatan_tugas_jabatan);
            kuant = (TextView) itemView.findViewById(R.id.kuant);
            kual = (TextView) itemView.findViewById(R.id.kual);
            waktu = (TextView) itemView.findViewById(R.id.waktu);

            cardView = (CardView) itemView.findViewById(R.id.cardview);
        }
    }

}
