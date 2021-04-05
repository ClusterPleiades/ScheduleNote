package com.pleiades.pleione.schedulenote;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String channelId = "channel";
        String channelName = "channelName";

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // response oreo update
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;

            // apply NotificationChannel to manage notification efficiently
            NotificationChannel notificationChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, channelId);

        // notification is clicked
        Intent notificationIntent = new Intent(context, MainActivity.class);

        // main activity to fragment
        notificationIntent.putExtra("startPage", "schedule");

        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        // if created PendingIntent is already exists, change ExtraData of Intent only
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        SharedPreferences prefs = context.getSharedPreferences("prefs", Context.MODE_PRIVATE);

        ArrayList<Schedule> scheduleList = PrefsController.getScheduleListPrefs(context, "scheduleList");
        ArrayList<NotificationRequest> notificationRequestList = PrefsController.getNotificationRequestListPrefs(context, "notificationRequestList");
        String contextTitle = null;

        for (int i = 0; i < notificationRequestList.size(); i++) {
            if (!notificationRequestList.get(i).completed) {
                contextTitle = scheduleList.get(notificationRequestList.get(i).position).title;
                notificationRequestList.get(i).completed = true;
                PrefsController.setNotificationRequestListPrefs(context, "notificationRequestList", notificationRequestList);
                break;
            }
        }

        builder.setContentTitle(contextTitle) // title
                .setDefaults(Notification.DEFAULT_ALL) // sound, vibrate, etc..
                .setAutoCancel(true) // touch remove
                .setPriority(NotificationCompat.PRIORITY_HIGH) // priority
                .setSmallIcon(R.drawable.ic_notification) // small icon
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher)) // large icon
                .setContentIntent(pendingIntent);
        notificationManager.notify(0, builder.build());
    }
}