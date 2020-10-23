package com.rmf.kuhcjr;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
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
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.rmf.kuhcjr.Adapter.SpinnerAdapter;
import com.rmf.kuhcjr.Adapter.SpinnerAdapterPimpinan;
import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.DataKantor;
import com.rmf.kuhcjr.Data.DataPimpinan;
import com.rmf.kuhcjr.Data.DataUserLogin;
import com.rmf.kuhcjr.Data.GetPimpinan;
import com.rmf.kuhcjr.Data.GetUserLogin;
import com.rmf.kuhcjr.Data.PostPutPerjalananDinas;
import com.rmf.kuhcjr.EditDataPengajuan.EditPengajuanDinas;

import java.io.File;
import java.net.URISyntaxException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.rmf.kuhcjr.RealPathUtil.isExternalStorageDocument;

public class EditProfile extends AppCompatActivity {

    private ApiInterface mApiInterface;
    private EditText editInstansi,editSatuanKerja,editUnitKerja,editSubUnitKerja;
    private EditText editNama,editNIK,editJabatan,editNoID,editNoPassport,editNoTelepon,editAlamat,editPendidikanTerakhir;
    private Spinner spinnerNamaAtasan;
    private EditText editNIKAtasan,editJabatanAtasan;
    private FloatingActionButton fab,fabPhoto;
    private ScrollView scrollView;

    private ImageView imageProfile;
    private List<DataPimpinan> listPimpinan;
    private boolean berhasilDiload=false;
    private String nipAtasan;
    private int indexAtasan;

    private static final int STORAGE_PERMISSION_CODE = 1;
    private Uri fileUri;
    private String filePath;
    private String fileBefore="";
    private boolean kegiatan_identitas_diisi=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
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

        imageProfile = (ImageView) findViewById(R.id.image_profile);
        Glide.with(this).load(R.drawable.kain)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.circleCropTransform()).into(imageProfile);

        initialUI();
        loadDataProfile();

    }

    private void initialUI(){
        editInstansi = (EditText) findViewById(R.id.edit_instansi);
        editSatuanKerja = (EditText) findViewById(R.id.edit_satuan_kerja);
        editUnitKerja = (EditText) findViewById(R.id.edit_unit_kerja);
        editSubUnitKerja = (EditText) findViewById(R.id.edit_sub_unit);

        editNama = (EditText) findViewById(R.id.edit_nama);
        editNIK = (EditText) findViewById(R.id.edit_nik);
        editJabatan = (EditText) findViewById(R.id.edit_jabatan);
        editNoID = (EditText) findViewById(R.id.edit_no_identitas);
        editNoPassport = (EditText) findViewById(R.id.edit_no_passport);
        editNoTelepon = (EditText) findViewById(R.id.edit_no_telepon);
        editAlamat = (EditText) findViewById(R.id.edit_alamat);
        editPendidikanTerakhir = (EditText) findViewById(R.id.edit_pendidikan_t);

        spinnerNamaAtasan = (Spinner) findViewById(R.id.spinner_nama_atasan);
        editNIKAtasan = (EditText) findViewById(R.id.edit_nik_atasan);
        editJabatanAtasan = (EditText) findViewById(R.id.edit_jabatan_atasan);

        fab = (FloatingActionButton) findViewById(R.id.btn_simpan);
        fabPhoto = (FloatingActionButton) findViewById(R.id.btn_ubah_foto);
        scrollView = (ScrollView) findViewById(R.id.scroll);
        mApiInterface = ApiClient.getClient().create(ApiInterface.class);

        spinnerNamaAtasan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(berhasilDiload){
                    indexAtasan =position;
                    aturDataAtasan(position);
//                    Toast.makeText(EditProfile.this, "Data :  "+spinnerNamaAtasan.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                simpanPerubahan();
            }
        });
        fabPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,1);
            }
        });

    }


    private void loadDataProfile(){


        String username = SharedPrefs.getInstance(EditProfile.this).LoggedInUser();
        String metode = "ambilData";

        Call<GetUserLogin> getUserLoginCall = mApiInterface.getDataProfile(username,metode);
        getUserLoginCall.enqueue(new Callback<GetUserLogin>() {
            @Override
            public void onResponse(Call<GetUserLogin> call, Response<GetUserLogin>
                    response) {
                String status = response.body().getStatus();
                String message = response.body().getMessage();
                List<DataUserLogin> listDataProfile = response.body().getData();
                if(status.equals("berhasil")){


                    Glide.with(EditProfile.this)
                            .load(ApiClient.BASE_URL+"assets/files/kinerja/identitas/"+listDataProfile.get(0).getFile())
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.circleCropTransform()).into(imageProfile);
                    fileBefore = listDataProfile.get(0).getFile();
//                    Toast.makeText(EditProfile.this,"File Before :"+fileBefore,Toast.LENGTH_LONG).show();
                    aturData(listDataProfile.get(0).getInstansi(),
                            listDataProfile.get(0).getSatuan_kerja(),
                            listDataProfile.get(0).getUnit_kerja(),
                            listDataProfile.get(0).getSub_unit_kerja(),
                            listDataProfile.get(0).getNama(),
                            listDataProfile.get(0).getJabatan(),
                            listDataProfile.get(0).getNip(),
                            listDataProfile.get(0).getNomor_identitas(),
                            listDataProfile.get(0).getNo_passport(),
                            listDataProfile.get(0).getNo_tlp(),
                            listDataProfile.get(0).getAlamat(),
                            listDataProfile.get(0).getPendidikan_terakhir());

                    nipAtasan = listDataProfile.get(0).getNip_atasan();
                    kegiatan_identitas_diisi=true;

                }
                loadDataPimpinan();
            }


            @Override
            public void onFailure(Call<GetUserLogin> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(EditProfile.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void aturData(String instansi,String satuanKerja,String unitKerja,String sub_unit,
                          String nama, String jabatan, String nik, String nomor_identitas, String nomor_passport,
                          String nomor_telepon, String alamat, String pendidikan_terakhir
                          ){
        editInstansi.setText(instansi);
        editSatuanKerja.setText(satuanKerja);
        editUnitKerja.setText(unitKerja);
        editSubUnitKerja.setText(sub_unit);

        editNama.setText(nama);
        editJabatan.setText(jabatan);
        editNIK.setText(nik);
        editNoID.setText(nomor_identitas);
        editNoPassport.setText(nomor_passport);
        editNoTelepon.setText(nomor_telepon);
        editAlamat.setText(alamat);
        editPendidikanTerakhir.setText(pendidikan_terakhir);

        //Visiblity
//        textNama.setVisibility(View.VISIBLE);
//        textInstansi.setVisibility(View.VISIBLE);
//        textNIK.setVisibility(View.VISIBLE);
//        textSatuanKerja.setVisibility(View.VISIBLE);
//        textJabatan.setVisibility(View.VISIBLE);

    }
    private void loadDataPimpinan(){


        String metode = "ambilPimpinan";

        Call<GetPimpinan> getPimpinanCall = mApiInterface.getDataPimpinan(metode);
        getPimpinanCall.enqueue(new Callback<GetPimpinan>() {
            @Override
            public void onResponse(Call<GetPimpinan> call, Response<GetPimpinan>
                    response) {
                String status = response.body().getStatus();
                if(status.equals("berhasil")){
                    listPimpinan = response.body().getListDataPimpinan();
                    ArrayAdapter<DataPimpinan> dataPimpinanArrayAdapter = new SpinnerAdapterPimpinan(EditProfile.this, android.R.layout.simple_spinner_item,listPimpinan);
                    spinnerNamaAtasan.setAdapter(dataPimpinanArrayAdapter);

                    if (kegiatan_identitas_diisi)
                        checkNamaAtasanfromDB();


//                    aturDataAtasan(0);
                    berhasilDiload=true;
                }
            }


            @Override
            public void onFailure(Call<GetPimpinan> call, Throwable t) {
                Log.e("Retrofit Get", t.toString());
                Toast.makeText(EditProfile.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void simpanPerubahan(){
        Toast.makeText(this, "Harap tunggu...", Toast.LENGTH_LONG).show();
        if(cekFormData()){
            try {

                File file = new File(FileUtils.getPath(EditProfile.this,fileUri));
                String uname = SharedPrefs.getInstance(this).LoggedInUser();
                //RequestBody
                RequestBody requestFile = RequestBody.create(MediaType.parse(getContentResolver().getType(fileUri)),file);
                MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file",file.getName(),requestFile);
                String metodeSimpan ="editProfil";
                if(kegiatan_identitas_diisi==false){
                    metodeSimpan = "simpanProfil";
                }
                RequestBody metode = RequestBody.create(MediaType.parse("text/plain"), metodeSimpan);
                RequestBody username = RequestBody.create(MediaType.parse("text/plain"), uname);
                RequestBody instansi = RequestBody.create(MediaType.parse("text/plain"), editInstansi.getText().toString());
                RequestBody satuanKerja = RequestBody.create(MediaType.parse("text/plain"), editSatuanKerja.getText().toString());
                RequestBody unitKerja = RequestBody.create(MediaType.parse("text/plain"), editUnitKerja.getText().toString());
                RequestBody subUnitKerja = RequestBody.create(MediaType.parse("text/plain"), editSubUnitKerja.getText().toString());
                RequestBody alamat = RequestBody.create(MediaType.parse("text/plain"), editAlamat.getText().toString());
                RequestBody nama = RequestBody.create(MediaType.parse("text/plain"), editNama.getText().toString());
                RequestBody nip = RequestBody.create(MediaType.parse("text/plain"), editNIK.getText().toString());
                RequestBody jabatan = RequestBody.create(MediaType.parse("text/plain"), editJabatan.getText().toString());
                RequestBody nomorIdentitas = RequestBody.create(MediaType.parse("text/plain"), editNoID.getText().toString());
                RequestBody noTlp = RequestBody.create(MediaType.parse("text/plain"), editNoTelepon.getText().toString());
                RequestBody pendidikanTerakhir = RequestBody.create(MediaType.parse("text/plain"), editPendidikanTerakhir.getText().toString());
                RequestBody noPassport = RequestBody.create(MediaType.parse("text/plain"), editNoPassport.getText().toString());
                RequestBody namaAtasan = RequestBody.create(MediaType.parse("text/plain"), listPimpinan.get(indexAtasan).getNama());
                RequestBody nipAtasan = RequestBody.create(MediaType.parse("text/plain"), editNIKAtasan.getText().toString());
                RequestBody jabatanAtasan = RequestBody.create(MediaType.parse("text/plain"), editJabatanAtasan.getText().toString());
                RequestBody fileB = RequestBody.create(MediaType.parse("text/plain"), fileBefore);


                Call<GetUserLogin> PDCall = mApiInterface.editProfil(metode,username,fileBody,instansi,satuanKerja,unitKerja,subUnitKerja,
                        alamat,nama,nip,jabatan,nomorIdentitas,noTlp,pendidikanTerakhir,noPassport,namaAtasan,nipAtasan,jabatanAtasan,fileB);
                PDCall.enqueue(new Callback<GetUserLogin>() {
                    @Override
                    public void onResponse(Call<GetUserLogin> call, Response<GetUserLogin>
                            response) {
                        if(response.isSuccessful()){
                            String status = response.body().getStatus();
                            String message = response.body().getMessage();
                            if(status.equals("gagal")){

                                Toast.makeText(EditProfile.this, "Gagal Edit Profil", Toast.LENGTH_SHORT).show();
                            }else{

                                Toast.makeText(EditProfile.this, "Berhasil Edit Profil", Toast.LENGTH_SHORT).show();
                                loadDataProfile();
                            }
                        }else{
                            Toast.makeText(EditProfile.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<GetUserLogin> call, Throwable t) {
                        Log.e("Retrofit Get", t.toString());
                        Toast.makeText(EditProfile.this, "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }catch (NullPointerException e){
                filePath ="";
//                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                String uname = SharedPrefs.getInstance(this).LoggedInUser();
                //RequestBody
                RequestBody requestFile = RequestBody.create(MediaType.parse(""),"");
                MultipartBody.Part fileBody = MultipartBody.Part.createFormData("file","",requestFile);
                RequestBody metode = RequestBody.create(MediaType.parse("text/plain"), "editProfil");
                RequestBody username = RequestBody.create(MediaType.parse("text/plain"), uname);
                RequestBody instansi = RequestBody.create(MediaType.parse("text/plain"), editInstansi.getText().toString());
                RequestBody satuanKerja = RequestBody.create(MediaType.parse("text/plain"), editSatuanKerja.getText().toString());
                RequestBody unitKerja = RequestBody.create(MediaType.parse("text/plain"), editUnitKerja.getText().toString());
                RequestBody subUnitKerja = RequestBody.create(MediaType.parse("text/plain"), editSubUnitKerja.getText().toString());
                RequestBody alamat = RequestBody.create(MediaType.parse("text/plain"), editAlamat.getText().toString());
                RequestBody nama = RequestBody.create(MediaType.parse("text/plain"), editNama.getText().toString());
                RequestBody nip = RequestBody.create(MediaType.parse("text/plain"), editNIK.getText().toString());
                RequestBody jabatan = RequestBody.create(MediaType.parse("text/plain"), editJabatan.getText().toString());
                RequestBody nomorIdentitas = RequestBody.create(MediaType.parse("text/plain"), editNoID.getText().toString());
                RequestBody noTlp = RequestBody.create(MediaType.parse("text/plain"), editNoTelepon.getText().toString());
                RequestBody pendidikanTerakhir = RequestBody.create(MediaType.parse("text/plain"), editPendidikanTerakhir.getText().toString());
                RequestBody noPassport = RequestBody.create(MediaType.parse("text/plain"), editNoPassport.getText().toString());
                RequestBody namaAtasan = RequestBody.create(MediaType.parse("text/plain"), listPimpinan.get(indexAtasan).getNama());
                RequestBody nipAtasan = RequestBody.create(MediaType.parse("text/plain"), editNIKAtasan.getText().toString());
                RequestBody jabatanAtasan = RequestBody.create(MediaType.parse("text/plain"), editJabatanAtasan.getText().toString());
                RequestBody fileB = RequestBody.create(MediaType.parse("text/plain"), fileBefore);


                Call<GetUserLogin> PDCall = mApiInterface.editProfil(metode,username,fileBody,instansi,satuanKerja,unitKerja,subUnitKerja,
                        alamat,nama,nip,jabatan,nomorIdentitas,noTlp,pendidikanTerakhir,noPassport,namaAtasan,nipAtasan,jabatanAtasan,fileB);
                PDCall.enqueue(new Callback<GetUserLogin>() {
                    @Override
                    public void onResponse(Call<GetUserLogin> call, Response<GetUserLogin>
                            response) {
                        if(response.isSuccessful()){
                            String status = response.body().getStatus();
                            String message = response.body().getMessage();
                            if(status.equals("gagal")){

                                Toast.makeText(EditProfile.this, "Gagal Edit Profil", Toast.LENGTH_SHORT).show();
                            }else{

                                Toast.makeText(EditProfile.this, "Berhasil Edit Profil", Toast.LENGTH_SHORT).show();
                                loadDataProfile();
                            }
                        }else{
                            Toast.makeText(EditProfile.this, "Terjadi masalah pada Server", Toast.LENGTH_SHORT).show();
                        }

                    }

                    @Override
                    public void onFailure(Call<GetUserLogin> call, Throwable t) {
                        Log.e("Retrofit Get", t.toString());
                        Toast.makeText(EditProfile.this, "ERR :"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }
    private boolean cekFormData(){
        if(TextUtils.isEmpty(editInstansi.getText().toString())){
            Toast.makeText(this, "Instansi Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editInstansi.getLeft(),editInstansi.getTop()-70);
            editInstansi.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editSatuanKerja.getText().toString())){
            Toast.makeText(this, "Satuan Kerja Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editSatuanKerja.getLeft(),editSatuanKerja.getTop()-70);
            editSatuanKerja.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editUnitKerja.getText().toString())){
            Toast.makeText(this, "Unit Kerja Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editUnitKerja.getLeft(),editUnitKerja.getTop()-70);
            editUnitKerja.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editSubUnitKerja.getText().toString())){
            Toast.makeText(this, "Sub Unit Kerja Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editSubUnitKerja.getLeft(),editSubUnitKerja.getTop()-70);
            editSubUnitKerja.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editNama.getText().toString())){
            Toast.makeText(this, "Nama Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editNama.getLeft(),editNama.getTop()-70);
            editNama.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editNIK.getText().toString())){
            Toast.makeText(this, "NIK Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editNIK.getLeft(),editNIK.getTop()-70);
            editNIK.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editJabatan.getText().toString())){
            Toast.makeText(this, "Jabatan Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editJabatan.getLeft(),editJabatan.getTop()-70);
            editJabatan.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editNoID.getText().toString())){
            Toast.makeText(this, "Nomor Identitas Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editNoID.getLeft(),editNoID.getTop()-70);
            editNoID.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editNoPassport.getText().toString())){
            Toast.makeText(this, "Nomor Passport Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editNoPassport.getLeft(),editNoPassport.getTop()-70);
            editNoPassport.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editNoTelepon.getText().toString())){
            Toast.makeText(this, "Nomor Telepon Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editNoTelepon.getLeft(),editNoTelepon.getTop()-70);
            editNoTelepon.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editAlamat.getText().toString())){
            Toast.makeText(this, "Alamat Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editAlamat.getLeft(),editAlamat.getTop()-70);
            editAlamat.requestFocus();
            return false;
        }
        else if(TextUtils.isEmpty(editPendidikanTerakhir.getText().toString())){
            Toast.makeText(this, "Pendidikan Terakhir Tidak Boleh Kosong", Toast.LENGTH_SHORT).show();
            scrollView.smoothScrollTo(editPendidikanTerakhir.getLeft(),editPendidikanTerakhir.getTop()-70);
            editPendidikanTerakhir.requestFocus();
            return false;
        }
        else{
            return true;
        }
    }
    public void aturDataAtasan(int index){
        editNIKAtasan.setText(listPimpinan.get(index).getNik());
        editJabatanAtasan.setText(listPimpinan.get(index).getJabatan());

    }
    public void checkNamaAtasanfromDB(){
        for (int i =0; i<listPimpinan.size();i++){
            if(nipAtasan.equals(listPimpinan.get(i).getNik())){
                spinnerNamaAtasan.setSelection(i);
                break;
            }

        }
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

    // Function to check and request permission.
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(EditProfile.this, permission)
                == PackageManager.PERMISSION_DENIED) {

            // Requesting the permission
            ActivityCompat.requestPermissions(EditProfile.this,
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
                Toast.makeText(EditProfile.this,
                        "Gallery Permission Denied",
                        Toast.LENGTH_SHORT)
                        .show();

            }
        }
    }
    private void pickFile(){
        Intent intent = new Intent();
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, STORAGE_PERMISSION_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case STORAGE_PERMISSION_CODE:
                if(resultCode==-1){
                    fileUri = data.getData();


//                        Toast.makeText(this,FileUtils.getPath(EditProfile.this,fileUri), Toast.LENGTH_SHORT).show();

                    filePath = fileUri.getLastPathSegment();
//                    imageProfile.setImageURI(fileUri);
                    Glide.with(this).load(fileUri)
                            .transition(DrawableTransitionOptions.withCrossFade())
                            .apply(RequestOptions.circleCropTransform()).into(imageProfile);
                }
        }
    }


}