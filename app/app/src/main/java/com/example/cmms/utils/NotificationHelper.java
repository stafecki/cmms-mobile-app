package com.example.cmms.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


public class NotificationHelper {

    public static final String CHANNEL_CRITICAL = "channel_critical";
    public static final String CHANNEL_ASSIGNMENTS = "channel_assignments";

    public static void createNotificationChannels(Context context) {
        NotificationManager manager = context.getSystemService(NotificationManager.class);

        NotificationChannel criticalChannel = new NotificationChannel(
                CHANNEL_CRITICAL,
                "Krytyczne awarie",
                NotificationManager.IMPORTANCE_HIGH
        );
        criticalChannel.setDescription("Powiadomienia o krytycznych awariach maszyn");

        NotificationChannel assignmentChannel = new NotificationChannel(
                CHANNEL_ASSIGNMENTS,
                "Przypisania zleceń",
                NotificationManager.IMPORTANCE_DEFAULT
        );
        assignmentChannel.setDescription("Powiadomienia o nowych przypisaniach zleceń");

        manager.createNotificationChannel(criticalChannel);
        manager.createNotificationChannel(assignmentChannel);
    }

    public static void sendCriticalAlert(Context context, String title, String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_CRITICAL)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    public static void sendWorkOrderAssignment(Context context, String workOrderTitle) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ASSIGNMENTS)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Nowe zlecenie do wykonania")
                .setContentText(workOrderTitle)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat manager = NotificationManagerCompat.from(context);
        manager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
