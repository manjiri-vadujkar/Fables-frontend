package com.example.fables_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.fables_frontend.Login.MY_PREFS_NAME;

public class SelectedBook extends AppCompatActivity {

    RequestQueue queue;
    String url;
    BottomNavigationView navView;
    TextView activityTitle;
    TextView authorName;
    TextView genre;
    TextView summary;
    Intent i;
    ArrayList<String> chapterArray;
    ListView chapterListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_book);

        //Initialise elements
        navView = findViewById(R.id.nav_view);
        activityTitle = findViewById(R.id.activity_name);
        chapterListView = findViewById(R.id.chapterListView);
        authorName = findViewById(R.id.authorName);
        genre = findViewById(R.id.genreName);
        summary = findViewById(R.id.summary);
        i = getIntent();

        activityTitle.setText(i.getStringExtra("selectedBook"));

        //Call functions
        navigation();
        try {
            setupVolley();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        bookDetails();
    }

    public void setupVolley() throws UnsupportedEncodingException {
        queue = Volley.newRequestQueue(this);
        url = "http://10.0.2.2:4000/api/books/" + URLEncoder.encode(String.valueOf(i.getIntExtra("bookId", 999)), StandardCharsets.UTF_8.toString()); //replace localhost with 10.0.2.2
    }

    public void navigation() {
        //Set selected activity
        navView.setSelectedItemId(R.id.navigation_genre);

        //perform ItemSelectedListener
        navView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.navigation_home:
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        overridePendingTransition(0,0);
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

    public void bookDetails() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");

        //Books Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("Response ", response.toString());
                                chapterArray = new ArrayList<String>();
                                try {
                                    JSONObject data = response.getJSONObject("data");

                                    authorName.setText("Author: " + data.getString("author"));
                                    genre.setText("Genre: " + data.getString("genre"));
                                    summary.setText(response.getJSONObject("data").getString("summary"));

                                    for(int i=0;i<data.getJSONArray("chapters").length();i++) {
                                        chapterArray.add(data.getJSONArray("chapters").getString(i));
                                    }

                                    ArrayAdapter<String> chapterAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.select_dialog_item, chapterArray);

                                    //set the adapter to listView
                                    chapterListView.setAdapter(chapterAdapter);

                                    //onClick Listener
                                    chapterListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            //Toast.makeText(getApplicationContext(), chapterArray.get(position), Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(getApplicationContext(), SelectedChapter.class);
                                            intent.putExtra("sentBookId", i.getIntExtra("bookId", 999));
                                            intent.putExtra("selectedChpt", chapterArray.get(position));
                                            startActivity(intent);
                                        }
                                    });

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
                            Toast.makeText(SelectedBook.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
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
}