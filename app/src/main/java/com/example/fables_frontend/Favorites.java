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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.fables_frontend.Login.MY_PREFS_NAME;

public class Favorites extends AppCompatActivity {

    BottomNavigationView navView;
    ListView fList;
    RequestQueue queue;
    String url;
    ArrayList<String> favList;
    ArrayList<Integer> bookIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //Initialise elements
        navView = findViewById(R.id.nav_view);
        fList = findViewById(R.id.favoritesListView);

        //Call functions
        navigation();
        setupVolley();
        displayFavoritesList();
    }

    public void setupVolley() {
        queue = Volley.newRequestQueue(this);
        url = "http://ec2-65-0-74-93.ap-south-1.compute.amazonaws.com/api/books/fav"; //replace localhost with 10.0.2.2
    }

    public void navigation() {
        //Set Home selected
        navView.setSelectedItemId(R.id.navigation_favorites);

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

    public void displayFavoritesList() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");

        //Books Request
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.i("Response ", response.toString());
                                try {
                                    JSONArray dataArray = response.getJSONArray("data");
                                    //List for ListView
                                    favList = new ArrayList<String>();
                                    bookIds = new ArrayList<Integer>();
                                    for (int i=0;i<dataArray.length();i++){
                                        bookIds.add(dataArray.getJSONObject(i).getInt("bookId"));
                                        favList.add(dataArray.getJSONObject(i).getString("bookname"));
                                    }

                                    //Adapter to render the arrayList into the ListView
                                    ArrayAdapter<String> favAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_black_text, R.id.list_content, favList);

                                    //set the adapter to listView
                                    fList.setAdapter(favAdapter);

                                    //OnClick Listener
                                    fList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> genreAdapter, View view, int position, long id) {
                                            //in our onItemClick method int position specifies the position of item clicked thus using that we can "get" an array item from that position
                                            Intent intent = new Intent(getApplicationContext(), SelectedBook.class);
                                            intent.putExtra("selectedBook", favList.get(position).toString());
                                            intent.putExtra("bookId", bookIds.get(position));
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
                            Toast.makeText(getApplicationContext(), "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
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