package com.rmf.kuhcjr;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressDialogPengajuan {

    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    ProgressBar progressBar;
    ImageView checklist;
    ImageView error;
    TextView text;


    public ProgressDialogPengajuan(Context context){
        builder = new AlertDialog.Builder(context);
        final View view = LayoutInflater.from(context).inflate(R.layout.progress_dialog,null);
        //initial ui
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        checklist = (ImageView) view.findViewById(R.id.image_progress_status_checklist);
        error = (ImageView) view.findViewById(R.id.image_progress_status_error);
        text = (TextView) view.findViewById(R.id.text_loading);

        builder.setView(view);
        dialog = builder.create();
    }
    public void show(String teks){
        text.setText(teks);

        checklist.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        dialog.show();
    }
    public void dismiss(){
        dialog.dismiss();
    }

    public void success(){
        progressBar.setVisibility(View.GONE);
        checklist.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        text.setText("Berhasil diajukan");
    }
    public void failure(String teksError){
        progressBar.setVisibility(View.GONE);
        checklist.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        text.setText("Berhasil diajukan");
    }
}
