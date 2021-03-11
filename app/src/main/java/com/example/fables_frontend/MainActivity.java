package com.example.fables_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import static com.example.fables_frontend.Login.MY_PREFS_NAME;

public class MainActivity extends AppCompatActivity {

    RequestQueue queue;
    String url;
    BottomNavigationView navView;
    //Intent intent;
    Intent loginIntent;
    TextView grid0;
    TextView grid1;
    TextView grid2;
    TextView grid3;
    TextView grid4;
    TextView grid5;
    TextView grid6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialise Navbar
        navView = findViewById(R.id.nav_view);
        loginIntent = getIntent();
        grid0 = findViewById(R.id.grid0);
        grid1 = findViewById(R.id.grid1);
        grid2 = findViewById(R.id.grid2);
        grid3 = findViewById(R.id.grid3);
        grid4 = findViewById(R.id.grid4);
        grid5 = findViewById(R.id.grid5);
        grid6 = findViewById(R.id.grid6);

        //Call functions
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");
        //Log.i("Login token", "token = " + token);
        if(!token.isEmpty()){
            try {
                setupVolley();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            navigation();
            getHomePage();
        }else {
            startActivity(new Intent(getApplicationContext(), Login.class));
        }
    }

    public void setupVolley() throws UnsupportedEncodingException {
        queue = Volley.newRequestQueue(this);
        url = "http://ec2-65-0-74-93.ap-south-1.compute.amazonaws.com/api/books/?limit=" + "7"; //replace localhost with 10.0.2.2
    }

    public void navigation() {
        //Set Home selected
        navView.setSelectedItemId(R.id.navigation_home);

        //perform ItemSelectedListener
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        return true;

                    case R.id.navigation_genre:
                        startActivity(new Intent(getApplicationContext(), Genre.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_recommend:
                        startActivity(new Intent(getApplicationContext(), Recommend.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_favorites:
                        startActivity(new Intent(getApplicationContext(), Favorites.class));
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.navigation_profile:
                        startActivity(new Intent(getApplicationContext(), Profile.class));
                        overridePendingTransition(0,0);
                        return true;
                }
                return false;
            }
        });
    }

    public void getHomePage(){
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");

        //Books Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    grid0.setText(response.getJSONArray("data").getJSONObject(0).getString("bookname"));
                                    grid0.setTag(response.getJSONArray("data").getJSONObject(0).getString("bookId"));

                                    grid1.setText(response.getJSONArray("data").getJSONObject(1).getString("bookname"));
                                    grid1.setTag(response.getJSONArray("data").getJSONObject(1).getString("bookId"));

                                    grid2.setText(response.getJSONArray("data").getJSONObject(2).getString("bookname"));
                                    grid2.setTag(response.getJSONArray("data").getJSONObject(2).getString("bookId"));

                                    grid3.setText(response.getJSONArray("data").getJSONObject(3).getString("bookname"));
                                    grid3.setTag(response.getJSONArray("data").getJSONObject(3).getString("bookId"));

                                    grid4.setText(response.getJSONArray("data").getJSONObject(4).getString("bookname"));
                                    grid4.setTag(response.getJSONArray("data").getJSONObject(4).getString("bookId"));

                                    grid5.setText(response.getJSONArray("data").getJSONObject(5).getString("bookname"));
                                    grid5.setTag(response.getJSONArray("data").getJSONObject(5).getString("bookId"));

                                    grid6.setText(response.getJSONArray("data").getJSONObject(6).getString("bookname"));
                                    grid6.setTag(response.getJSONArray("data").getJSONObject(6).getString("bookId"));

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
                            Log.i("RequestError", error.toString());
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
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

    public void selectBook(View view) {
        TextView clickedBook = (TextView) view;
        int tag = Integer.parseInt(clickedBook.getTag().toString());
        String book = clickedBook.getText().toString();
        Intent intent = new Intent(getApplicationContext(), SelectedBook.class);
        intent.putExtra("selectedBook", book);
        intent.putExtra("bookId", tag);
        startActivity(intent);
    }

    public void viewAllBooks(View view) {
        Intent intent = new Intent(getApplicationContext(), AllBooks.class);
        startActivity(intent);
    }
}