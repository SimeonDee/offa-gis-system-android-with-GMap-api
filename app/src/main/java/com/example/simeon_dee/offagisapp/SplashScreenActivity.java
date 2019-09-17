package com.example.simeon_dee.offagisapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
    }

    public void onViewMapActivityClickHandler(View view) {
        Intent intent = new Intent(this, MainMapsActivity.class);
        startActivity(intent);
    }
}
