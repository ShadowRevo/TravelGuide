package com.example.fproject1;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

public class MyApp extends Application {
    public static final String REMINDER_CHANNEL_ID = "travel_reminders";

    @Override
    public void onCreate() {
        super.onCreate();
        createReminderNotificationChannel();
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings =
                new FirebaseFirestoreSettings.Builder()
                        .setPersistenceEnabled(true)
                        .build();
        firestore.setFirestoreSettings(settings);
    }

    private void createReminderNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return;
        }

        NotificationChannel channel = new NotificationChannel(
                REMINDER_CHANNEL_ID,
                "Travel Reminders",
                NotificationManager.IMPORTANCE_HIGH // Increased importance for visibility
        );
        channel.setDescription("Notifications for planned place visit reminders.");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }
    }
}