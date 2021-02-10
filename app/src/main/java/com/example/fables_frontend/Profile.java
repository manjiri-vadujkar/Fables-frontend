package com.example.fables_frontend;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Profile extends AppCompatActivity {

    BottomNavigationView navView;
    TextView name;
    TextView email;
    TextView subs;
    Button renewSubs;
    Button logout;

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
        navigation();
        displayProfile();
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
        // Concatenate values from "get" API to foll. strings
        String username = "Name: ";
        String usermail = "Usermail: ";
        String subsStatus = "Subscription Status: ";

        //Set values to textfields
        name.setText(username);
        email.setText(usermail);
        subs.setText(subsStatus);
    }

    public void logout(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
    }

    public void renewSubs() {

    }
}