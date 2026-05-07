package com.example.fproject1;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.Locale;

public class ReminderReceiver extends BroadcastReceiver {

    public static final String EXTRA_PLACE_NAME = "extra_place_name";
    public static final String EXTRA_PLACE_FEE = "extra_place_fee";

    @Override
    public void onReceive(Context context, Intent intent) {
        String placeName = intent.getStringExtra(EXTRA_PLACE_NAME);
        String fee = intent.getStringExtra(EXTRA_PLACE_FEE);
        
        Log.d("ReminderReceiver", "Received broadcast for: " + placeName);

        String normalizedFee = fee == null ? "" : fee.toLowerCase(Locale.US);
        String feeMessage;
        if (normalizedFee.contains("free")) {
            feeMessage = "This place is free to enter.";
        } else if (normalizedFee.contains("cash")) {
            feeMessage = "This place is cash-only for entry fee.";
        } else {
            feeMessage = "Entry fee details: " + (TextUtils.isEmpty(fee) ? "Check at location." : fee);
        }

        Intent launchIntent = new Intent(context, SplashActivity.class);
        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        
        int requestCode = (placeName == null ? "place" : placeName).hashCode();
        PendingIntent contentIntent = PendingIntent.getActivity(
                context,
                Math.abs(requestCode),
                launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, MyApp.REMINDER_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(placeName == null || placeName.trim().isEmpty() ? "Place details" : placeName)
                .setContentText(feeMessage)
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setAutoCancel(true);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        
        if (notificationManager != null) {
            int notificationId = 4000 + (Math.abs(requestCode) % 1000);
            notificationManager.notify(notificationId, builder.build());
            Log.d("ReminderReceiver", "Notification posted for id: " + notificationId);
        }
    }
}