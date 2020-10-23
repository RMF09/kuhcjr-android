package com.rmf.kuhcjr;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.rmf.kuhcjr.Fragments.BerandaFragment;

public class NotificationAbsenBerakhir extends Worker {
    private static final String WORK_RESULT = "work_result";

    public NotificationAbsenBerakhir(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Data taskData = getInputData();
        String taskDataStr = taskData.getString(BerandaFragment.MESSAGE_STATUS);
        return null;
    }
    private void showNotification(){
        int notifyID = 1;
        String CHANNEL_ID = "my_channel_01";// The id of the channel.
        CharSequence name = "notif";// The user-visible name of the channel.

        NotificationManager mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(getApplicationContext().NOTIFICATION_SERVICE) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(),CHANNEL_ID ) ;
        mBuilder.setContentTitle( new String(Character.toChars(0x1F60A))+" Waktu Absen Sebentar Lagi Berakhir!" ) ;
        mBuilder.setContentText( "Tap untuk absensi kehadiran Anda" ) ;
        mBuilder.setLargeIcon(BitmapFactory. decodeResource (getApplicationContext().getResources() , R.drawable.app_iconkuh )) ;
        mBuilder.setSmallIcon(R.drawable.ic_stat_name ) ;
        mBuilder.setAutoCancel( true ) ;
        if (android.os.Build.VERSION. SDK_INT >= android.os.Build.VERSION_CODES. O ) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel( CHANNEL_ID , name , importance) ;
            mBuilder.setChannelId( CHANNEL_ID ) ;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }

        mNotificationManager.notify(notifyID , mBuilder.build()) ;
    }
}
