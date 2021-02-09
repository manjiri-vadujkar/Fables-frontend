package com.example.fables_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Recommend extends AppCompatActivity {

    BottomNavigationView navView;
    ListView rList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);

        //Initialise elements
        navView = findViewById(R.id.nav_view);
        rList = findViewById(R.id.recommendListView);

        //Call functions
        navigation();
        recommendList();
    }

    public void navigation() {
        //Set Home selected
        navView.setSelectedItemId(R.id.navigation_recommend);

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

    public void recommendList() {
        //List for ListView
        ArrayList<String> recommendList = new ArrayList<>();
        recommendList.add("Adventures of Pinocchio");
        recommendList.add("Alice's Adventures");
        recommendList.add("Stories of Sherlock Holmes");
        recommendList.add("The Breaking Dawn");
        recommendList.add("Supernatural");
        recommendList.add("The Lone Wolf");

        //Adapter to render the arrayList into the ListView
        ArrayAdapter<String> recommendAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item, recommendList);

        //set the adapter to listView
        rList.setAdapter(recommendAdapter);

        //if clicked on item
        rList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> recommendAdapter, View view, int position, long id) {
                //in our onItemClick method int position specifies the position of item clicked thus using that we can "get" an array item from that position
                Toast.makeText(getApplicationContext(), recommendList.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}