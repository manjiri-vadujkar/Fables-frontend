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

public class SelectedGenre extends AppCompatActivity {

    RequestQueue queue;
    String url;
    BottomNavigationView navView;
    ListView selectedGList;
    TextView activityTitle;
    Intent i;
    ArrayList<String> bookArray;
    ArrayList<Integer> bookIds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_genre);

        //Initialise all elements
        navView = findViewById(R.id.nav_view);
        selectedGList = findViewById(R.id.selectedGenreListView);
        activityTitle = findViewById(R.id.activity_name);
        i = getIntent();

        //activity name
        activityTitle.setText(i.getStringExtra("selectedGenre"));

        //Call functions
        try {
            setupVolley();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        navigation();
        selectedGenreList();
    }

    public void setupVolley() throws UnsupportedEncodingException {
        queue = Volley.newRequestQueue(this);
        url = "http://ec2-65-0-74-93.ap-south-1.compute.amazonaws.com/api/books/?genre=" + URLEncoder.encode(i.getStringExtra("selectedGenre"), StandardCharsets.UTF_8.toString()); //replace localhost with 10.0.2.2
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

    public void selectedGenreList() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");

        //Books Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    //List for ListView
                                    bookArray = new ArrayList<>();
                                    bookIds = new ArrayList<Integer>();
                                    for (int i=0;i<dataArray.length();i++){
                                        bookIds.add(dataArray.getJSONObject(i).getInt("bookId"));
                                        bookArray.add(dataArray.getJSONObject(i).getString("bookname"));
                                    }

                                    //Adapter to render the arrayList into the ListView
                                    ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_black_text, R.id.list_content, bookArray);

                                    //set the adapter to listView
                                    selectedGList.setAdapter(bookAdapter);

                                    //OnClick Listener
                                    selectedGList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> genreAdapter, View view, int position, long id) {
                                            //in our onItemClick method int position specifies the position of item clicked thus using that we can "get" an array item from that position
                                            Intent intent = new Intent(getApplicationContext(), SelectedBook.class);
                                            intent.putExtra("selectedBook", bookArray.get(position).toString());
                                            intent.putExtra("bookId", bookIds.get(position));
                                            startActivity(intent);
                                            //Toast.makeText(getApplicationContext(), bookArray.get(position).toString(), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(SelectedGenre.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
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