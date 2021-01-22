package com.rmf.kuhcjr.EditDataPengajuan;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.PostPutCuti;
import com.rmf.kuhcjr.FileUtils;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.SharedPrefs;
import com.rmf.kuhcjr.Utils.DateUtils;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPengajuanCuti extends AppCompatActivity {

    private static final int STORAGE_PERMISSION_CODE = 1;
    private Uri fileUri;
    private String filePath;

    private ImageView back;
    private EditText editMulaiCuti,editSelesaiCuti,editUraian,editFile;
    private Button btnCari;
    private FloatingActionButton btnSimpan;

    private ApiInterface apiInterface;
    private String mulaiCuti,selesaiCuti,uraian,file;
    private int id;
    private String username;
    private boolean berhasil=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pengajuan_cuti);
        //        Sistem
        if (Build.VERSION.SDK_INT >= 19 && Build.VERSION.SDK_INT < 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);
        }
        if (Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        //make fully Android Transparent Status bar
        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);

        }
        username = SharedPrefs.getInstance(this).LoggedInUser();


        id = getIntent().getIntExtra("id",0);
        mulaiCuti = getIntent().getStringExtra("mulai");
        selesaiCuti = getIntent().getStringExtra("selesai");
        uraian = getIntent().getStringExtra("uraian");
        file = getIntent().getStringExtra("file");

        initialUI();

    }

    private void initialUI(){

        editMulaiCuti = (EditText) findViewById(R.id.mulai_cuti);
        editSelesaiCuti = (EditText) findViewById(R.id.selesai_cuti);
        editUraian = (EditText) findViewById(R.id.uraian);
        editFile = (EditText) findViewById(R.id.file_formulir);

        btnCari = (Button) findViewById(R.id.btn_cari);
        btnSimpan= (FloatingActionButton) findViewById(R.id.btn_simpan);

        editMulaiCuti.setText(getDate(mulaiCuti));
        editSelesaiCuti.setText(getDate(selesaiCuti));
        editUraian.setText(uraian);


        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        initialDialogPengajuan();
        this.actionUI();
    }
    private void actionUI(){

        editMulaiCuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditPengajuanCuti.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                editMulaiCuti.setText(day +"/"+(month+1)+"/"+year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });
        editSelesaiCuti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                final int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EditPengajuanCuti.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                                editSelesaiCuti.setText(day +"/"+(month+1)+"/"+year);
                            }
                        }, year, month, dayOfMonth);
                datePickerDialog.show();
            }
        });


        editFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
            }
        });

        btnSimpan.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                if(checkInput(v)){
                    show("Memperbarui data cuti...");
                    updateData(fileUri,ubahFormatTanggalKeSQL(editMulaiCuti.getText().toString()),ubahFormatTanggalKeSQL(editSelesaiCuti.getText().toString()),editUraian.getText().toString());
                }
            }
        });
    }

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(EditPengajuanCuti.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(EditPengajuanCuti.this,
                    new String[] { permission },
                    requestCode);
        }
        else {
            pickFile();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,
                permissions,
                grantResults);

        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                pickFile();
            }
            else {
                Toast.makeText(EditPengajuanCuti.this,
                        "Gallery Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();

            }
        }

    }

    private void pickFile(){
        Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
        chooseFile.setType("application/pdf");
        String[] mimetypes = {"application/pdf"};
        chooseFile.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
        chooseFile = Intent.createChooser(chooseFile,"Pilih File");
        startActivityForResult(chooseFile, STORAGE_PERMISSION_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case STORAGE_PERMISSION_CODE:
                if(resultCode==-1){
                    fileUri = data.getData();
                    filePath = fileUri.getLastPathSegment();
                    editFile.setHint(FileUtils.getPath(this,fileUri));
                }
        }
    }

    private boolean checkInput(View v){
        boolean cek = false;
        if(TextUtils.isEmpty(editMulaiCuti.getText().toString())){
            Snackbar.make(v,"Harap isi tanggal mulai cuti",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editSelesaiCuti.getText().toString())){
            Snackbar.make(v,"Harap isi tanggal selesai cuti",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(editUraian.getText().toString())){
            Snackbar.make(v,"Harap isi uraian",Snackbar.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(filePath)){
            Snackbar.make(v,"Harap pilih file formulir",Snackbar.LENGTH_SHORT).show();
        }
        else{
            cek = true;
        }
        return cek;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void updateData(Uri fileUri, String tanggalMulai, String tanggalSelesai, String uraian){

        try {
            File file = new File(FileUtils.getPath(EditPengajuanCuti.this,fileUri));

            //RequestBody
            RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),file);
            RequestBody mulaiBody = RequestBody.create(MediaType.parse("text/plain"), tanggalMulai);
            RequestBody selesaiBody = RequestBody.create(MediaType.parse("text/plain"), tanggalSelesai);
            RequestBody uraianBody = RequestBody.create(MediaType.parse("text/plain"), uraian);
            RequestBody metodeBody = RequestBody.create(MediaType.parse("text/plain"), "update");
            RequestBody idBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(id));
            RequestBody usernameBody = RequestBody.create(MediaType.parse("text/plain"), username);
            RequestBody filePS = RequestBody.create(MediaType.parse("text/plain"), this.file);
            RequestBody dateUpdatedBody = RequestBody.create(MediaType.parse("text/plain"), DateUtils.getDateAndTime());
            Call<PostPutCuti> CutiCall = apiInterface.putCuti(requestFile,idBody,dateUpdatedBody,mulaiBody,selesaiBody,uraianBody,metodeBody,usernameBody,filePS);
            CutiCall.enqueue(new Callback<PostPutCuti>() {
                @Override
                public void onResponse(Call<PostPutCuti> call, Response<PostPutCuti>
                        response) {
                    if(response.isSuccessful()){

                        String status = response.body().getStatus();
                        String message = response.body().getMessage();
                        if(status.equals("gagal")){
                            failure(message);
                            berhasil=false;
                        }
                        else{
                            success();
                            berhasil=true;
                        }
                    }else{
                        failure("Terjadi masalah pada Server");
                    }
                }

                @Override
                public void onFailure(Call<PostPutCuti> call, Throwable t) {
                    Log.e("Retrofit Get", t.toString());
                    failure(t.getMessage());
                }
            });
        }
        catch (NullPointerException e){
            filePath ="";
            editFile.setHint("File Formulir");
            failure("Tidak dapat mengupload file, coba pilih file yang lain");
        }



    }
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String getPathFromURI(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private String ubahFormatTanggalKeSQL(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date =null;
        try {
            date = simpleDateFormat.parse(tgl);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat output = new SimpleDateFormat("yyyy-MM-dd");
        String akhir = output.format(date);

        return akhir;

    }
    private String getDate(String tgl){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE, dd MMMM yyyy");;
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

    //Dialog Progress
    AlertDialog alertDialog;
    ProgressBar progressBar;
    ImageView checklist;
    ImageView error;
    TextView text;

    private void initialDialogPengajuan(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final View view = getLayoutInflater().inflate(R.layout.progress_dialog,null);

        //initial ui
        progressBar = (ProgressBar) view.findViewById(R.id.progress);
        checklist = (ImageView) view.findViewById(R.id.image_progress_status_checklist);
        error = (ImageView) view.findViewById(R.id.image_progress_status_error);
        text = (TextView) view.findViewById(R.id.text_loading);

        builder.setView(view);
        alertDialog= builder.create();
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(berhasil){
                    finish();
                }
            }
        });
    }

    public void show(String teks){
        text.setText(teks);

        checklist.setVisibility(View.GONE);
        error.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
    public void dismiss(){
        alertDialog.dismiss();
    }

    public void success(){
        progressBar.setVisibility(View.GONE);
        checklist.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        text.setText("Berhasil diperbarui");
        alertDialog.setCancelable(true);
    }
    public void failure(String teksError){
        progressBar.setVisibility(View.GONE);
        checklist.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        text.setText(teksError);
        alertDialog.setCancelable(true);
    }
    public static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }
}
