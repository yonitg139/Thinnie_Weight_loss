package com.example.thinnie_weight_loss;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.browse.MediaBrowser;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final SimpleDateFormat uiDateFormatter = new SimpleDateFormat("y/M/d");
    private static final SimpleDateFormat serverDateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    EditText weight_value;
    double first_weight_value;
    int loss_value;
    int percent_value;
    String last_time_value;

    String id;
    String name;
    TextView firstWeight;
    TextView lastTime;
    TextView percent;
    TextView loss;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //Read from shared preferences
        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.sharedName, 0);
        id = sharedPreferences.getString(MainActivity.ID, null);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        firstWeight = findViewById(R.id.firstWeight);
        loss = findViewById(R.id.weightLoss);
        percent = findViewById(R.id.weightPercent);
        lastTime = findViewById(R.id.LastTime);
        weight_value = findViewById(R.id.CurWeight);


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


    public void sendWeight(View view) {

        hideKeyboard();

        //if a weight value was inserted -> save weight and query for other params and show weight data
        if (weight_value.getText()!=null) {

            //save weight and check if exception
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
                    Toast.makeText(LoginActivity.this, "Failed to save", Toast.LENGTH_SHORT). show();
                }
            })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params = new HashMap<>();
                    params.put("Id", id);
                    params.put("weight", weight_value.getText().toString());
                    return params;
                }
            };
            queue.add(request);

            //query for other prams and show them
            String url2 = "https://talez.mtacloud.co.il/includes/app/getInitialData.php";
            RequestQueue queue2 = Volley.newRequestQueue(this);

            StringRequest request2 = new StringRequest(Request.Method.POST, url2, new Response.Listener<String>() {
                @Override
                public void onResponse(String response2) {
                    try {
                        JSONObject jsonObject = new JSONObject(response2);
                        String result2 = jsonObject.getString("response");

                        if (result2.equals("ok")) {
                            first_weight_value = Double.parseDouble(jsonObject.getString("first_weight"));
                            last_time_value = jsonObject.getString("last_ts");

                            double weightDouble = Double.parseDouble(weight_value.getText().toString());
                            percent_value = (int) Math.ceil((weightDouble / first_weight_value * 100));
                            loss_value = (int) Math.ceil(first_weight_value - weightDouble);

                            //show values once without saving in DB
                            lastTime.setText(uiDateFormatter.format(serverDateFormatter.parse(String.valueOf(last_time_value))));
                            percent.setText(String.valueOf(percent_value));

                            if (loss_value < 0) {
                                loss_value *= -1;
                                TextView youLostView = findViewById(R.id.loss);
                                youLostView.setText("You gained:");
                            }

                            loss.setText(String.valueOf(loss_value));
                            firstWeight.setText(String.valueOf(first_weight_value));

                        }

                    } catch (JSONException e) {
                        Toast.makeText(LoginActivity.this, "Json failure", Toast.LENGTH_SHORT).show();
                    } catch (ParseException e) {
                        Toast.makeText(LoginActivity.this, "Failed to parse server response date", Toast.LENGTH_SHORT).show();
                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(LoginActivity.this, "Failed to query", Toast.LENGTH_SHORT).show();
                }
            }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    HashMap<String, String> params2 = new HashMap<>();
                    params2.put("Id", id);
                    return params2;
                }
            };
            queue2.add(request2);
        }

    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(this);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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