package com.gmail.lesniakwojciech.listazakupowa;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationMain {
    private static final String CHANNEL_ID = "999";
    private static final String CHANNEL_NAME = "com.gmail.lesniakwojciech.listazakupowa";
    private static final String CHANNEL_DESCRIPTION = "com.gmail.lesniakwojciech.listazakupowa";

    public static void show(final Context context, final String string) {
        NotificationManagerCompat.from(context)
                .notify(0, new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_shopping_basket)
                        .setContentTitle(context.getString(R.string.app_name))
                        .setContentText(string)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                        .setAutoCancel(true)
                        .setContentIntent(PendingIntent.getActivity(
                                context,
                                0,
                                new Intent(context, ActivityKomunikat.class)
                                        .putExtra(ActivityKomunikat.IE_KOMUNIKAT, string),
                                PendingIntent.FLAG_UPDATE_CURRENT
                                )
                        )
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(string))
                        .setDefaults(Notification.DEFAULT_SOUND)
                        .build()
                );
    }

    public static void createNotificationChannel(final Context context) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION_CODES.O <= Build.VERSION.SDK_INT) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance);
            channel.setDescription(CHANNEL_DESCRIPTION);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
