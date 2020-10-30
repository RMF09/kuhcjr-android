package com.rmf.kuhcjr;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.rmf.kuhcjr.App.Config;
import com.rmf.kuhcjr.Fragments.BerandaFragment;

import com.rmf.kuhcjr.Fragments.ProfileFragment;

import com.rmf.kuhcjr.Utils.NotificationUtils;


public class MenuUtama extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private MenuItem itemBeranda,itemProfil;
    private Fragment fragmentBeranda,fragmentProfil;
    private String tag;
    private static final String TAG = MenuUtama.class.getSimpleName();
    private BroadcastReceiver RegistBR;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_utama);
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

        fragmentBeranda = new BerandaFragment();
        fragmentProfil = new ProfileFragment();

        initialUI();
        actionUI();


        tag ="beranda";

        getSupportFragmentManager().beginTransaction().replace(R.id.frame_container,
                fragmentBeranda,tag).commit();

        FirebaseMessaging.getInstance().subscribeToTopic("Berita").addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(MenuUtama.this, "Success Berita", Toast.LENGTH_SHORT).show();
            }
        });
        RegistBR = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);


                    displayFirebaseRegId();

                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

//                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();

                }
            }
        };

    }

    private void displayFirebaseRegId() {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = pref.getString("regId", null);

        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            Toast.makeText(this, "Firebase Reg Id: " + regId, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Firebase Reg Id is not received yet!", Toast.LENGTH_SHORT).show();

    }

    private void initialUI(){
        bottomNav = (BottomNavigationView) findViewById(R.id.navigation);
    }
    private void actionUI(){
        bottomNav.setOnNavigationItemSelectedListener(navListener);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                    FragmentManager fm = getSupportFragmentManager();

                    switch (item.getItemId()) {
                        case R.id.navigation_home:

                            if(fm.findFragmentByTag("beranda")!=null){
                                fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out).show(fm.findFragmentByTag("beranda")).commit();
                            }
                            else{
                                fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out).add(R.id.frame_container,fragmentBeranda,"beranda").commit();
                            }
                            if(fm.findFragmentByTag("profil")!=null){
                                fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out).hide(fm.findFragmentByTag("profil")).commit();


                            }

                            break;
                        case R.id.navigation_profile:

                            if(fm.findFragmentByTag("profil")!=null){
                                fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out).show(fm.findFragmentByTag("profil")).commit();
                            }
                            else{
                                fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out).add(R.id.frame_container,fragmentProfil,"profil").commit();
                            }
                            if(fm.findFragmentByTag("beranda")!=null){
                                fm.beginTransaction().setCustomAnimations(android.R.anim.fade_in,
                                        android.R.anim.fade_out).hide(fm.findFragmentByTag("beranda")).commit();


                            }

                            break;


                    }
                    return true;
                }
            };




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

    @Override
    protected void onResume() {
        super.onResume();
        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(RegistBR,
                new IntentFilter(Config.REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(RegistBR,
                new IntentFilter(Config.PUSH_NOTIFICATION));

        // clear the notification area when the app is opened
        NotificationUtils.clearNotifications(getApplicationContext());
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(RegistBR);
        super.onPause();
    }
}
