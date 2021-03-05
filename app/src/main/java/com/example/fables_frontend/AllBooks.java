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

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.fables_frontend.Login.MY_PREFS_NAME;

public class AllBooks extends AppCompatActivity {

    RequestQueue queue;
    String url;
    BottomNavigationView navView;
    ListView allBooksList;
    ArrayList<String> bookArray;
    ArrayList<Integer> bookIds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_books);

        //Initialise all elements
        navView = findViewById(R.id.nav_view);
        allBooksList = (ListView) findViewById(R.id.allBooksListView);

        //Call functions
        try {
            setupVolley();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        navigation();
        displayAllBooks();

    }

    public void setupVolley() throws UnsupportedEncodingException {
        queue = Volley.newRequestQueue(this);
        url = "http://ec2-65-0-74-93.ap-south-1.compute.amazonaws.com/api/books/"; //replace localhost with 10.0.2.2
    }

    public void navigation() {
        //Set selected activity
        navView.setSelectedItemId(R.id.navigation_home);

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

    public void displayAllBooks() {
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
                                    bookArray = new ArrayList<String>();
                                    bookIds = new ArrayList<Integer>();
                                    for (int i=0;i<dataArray.length();i++){
                                        bookIds.add(dataArray.getJSONObject(i).getInt("bookId"));
                                        bookArray.add(dataArray.getJSONObject(i).getString("bookname"));
                                    }

                                    //Adapter to render the arrayList into the ListView
                                    ArrayAdapter<String> bookAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item, bookArray);

                                    //set the adapter to listView
                                    allBooksList.setAdapter(bookAdapter);

                                    //OnClick Listener
                                    allBooksList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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