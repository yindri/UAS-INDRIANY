package com.example.perpustakaan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                checkSession();
            }
        }, 2000); // 2 seconds delay
    }

    private void checkSession() {
        SharedPreferences prefs = getSharedPreferences("AppSession", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);

        Intent intent;
        if (isLoggedIn) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, LoginActivity.class);
        }
        startActivity(intent);
        finish();
    }
}
