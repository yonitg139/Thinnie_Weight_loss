package com.example.thinnie_weight_loss;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;
import static com.example.thinnie_weight_loss.MainActivity.LOGGED_IN_KEY;
import static com.example.thinnie_weight_loss.MainActivity.LOGGED_IN_PREFERENCE;

public class DeviceBootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            Intent alarmIntent = new Intent(context, NotificationReceiver.class).setAction("com.example.thinnie_weight_loss.notify");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            SharedPreferences prefs = context.getSharedPreferences(LOGGED_IN_PREFERENCE, MODE_PRIVATE);
            boolean loggedIn = prefs.getBoolean(LOGGED_IN_KEY, false);
            if(!loggedIn) {
//            user is not logged in
                return;
            }

            Calendar startTime = Calendar.getInstance();
            startTime.set(Calendar.HOUR_OF_DAY, 17);
            startTime.set(Calendar.MINUTE, 0);

            if (startTime.getTime().compareTo(new Date()) < 0) {
                startTime.add(Calendar.DAY_OF_MONTH, 1);
            }

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int interval = 86400000;
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), interval, pendingIntent);
        }
    }

}
