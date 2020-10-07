package com.example.thinnie_weight_loss;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class History extends AppCompatActivity {

    String id;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        getSupportActionBar().setDisplayShowTitleEnabled(false);

        SharedPreferences sharedPreferences = getSharedPreferences(MainActivity.SHARED_NAME, 0);
        id = sharedPreferences.getString(MainActivity.ID, null);
        recyclerView = findViewById(R.id.weight_list);

        String url = "https://talez.mtacloud.co.il/includes/app/weights_history.php";
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    String result = jsonObject.getString("result");
                    JSONArray jsonArray = new JSONArray(result);

                    WeightAdapter weightAdapter;
                    weightAdapter = new WeightAdapter(History.this, jsonArray);
                    recyclerView.setAdapter(weightAdapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(History.this));
                } catch (JSONException e) {
                    Toast.makeText(History.this, "Failed to parse first response", Toast.LENGTH_SHORT). show();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(History.this, "Failed to query", Toast.LENGTH_SHORT). show();
            }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("Id", id);
                return params;
            }
        };
        queue.add(request);

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