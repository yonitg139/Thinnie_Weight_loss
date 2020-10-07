package com.example.thinnie_weight_loss;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;


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


public class MainActivity extends AppCompatActivity {

    public static final String LOGGED_IN_PREFERENCE = "loggedInPreference";
    public static final String LOGGED_IN_KEY = "loggedInKey";

    public static final String SHARED_NAME = "shared_file";
    public final static String ID = "id";
    private EditText username;
    private EditText password;
    CheckBox loginState;
    Button login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(LOGGED_IN_PREFERENCE, MODE_PRIVATE);
        boolean loggedIn = prefs.getBoolean(LOGGED_IN_KEY, false);
        if(loggedIn) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        username = findViewById(R.id.userName);
        password = findViewById(R.id.Password);
        loginState = findViewById(R.id.remmeber);
        login = findViewById(R.id.login);
    }

    public void login(View view) {
        String url = "https://talez.mtacloud.co.il/includes/app/user_login.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        final String inputUsername = username.getText().toString();
        final String inputPassword = password.getText().toString();
        final boolean inputRemember = loginState.isChecked();

        if (inputUsername.equals("") || inputPassword.equals("")) {
            Toast.makeText(MainActivity.this, "Bad Input", Toast.LENGTH_SHORT). show();
            return;
        }

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("response");


                    if (result.equals("ok")){
                        String Id = jsonObject.getString("id");
                        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_NAME, 0);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(ID, Id);
                        editor.commit();

                        SharedPreferences loggedInPreference = getSharedPreferences(LOGGED_IN_PREFERENCE, 0);
                        SharedPreferences.Editor loggedInEditor = loggedInPreference.edit();
                        loggedInEditor.putBoolean(LOGGED_IN_KEY, inputRemember);
                        loggedInEditor.commit();

                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                        startActivity(intent);
                    }
                    else if (result.equals("nouser")){
                        Toast.makeText(MainActivity.this, "Wrong User", Toast.LENGTH_SHORT). show();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Wrong Password", Toast.LENGTH_SHORT). show();
                    }

                } catch (JSONException e) {
                    Toast.makeText(MainActivity.this, "Failed to parse response", Toast.LENGTH_SHORT). show();
                }

            }
        }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this, "Failed to connect", Toast.LENGTH_SHORT). show();
                }
        })
        {
            @Override
            protected Map<String, String> getParams() {
                HashMap<String, String> params = new HashMap<>();
                params.put("username", username.getText().toString());
                params.put("password", password.getText().toString());
                return params;
            }
        };
        queue.add(request);
    }
}