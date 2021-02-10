package com.example.fables_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Login extends AppCompatActivity {

    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialise elements
        login = findViewById(R.id.login);
    }

    public void login(View view) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
    }

    //Guide new user to register activity
    public void registerActivity(View view) {
        startActivity(new Intent(getApplicationContext(), Register.class));
    }
}