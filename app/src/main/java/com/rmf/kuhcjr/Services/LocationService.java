package com.rmf.kuhcjr.Services;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.IBinder;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.rmf.kuhcjr.R;
import com.rmf.kuhcjr.Utils.Constans;

public class LocationService extends Service {

    private LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);
            if (locationResult != null && locationResult.getLastLocation() != null) {
                double latitude = locationResult.getLastLocation().getLatitude();
                double longtitude = locationResult.getLastLocation().getLongitude();
                Intent i =  new Intent(Constans.ACTION_NAME);
                i.putExtra("lat",latitude);
                i.putExtra("long",longtitude);
                sendBroadcast(i);
            }
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");

    }

    private void startLocationService() {
        String channelID = "location_notification_channel";
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent();
        PendingIntent pendingIntent = PendingIntent.getActivity(
                getApplicationContext(),
                0,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                getApplicationContext(), channelID
        );
        builder.setSmallIcon(R.mipmap.ic_launcher_app_icon);
        builder.setContentTitle("Location Service");
        builder.setDefaults(NotificationCompat.DEFAULT_ALL);
        builder.setContentText("Tracking your location");
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(false);
        builder.setPriority(NotificationCompat.PRIORITY_MAX);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (notificationManager != null && notificationManager.getNotificationChannel(channelID) == null) {
                NotificationChannel notificationChannel = new NotificationChannel(
                        channelID,
                        "Location Service",
                        NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setDescription("ini location services");
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(4000);
        locationRequest.setFastestInterval(2000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        LocationServices.getFusedLocationProviderClient(this)
                .requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());
        startForeground(Constans.LOCATION_SERVICE_ID,builder.build());
    }

    private void stopLocationService(){
        LocationServices.getFusedLocationProviderClient(this)
                .removeLocationUpdates(locationCallback);
        stopForeground(true);
        stopSelf();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent !=null){
            String action  = intent.getAction();
            if(action!=null){
                if(action.equals(Constans.ACTION_START_LOCATION_SERVICE)){
                    startLocationService();
                }else if(action.equals(Constans.ACTION_STOP_LOCATION_SERVICE)){
                    stopLocationService();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
