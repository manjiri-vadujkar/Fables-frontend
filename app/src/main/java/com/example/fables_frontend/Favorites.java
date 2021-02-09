package com.example.fables_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

public class Favorites extends AppCompatActivity {

    BottomNavigationView navView;
    ListView fList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        //Initialise elements
        navView = findViewById(R.id.nav_view);
        fList = findViewById(R.id.favoritesListView);

        //Call functions
        navigation();
        favoritesList();
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

    public void favoritesList() {
        //List for ListView
        ArrayList<String> favoritesArray = new ArrayList<>();
        favoritesArray.add("Adventures of Pinnochio");
        favoritesArray.add("Alice's Adventures");
        favoritesArray.add("Stories of Sherlock Holmes");
        favoritesArray.add("The Breaking Dawn");
        favoritesArray.add("Supernatural");

        //Adapter to render the arrayList into the ListView
        ArrayAdapter<String> favoritesAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.select_dialog_item, favoritesArray);

        //set the adapter to listView
        fList.setAdapter(favoritesAdapter);

        //if clicked on item
        fList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> favoritesAdapter, View view, int position, long id) {
                //in our onItemClick method int position specifies the position of item clicked thus using that we can "get" an array item from that position
                Toast.makeText(getApplicationContext(), favoritesArray.get(position).toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}