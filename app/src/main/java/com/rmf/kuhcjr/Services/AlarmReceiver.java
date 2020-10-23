package com.rmf.kuhcjr.Services;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.rmf.kuhcjr.Absensi;
import com.rmf.kuhcjr.R;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Im Running", Toast.LENGTH_SHORT).show();
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "notif";// The user-visible name of the channel.

        Intent notificationIntent = new Intent(context, Absensi.class);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,99,notificationIntent,0);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID ) ;
        mBuilder.setContentTitle( new String(Character.toChars(	0x26A0))+" Waktu Absen Sebentar Lagi Berakhir!" ) ;
        mBuilder.setContentText( "Tap untuk absensi kehadiran Anda" ) ;
        mBuilder.setLargeIcon(BitmapFactory. decodeResource (context.getResources() , R.drawable.app_iconkuh )) ;
        mBuilder.setSmallIcon(R.drawable.ic_stat_name ) ;
        mBuilder.setAutoCancel( true ) ;
        mBuilder.setContentIntent(pendingIntent);
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel( CHANNEL_ID , name , importance) ;
            mBuilder.setChannelId( CHANNEL_ID ) ;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }

        mNotificationManager.notify(notifyID , mBuilder.build()) ;
    }
}
