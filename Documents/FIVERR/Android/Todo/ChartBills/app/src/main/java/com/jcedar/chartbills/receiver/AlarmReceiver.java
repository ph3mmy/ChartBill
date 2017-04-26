package com.jcedar.chartbills.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.jcedar.chartbills.R;
import com.jcedar.chartbills.activity.MainActivity;

/**
 * Created by OLUWAPHEMMY on 3/16/2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            String desc = intent.getStringExtra("message");
            int reqCode = intent.getIntExtra("reqCode", 0);
            showNotification(context, reqCode, desc);
//        }

    }

    public void showNotification(Context context, int reqCode, String desc) {
        Intent intent = new Intent(context, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.notif)
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentTitle("Chart Bills")
                    .setContentText(desc + " Bill is due for payment tomorrow")
            .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(desc));
            mBuilder.setContentIntent(pi);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            mBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(reqCode, mBuilder.build());
        } else {

            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle("Chart Bills")
                            .setStyle(new NotificationCompat.BigTextStyle().bigText(desc))
                    .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                    .setContentText(desc + " Bill is due for payment tomorrow");
            mBuilder.setContentIntent(pi);
            mBuilder.setDefaults(Notification.DEFAULT_SOUND);
            mBuilder.setAutoCancel(true);
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(reqCode, mBuilder.build());
        }
    }
}