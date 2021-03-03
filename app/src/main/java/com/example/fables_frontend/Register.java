package com.example.fables_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Register extends AppCompatActivity {

    EditText name;
    EditText email;
    EditText password;
    RequestQueue queue;
    String url;
    JSONObject registerObj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initialise elements
        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);

        //Call functions
        setupVolley();
    }

    public void setupVolley() {
        queue = Volley.newRequestQueue(this);
        url = "http://10.0.2.2:4000/api/auth/register"; //replace localhost with 10.0.2.2
    }

    public void createJsonObject() {
        try{
            registerObj = new JSONObject();
            registerObj.put("name", name.getText());
            registerObj.put("email", email.getText());
            registerObj.put("password", password.getText());
        } catch (JSONException e) {
            Log.i("Error", String.valueOf(e));
        }
    }

    public void register(View view) {
        createJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, registerObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Success", "Registeration successfull!!");
                        startActivity(new Intent(getApplicationContext(), Login.class));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error", String.valueOf(error));
            }
        });
        queue.add(jsonObjectRequest);
    }

    //Guide already registered user to login activity
    public void loginActivity(View view) {
        startActivity(new Intent(getApplicationContext(), Login.class));
    }
}