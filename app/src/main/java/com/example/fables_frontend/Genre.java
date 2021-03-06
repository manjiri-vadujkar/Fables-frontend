package com.example.fables_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Genre extends AppCompatActivity {

    BottomNavigationView navView;
    ListView gList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_genre);

        //All elements
        navView = findViewById(R.id.nav_view);
        gList = findViewById(R.id.genreListView);

        //Calling functions
        navigation();
        genreList();
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

    public void genreList() {
        //List for ListView
        ArrayList<String> genreArray = new ArrayList<>();
        genreArray.add("Children");
        genreArray.add("Crime & Mystery");
        genreArray.add("Adventure");
        genreArray.add("Science Fiction");
        genreArray.add("Horror");

        //Adapter to render the arrayList into the ListView
        ArrayAdapter<String> genreAdapter = new ArrayAdapter<>(getApplicationContext(), R.layout.list_black_text, R.id.list_content, genreArray);

        //set the adapter to listView
        gList.setAdapter(genreAdapter);

        //if clicked on item
        gList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> genreAdapter, View view, int position, long id) {
                //in our onItemClick method int position specifies the position of item clicked thus using that we can "get" an array item from that position
                Intent intent = new Intent(getApplicationContext(), SelectedGenre.class);
                intent.putExtra("selectedGenre", genreArray.get(position));
                startActivity(intent);
                overridePendingTransition(0,0);
            }
        });
    }
}