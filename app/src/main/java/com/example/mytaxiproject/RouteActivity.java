package com.example.mytaxiproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class RouteActivity extends AppCompatActivity {
    SharedPreferences sharedPreferences;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);

        if (sharedPreferences.getString("yourPhoneNumber", "").length() > 0) {
            intent = new Intent(this, SearchActivity.class);
        } else {
            intent = new Intent(this, OpeningActivity.class);
        }

        startActivity(intent);
    }
}
