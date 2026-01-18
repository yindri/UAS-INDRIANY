package com.example.perpustakaan;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class LanguageActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);

        Button btnIndo = findViewById(R.id.btn_indo);
        Button btnEng = findViewById(R.id.btn_eng);

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Determine language (just for show or storage)
                String lang = (v.getId() == R.id.btn_indo) ? "id" : "en";
                saveLanguage(lang);

                Intent intent = new Intent(LanguageActivity.this, GreetingActivity.class);
                startActivity(intent);
                finish();
            }
        };

        btnIndo.setOnClickListener(listener);
        btnEng.setOnClickListener(listener);
    }

    private void saveLanguage(String lang) {
        SharedPreferences prefs = getSharedPreferences("AppSession", MODE_PRIVATE);
        prefs.edit().putString("language", lang).commit();
    }
}
