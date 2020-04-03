package com.weather.weatherforecast;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;

public class MainActivity extends AppCompatActivity {
    private final int SPLASH_DISPLAY_LENGTH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                    Intent mainIntent = new Intent(MainActivity.this, SearchWeatherActivity.class);
                    startActivity(mainIntent);
                    finish();
                }
        }, SPLASH_DISPLAY_LENGTH);
    }
}
