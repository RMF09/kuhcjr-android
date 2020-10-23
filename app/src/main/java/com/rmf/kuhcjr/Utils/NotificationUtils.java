package com.rmf.kuhcjr.Utils;

import android.app.ActivityManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;

import com.rmf.kuhcjr.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationUtils {
    private static  String TAG = NotificationUtils.class.getSimpleName();
    private Context context;

    public NotificationUtils(Context context){
        this.context = context;
    }

    public void showNotificationMessage(String title, String message, String timeStamp, Intent intent){
        showNotificationMessage(title, message, timeStamp, intent, null);
    }
    public void showNotificationMessage(final String title, final String message, final String timeStamp, Intent intent, String imageUrl){
        if(TextUtils.isEmpty(message))
            return;

        int notifyID = 1;
        String CHANNEL_ID = "kuh_01";// The id of the channel.
        CharSequence name = "pengunguman_kuh";// The user-visible name of the channel.

//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,99,intent,0);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE) ;
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,CHANNEL_ID ) ;
        mBuilder.setContentTitle(title) ;
        mBuilder.setContentText(message) ;
        mBuilder.setLargeIcon(BitmapFactory. decodeResource (context.getResources() , R.drawable.app_iconkuh )) ;
        mBuilder.setSmallIcon(R.drawable.ic_stat_name ) ;
        mBuilder.setAutoCancel( true ) ;
        mBuilder.setContentIntent(pendingIntent);
        if (Build.VERSION. SDK_INT >= Build.VERSION_CODES. O ) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel( CHANNEL_ID , name , importance) ;
            mBuilder.setChannelId( CHANNEL_ID ) ;
            mNotificationManager.createNotificationChannel(notificationChannel) ;
        }

        mNotificationManager.notify(notifyID , mBuilder.build()) ;
    }

    /* Method checks if the app is in background or not
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }

    // Clears notification tray messages
    public static void clearNotifications(Context context) {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    public static long getTimeMilliSec(String timeStamp) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = format.parse(timeStamp);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
