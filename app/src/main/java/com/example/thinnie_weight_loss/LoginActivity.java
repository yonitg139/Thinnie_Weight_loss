package com.example.thinnie_weight_loss;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    /// TODO: add textedit for weight and the query for thr first weight value;
    // TODO: think about other params (not BMI or fat percentage)
    Integer weight_value = 80;
    double first_weight = 150;
    double bmi_value = 34.6;
    int loss_value;
    int percent_value;
    int fat_value = 28;

    String id;
    String name;
    TextView fat;
    TextView bmi;
    TextView percent;
    TextView loss;
    TextView weight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Read from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedName, 0);
        id = sharedPreferences.getString(MainActivity.ID, null);
        name = sharedPreferences.getString(MainActivity.USER_NAME, null);
        percent_value = (int)Math.ceil(weight_value/first_weight*100);
        loss_value = (int)Math.ceil(first_weight - weight_value);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        fat = findViewById(R.id.fatData);
        loss = findViewById(R.id.weightLoss);
        percent = findViewById(R.id.weightPercent);
        bmi = findViewById(R.id.BMIData);
        weight = findViewById(R.id.CurWeight);

        //show values once without saving in DB
        weight.setText(weight_value.toString());
        bmi.setText(String.valueOf((int) bmi_value));
        percent.setText(String.valueOf(percent_value));
        loss.setText(String.valueOf(loss_value));
        fat.setText(String.valueOf(fat_value));

        String url = "https://talez.mtacloud.co.il/includes/app/traj_check.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("response");

                    Toast.makeText(LoginActivity.this, "Update request result: " + result, Toast.LENGTH_SHORT). show();

                } catch (JSONException e) {
                    Toast.makeText(LoginActivity.this, "Failed to parse first response", Toast.LENGTH_SHORT). show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(LoginActivity.this, "Failed to update", Toast.LENGTH_SHORT). show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Id", id);
                params.put("weight", weight_value.toString());
                return params;
            }
        };
        queue.add(request);

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, 17);
        startTime.set(Calendar.MINUTE, 0);

        if (startTime.getTime().compareTo(new Date()) < 0) {
            startTime.add(Calendar.DAY_OF_MONTH, 1);
        }

        Intent alarmIntent = new Intent(this, NotificationReceiver.class).setAction("com.example.thinnie_weight_loss.notify");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        int interval = 86400000;
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, startTime.getTimeInMillis(), interval, pendingIntent);
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
}