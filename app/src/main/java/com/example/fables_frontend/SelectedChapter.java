package com.example.fables_frontend;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.polly.AmazonPollyPresigningClient;
import com.amazonaws.services.polly.model.DescribeVoicesRequest;
import com.amazonaws.services.polly.model.DescribeVoicesResult;
import com.amazonaws.services.polly.model.OutputFormat;
import com.amazonaws.services.polly.model.SynthesizeSpeechPresignRequest;
import com.amazonaws.services.polly.model.Voice;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import static com.example.fables_frontend.Login.MY_PREFS_NAME;

public class SelectedChapter extends AppCompatActivity {

    RequestQueue queue;
    String url;
    TextView activityTitle;
    TextView chapterText;
    BottomNavigationView navView;
    Intent i;
    AmazonPollyPresigningClient client;
    List<Voice> voices;
    URL presignedSynthesizeSpeechUrl;
    MediaPlayer mediaPlayer;
    Button playBtn;
    Button pauseBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_chapter);

        //Initialise element
        navView = findViewById(R.id.nav_view);
        activityTitle = findViewById(R.id.activity_name);
        chapterText = findViewById(R.id.chapterText);
        i = getIntent();
        playBtn = findViewById(R.id.playButton);
        pauseBtn = findViewById(R.id.pauseButton);

        activityTitle.setText(i.getStringExtra("selectedChpt"));

        //Solve network thread exception
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Call functions;
        navigation();
        try {
            setupVolley();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        displayChapter();

        //Polly functions
        initPolly();
        getListOfVoices();

    }

    public void setupVolley() throws UnsupportedEncodingException {
        queue = Volley.newRequestQueue(this);
        url = "http://ec2-65-0-74-93.ap-south-1.compute.amazonaws.com/api/books/"
                + URLEncoder.encode(String.valueOf(i.getIntExtra("sentBookId", 999)), StandardCharsets.UTF_8.toString())
                + "/chapter/"
                + URLEncoder.encode(String.valueOf(i.getStringExtra("selectedChpt")), StandardCharsets.UTF_8.toString()); //replace localhost with 10.0.2.2

        //Log.i("URL", url);
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

    public void displayChapter() {
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String token = prefs.getString("token", "");

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, url, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    chapterText.setText(response.getString("data"));
                                    String chptText = chapterText.getText().toString();
                                    getURL(chptText);
                                    playSpeech();
                                    playBtn.setEnabled(true);
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
                            Toast.makeText(SelectedChapter.this, "Something went wrong. Please try again", Toast.LENGTH_SHORT).show();
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

    public void initPolly() {
        // Cognito pool ID. Pool needs to be unauthenticated pool with
        // Amazon Polly permissions.
        String COGNITO_POOL_ID = "ap-south-1:ac9e6213-c192-489b-a833-3835a6fc6766";

        // Region of Amazon Polly.
        Regions MY_REGION = Regions.AP_SOUTH_1;

        // Initialize the Amazon Cognito credentials provider.
        CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(),
                COGNITO_POOL_ID,
                MY_REGION
        );

        // Create a client that supports generation of presigned URLs.
        client = new AmazonPollyPresigningClient(credentialsProvider);
    }

    public void getListOfVoices() {
        // Create describe voices request.
        DescribeVoicesRequest describeVoicesRequest = new DescribeVoicesRequest();

        // Synchronously ask Amazon Polly to describe available TTS voices;

        DescribeVoicesResult describeVoicesResult = client.describeVoices(describeVoicesRequest);
        voices = describeVoicesResult.getVoices();
    }

    public void getURL(String text){
        // Create speech synthesis request.
        SynthesizeSpeechPresignRequest synthesizeSpeechPresignRequest =
                new SynthesizeSpeechPresignRequest()
                        // Set the text to synthesize.
                        .withText(text)
                        // Select voice for synthesis.
                        .withVoiceId(voices.get(26).getId()) // "Aditi"
                        //language code
                        //.withLanguageCode("hi-IN")
                        // Set format to MP3.
                        .withOutputFormat(OutputFormat.Mp3);
        // Get the presigned URL for synthesized speech audio stream.
        presignedSynthesizeSpeechUrl = client.getPresignedSynthesizeSpeechUrl(synthesizeSpeechPresignRequest);
    }

    public void playSpeech() {
        // Use MediaPlayer: https://developer.android.com/guide/topics/media/mediaplayer.html

        // Create a media player to play the synthesized audio stream.
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            // Set media player's data source to previously obtained URL.
            mediaPlayer.setDataSource(presignedSynthesizeSpeechUrl.toString());
        } catch (IOException e) {
            Log.i( "Unabletosetdatasource" , e.getMessage().toString());
        }

        // Prepare the MediaPlayer asynchronously (since the data source is a network stream).
        mediaPlayer.prepareAsync();

        // Set the callback to start the MediaPlayer when it's prepared.
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp){

            }

        });

        // Set the callback to release the MediaPlayer after playback is completed.
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                Log.i("mp release", "Its released");
                playBtn.setEnabled(false);
                pauseBtn.setEnabled(false);
                mp.release();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mediaPlayer != null && mediaPlayer.isPlaying())
        {
            mediaPlayer.release();
            mediaPlayer = null;
        }else
        {
            Log.i("onBackPressed", "Mediaplayer was not running");
        }
    }

    public void play(View view) {
        mediaPlayer.start();
        playBtn.setEnabled(false);
        pauseBtn.setEnabled(true);
    }

    public void pause(View view) {
        mediaPlayer.pause();
        pauseBtn.setEnabled(false);
        playBtn.setEnabled(true);
    }

}