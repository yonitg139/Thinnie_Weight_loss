package com.example.thinnie_weight_loss;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.example.thinnie_weight_loss.Settings.NO_ALERT_DATES_PREFERENCE_KEY;
import static com.example.thinnie_weight_loss.Settings.SETTINGS_PREFERENCE_NAME;

public class NotificationReceiver extends BroadcastReceiver {

    private static final String DATE_STRING_FORMAT = "%d/%d/%d";
    private static final String CHANNEL_ID = "CHANNEL";
    private static final int NOTIFICATION_ID = 0;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = context.getString(R.string.channel_name);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

//        get saved no-notification dates
        SharedPreferences settings = context.getSharedPreferences(SETTINGS_PREFERENCE_NAME, MODE_PRIVATE);
        Set<String> selectedDates = settings.getStringSet(NO_ALERT_DATES_PREFERENCE_KEY, new HashSet<String>());

        String todayString = calendarToString(Calendar.getInstance());
        if(selectedDates.contains(todayString)) {
//            today is a no-notification day
            return;
        }

        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, loginIntent, 0);

//        construct notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_settings)
                .setContentTitle("Thinnie Reminder")
                .setContentText("Don't forget to hop on your weight!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                // Set the intent that will fire when the user taps the notification
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

//        show notification
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }

    public static String calendarToString(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        return String.format(DATE_STRING_FORMAT, day, month, year);
    }
}