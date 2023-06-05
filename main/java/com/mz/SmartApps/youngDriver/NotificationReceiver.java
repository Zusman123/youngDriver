package com.mz.SmartApps.youngDriver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationReceiver extends BroadcastReceiver {
    private String cText;
    @Override
    public void onReceive(Context context, Intent intent) {

        cText = intent.getExtras().getString("text");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create a notification channel
            String description = "MyApp Notification Channel";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("youngDriver_channel", "YoungDriverChannel", importance);
            channel.setDescription(description);
            NotificationManager notificationManager =context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
        Intent oIntent = new Intent(context, MainActivity.class);
        oIntent.putExtra("type", 1);
        oIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP & Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingoIntent = PendingIntent.getActivity(context, 2005, oIntent,  PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_MUTABLE);;



        // Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "youngDriver_channel")
                .setSmallIcon(R.mipmap.ic_launcher_or)
                .setContentTitle("נהג צעיר")
                .setContentText(cText)
                .setContentIntent(pendingoIntent)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);


        // Show the notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(2005, builder.build());
    }
}