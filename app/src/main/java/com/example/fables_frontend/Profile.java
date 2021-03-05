package com.example.fables_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.example.fables_frontend.Login.MY_PREFS_NAME;

public class Profile extends AppCompatActivity {

    RequestQueue queue;
    String url;
    BottomNavigationView navView;
    TextView name;
    TextView email;
    TextView subs;
    Button renewSubs;
    Button logout;
    String fetchedName;
    String fetchedEmail;
    String fetchedSubs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Initialise elements
        navView = findViewById(R.id.nav_view);
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        subs = findViewById(R.id.subscriptionStatus);
        renewSubs = findViewById(R.id.subscribe);
        logout = findViewById(R.id.logout);

        //Call functions
        setupVolley();
        navigation();
        displayProfile();
    }

    public void setupVolley() {
        queue = Volley.newRequestQueue(this);
        url = "http://ec2-65-0-74-93.ap-south-1.compute.amazonaws.com/api/user/"; //replace localhost with 10.0.2.2
    }

    public void navigation() {
        //Set Home selected
        navView.setSelectedItemId(R.id.navigation_profile);

        //perform ItemSelectedListener
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.navigation_genre:
                        startActivity(new Intent(getApplicationContext(), Genre.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.navigation_recommend:
                        startActivity(new Intent(getApplicationContext(), Recommend.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.navigation_favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        overridePendingTransition(0, 0);
                        return true;

                    case R.id.navigation_profile:
                        return true;
                }
                return false;
            }
        });
    }

    public void displayProfile() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");
        //Log.i("Profile page", "token = " + token);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            fetchedName = response.getJSONObject("data").getString("name");
                            fetchedEmail = response.getJSONObject("data").getString("email");
                            fetchedSubs = response.getJSONObject("data").getString("subscription");
                            if(fetchedSubs.equals("0")){
                                fetchedSubs = "Inactive";
                            }
                            else {
                                fetchedSubs = "Active";
                            }
                            fetchedName = "Name: " + fetchedName;
                            fetchedEmail = "Usermail: " + fetchedEmail;
                            fetchedSubs = "Subscription Status: " + fetchedSubs;
                            name.setText(fetchedName);
                            email.setText(fetchedEmail);
                            subs.setText(fetchedSubs);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                        Log.i("Error: ", error.toString());
                        if(error.toString().equals("com.android.volley.AuthFailureError")){
                            logout();
                        }
                        else {
                            Toast.makeText(Profile.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
        {
            @Override
            public Map getHeaders() throws AuthFailureError {
                String headerToken = "Bearer "+ token;
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", headerToken);
                return headers;
            }
        };
        queue.add(jsonObjectRequest);
    }

    public void logout() {
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString("token", "");
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    public void onLogoutClick(View view) {
        logout();
    }

    public void renewSubs() {

    }
}