package com.example.thinnie_weight_loss;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.CalendarView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.timessquare.CalendarPickerView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Settings extends AppCompatActivity {

    public static final String NO_ALERT_DATES_PREFERENCE_KEY = "NO_ALERT_DATES";
    public static final String SETTINGS_PREFERENCE_NAME = "SETTINGS";

    private static final String DATE_FORMAT = "y/M/d";
    private static final SimpleDateFormat dateFormatter = new SimpleDateFormat(DATE_FORMAT);

    private CalendarPickerView calendar;
    private Set<String> datesToSave;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //read id from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedName, 0);
        id = sharedPreferences.getString(MainActivity.ID, null);

        calendar = findViewById(R.id.calendarView);

        Calendar nextTwoMonths = Calendar.getInstance();
        nextTwoMonths.add(Calendar.MONTH, 2);

        calendar.init(new Date(), nextTwoMonths.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE);

//        highlight already selected dates in the calendar
        SharedPreferences settings = getSharedPreferences(SETTINGS_PREFERENCE_NAME, Context.MODE_PRIVATE);
        datesToSave = new HashSet<>(settings.getStringSet(NO_ALERT_DATES_PREFERENCE_KEY, new HashSet<String>()));
        String todayStr = dateFormatter.format(new Date());

        for (String dateStr : datesToSave) {
            try {
                Date currentDate = dateFormatter.parse(dateStr);
                String currentDateStr = dateFormatter.format(currentDate);

                if (currentDate.after(new Date()) || currentDateStr.equals(todayStr)) {
                    calendar.selectDate(currentDate);
                }

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


        String url = "https://talez.mtacloud.co.il/includes/app/insert_dates.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("response");

                    Toast.makeText(Settings.this, "Update request result: " + result, Toast.LENGTH_SHORT). show();

                } catch (JSONException e) {
                    Toast.makeText(Settings.this, "Failed to parse first response", Toast.LENGTH_SHORT). show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(Settings.this, "Failed to insert", Toast.LENGTH_SHORT). show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Id", id);
                params.put("dates", TextUtils.join(",",datesToSave));
                return params;
            }
        };
        queue.add(request);


        }

}