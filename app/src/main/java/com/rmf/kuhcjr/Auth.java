package com.rmf.kuhcjr;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Toast;

import com.rmf.kuhcjr.Api.ApiClient;
import com.rmf.kuhcjr.Api.ApiInterface;
import com.rmf.kuhcjr.Data.GetUserLogin;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Auth {

    private boolean hasil;
    private static Auth mInstance;
    private static Context mContext;
    private ApiInterface apiInterface;

    public Auth(Context mContext){
        this.mContext =mContext;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
    }
    public static synchronized Auth getInstance(Context context){
        if(mInstance==null){
            mInstance = new Auth(context);
        }
        return mInstance;
    }
    public boolean authenticating(String username){


        Call<GetUserLogin> userLoginCall = apiInterface.authenticating(username,"auth");
        userLoginCall.enqueue(new Callback<GetUserLogin>() {
            @Override
            public void onResponse(Call<GetUserLogin> call, Response<GetUserLogin> response) {
                String status = response.body().getStatus();
                String message = response.body().getMessage();

                if(status.equals("berhasil")){
                    hasil =true;
                }
                else {
                    hasil =false;
                }
                Toast.makeText(mContext, status, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<GetUserLogin> call, Throwable t) {

                Toast.makeText(mContext, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return hasil;
    }

}
