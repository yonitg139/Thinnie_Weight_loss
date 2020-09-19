package com.example.thinnie_weight_loss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.Toast;

import com.squareup.timessquare.CalendarPickerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public class Settings extends AppCompatActivity {

    public static final String NO_ALERT_DATES_PREFERENCE_KEY = "NO_ALERT_DATES";
    public static final String SETTINGS_PREFERENCE_NAME = "SETTINGS";

    private static final String DATE_FORMAT = "y/M/d";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    private CalendarPickerView calendar;
    private Set<String> datesToSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        datesToSave = new HashSet<>();
        calendar = findViewById(R.id.calendarView);

        Calendar nextTwoMonths = Calendar.getInstance();
        nextTwoMonths.add(Calendar.MONTH, 2);

        calendar.init(new Date(), nextTwoMonths.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

//        highlight already selected dates in the calendar
        SharedPreferences settings = getSharedPreferences(SETTINGS_PREFERENCE_NAME, Context.MODE_PRIVATE);
        datesToSave = settings.getStringSet(NO_ALERT_DATES_PREFERENCE_KEY, new HashSet<String>());

        for (String dateStr : datesToSave) {
            try {
                calendar.selectDate(dateFormatter.parse(dateStr));
            }
            catch(ParseException e) {
                Toast.makeText(Settings.this, "failed to parse date string: " + dateStr, Toast.LENGTH_LONG).show();
            }
        }

        //action while clicking on a date
        calendar.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                datesToSave.add(dateFormatter.format(date));
            }

            @Override
            public void onDateUnselected(Date date) {
                datesToSave.remove(dateFormatter.format(date));
            }
        });
//        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
//            @Override
//            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
////                constructs date object
////                Calendar c = Calendar.getInstance();
////                c.set(year, month, dayOfMonth);
//
//                String dateToAddStr = String.format(DATE_STRING_FORMAT, year, month, dayOfMonth);
//
//                if(datesToSave.contains(dateToAddStr)) {
//                    datesToSave.remove(dateToAddStr);
////                    check whether or not contains works
////                    set date color as potentially removed
//                }
//                else {
//                    datesToSave.add(dateToAddStr);
////                    set date color as selected
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent nextActivity;
        int id = item.getItemId();

        if (id == R.id.home) {
            nextActivity = new Intent(this, MainActivity.class);
        }
        else if (id == R.id.history)
            nextActivity = new Intent(this, History.class);
        else
            nextActivity = new Intent(this, Settings.class);

        startActivity(nextActivity);
        return super.onOptionsItemSelected(item);
    }

        public void save(View view) {
        SharedPreferences settings = getSharedPreferences(SETTINGS_PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putStringSet(NO_ALERT_DATES_PREFERENCE_KEY, new HashSet<String>(datesToSave));
        editor.apply();
        Toast.makeText(Settings.this, "Saved Successfully", Toast.LENGTH_LONG).show();

        }
}