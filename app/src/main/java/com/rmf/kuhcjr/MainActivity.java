package com.rmf.kuhcjr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.GetUserLogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editUsername,editPassword;
    private Button btnLogin;

    private LinearLayout linearInputLogin,linearProgress;

    private static ApiInterface apiInterface;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        initialUI();
        actionUI();


        //Check Login
        if(SharedPrefs.getInstance(MainActivity.this).isLoggedIn()){
            startActivity(new Intent(MainActivity.this,MenuUtama.class));
            finish();
        }

    }

    private void initialUI(){

        linearInputLogin = (LinearLayout) findViewById(R.id.linear_input_login);
        linearProgress = (LinearLayout) findViewById(R.id.linear_progress_login);

        editUsername = (EditText) findViewById(R.id.input_edit_username);
        editPassword= (EditText) findViewById(R.id.input_edit_password);
        btnLogin = (Button) findViewById(R.id.btn_login);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }
    private void actionUI(){
       btnLogin.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {

               authLogin(editUsername.getText().toString(),editPassword.getText().toString());

           }
       });
    }

    private void authLogin(final String username, String password){

        linearInputLogin.setVisibility(View.GONE);
        linearProgress.setVisibility(View.VISIBLE);

        Call<GetUserLogin> userLoginCall = apiInterface.auth(username,password);
        userLoginCall.enqueue(new Callback<GetUserLogin>() {
            @Override
            public void onResponse(Call<GetUserLogin> call, Response<GetUserLogin> response) {
                String status = response.body().getStatus();
                String message = response.body().getMessage();
                
                if(status.equals("berhasil")){
                    SharedPrefs.getInstance(MainActivity.this).storeUserName(username);
                    startActivity(new Intent(MainActivity.this,MenuUtama.class));
                    finish();

                }
                else {
                    linearProgress.setVisibility(View.GONE);
                    linearInputLogin.setVisibility(View.VISIBLE);

                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GetUserLogin> call, Throwable t) {
                linearProgress.setVisibility(View.GONE);
                linearInputLogin.setVisibility(View.VISIBLE);

                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }





}
