package com.example.thinnie_weight_loss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
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

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    /// values we'll get from the digital scale
    Integer weight_value;
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
        weight_value = sharedPreferences.getInt(MainActivity.WEIGHT, 0);
        id = sharedPreferences.getString(MainActivity.ID, null);
        name = sharedPreferences.getString(MainActivity.USER_NAME, null);
        percent_value = (int)Math.ceil(weight_value/first_weight*100);
        loss_value = (int)Math.ceil(first_weight - weight_value);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


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
    }
}