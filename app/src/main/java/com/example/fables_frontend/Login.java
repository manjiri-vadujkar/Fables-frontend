package com.example.fables_frontend;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    EditText email;
    EditText password;
    RequestQueue queue;
    String url;
    JSONObject loginObj;
    Intent loginIntent;
    static String token = null;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialise elements
        email = findViewById(R.id.username);
        password = findViewById(R.id.password);

        //Call functions
        setupVolley();
    }

    public void setupVolley() {
        queue = Volley.newRequestQueue(this);
        url = "http://10.0.2.2:4000/api/auth/login"; //replace localhost with 10.0.2.2
    }

    public void createJsonObject() {
        try{
            loginObj = new JSONObject();
            loginObj.put("email", email.getText());
            loginObj.put("password", password.getText());
            //Log.i("JSONObj", String.valueOf(loginObj));
        } catch (JSONException e) {
            Log.i("Error", String.valueOf(e));
        }
    }

    public void login(View view) {
        createJsonObject();
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, loginObj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Success", "POST successfull!!");
                        try {
                            token = response.getJSONObject("data").getString("token");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.i("shareToken", shareToken());
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("token", token);
                        editor.apply();
                        loginIntent = new Intent(getApplicationContext(), MainActivity.class);
                        //loginIntent.putExtra("token", true);
                        startActivity(loginIntent);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("error", String.valueOf(error));
            }
        });
        queue.add(jsonObjectRequest);
    }

    public static String shareToken(){
        return token;
    }

    //Guide new user to register activity
    public void registerActivity(View view) {
        startActivity(new Intent(getApplicationContext(), Register.class));
    }
}