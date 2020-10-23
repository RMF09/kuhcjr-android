package com.rmf.kuhcjr.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataFileKegiatan;
import com.rmf.kuhcjr.Data.DataKegiatan;
import com.rmf.kuhcjr.Data.GetPostPutFileKegiatan;
import com.rmf.kuhcjr.Data.PostPutKegiatan;
import com.rmf.kuhcjr.DetailKegiatan;
import com.rmf.kuhcjr.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdapterRVDataFileKegiatan extends RecyclerView.Adapter<AdapterRVDataFileKegiatan.FileKegiatan> {
    List<DataFileKegiatan> list;
    Context context;
    AlertDialog alertDialogHapus;
    TextView textKeteranganHapus;

    DetailKegiatan detailKegiatan;
    ApiInterface mApiInterface;

    TextView textHapus;
    TextView textBatal;
    ImageView imageHapus;

    boolean dihapus=false;
    int idfile =0;
    String namaFileHapus;


    public AdapterRVDataFileKegiatan(List<DataFileKegiatan> list,Context context, DetailKegiatan detailKegiatan) {
        this.list = list;
        this.context = context;
        this.detailKegiatan = detailKegiatan;
        initialDialogHapus();
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);
    }

    @NonNull
    @Override
    public FileKegiatan onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_data_file_kegiatan, viewGroup,false);

        return new FileKegiatan(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final FileKegiatan fileKegiatan, int i) {
        final int id = list.get(i).getId();
        final String namaFile =  list.get(i).getFile();


        fileKegiatan.namaFile.setText(namaFile);

        fileKegiatan.hapusFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                namaFileHapus = namaFile;
                idfile =id;
                textKeteranganHapus.setText("Hapus file '"+namaFileHapus+"'?");
                alertDialogHapus.show();
            }
        });

        fileKegiatan.namaFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                detailKegiatan.namafile =namaFile;
                detailKegiatan.downloadZipFile(detailKegiatan.namafile);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class FileKegiatan extends RecyclerView.ViewHolder {

        TextView namaFile;
        ImageView hapusFile;

        public FileKegiatan(@NonNull View itemView) {
            super(itemView);

            namaFile = (TextView) itemView.findViewById(R.id.nama_file);
            hapusFile= (ImageView) itemView.findViewById(R.id.hapus_file);


        }
    }

    private void initialDialogHapus(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_hapus,null);

        imageHapus = view.findViewById(R.id.image_peringatan);
        textHapus = view.findViewById(R.id.hapus);
        textBatal = view.findViewById(R.id.batal);
        textKeteranganHapus = view.findViewById(R.id.text_dialog);

        builder.setView(view);
        alertDialogHapus = builder.create();
        alertDialogHapus.setCancelable(false);

        textHapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(dihapus){
                    alertDialogHapus.dismiss();
                    detailKegiatan.loadDataFileKegiatan();
                }else {
                    hapusDataFile(idfile,namaFileHapus);
                }
            }
        });

        textBatal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialogHapus.dismiss();
            }
        });
    }
    private void setSuccessDeleteFile(){
        textBatal.setVisibility(View.GONE);
        textHapus.setText("OK");
        imageHapus.setImageResource(R.drawable.ic_check_black_24dp);
        textKeteranganHapus.setText("File '"+namaFileHapus+"' berhasil dihapus");
    }
    private void setFailedDeleteFile(String message){
        imageHapus.setImageResource(R.drawable.ic_error_black_24dp);
        textKeteranganHapus.setText(message);
    }
    private void hapusDataFile(int id,String namaFile){
//        progressBar.setVisibility(View.VISIBLE);
        textKeteranganHapus.setText("Harap tunggu...");
        Call<GetPostPutFileKegiatan> fileKegiatanCall = mApiInterface.deleteFileKegiatan(id,namaFile);
        fileKegiatanCall.enqueue(new Callback<GetPostPutFileKegiatan>() {
            @Override
            public void onResponse(Call<GetPostPutFileKegiatan> call, Response<GetPostPutFileKegiatan>
                    response) {

                if(response.isSuccessful()){
                    String status = response.body().getStatus();
                    String message= response.body().getMessage();
                    if(status.equals("berhasil")){
                        dihapus=true;
                        setSuccessDeleteFile();

                    }else{
                       setFailedDeleteFile("Gagal menghapus file kegiatan");
                    }
                }else{
                    setFailedDeleteFile("Terjadi masalah pada Server");
                }


            }

            @Override
            public void onFailure(Call<GetPostPutFileKegiatan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                setFailedDeleteFile(t.getMessage());
//                Toast.makeText(context, "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
        }
        });
    }


}
