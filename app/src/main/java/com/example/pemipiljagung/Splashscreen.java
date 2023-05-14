package com.example.pemipiljagung;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Splashscreen extends AppCompatActivity {

    private static final int SPLASH_TIME_OUT = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // method ini akan di eksekusi setelah timer SELESAI

                // start Main activity
                Intent i = new Intent(Splashscreen.this, MainActivity.class);

                startActivity(i);
                // tutup activity ini
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}