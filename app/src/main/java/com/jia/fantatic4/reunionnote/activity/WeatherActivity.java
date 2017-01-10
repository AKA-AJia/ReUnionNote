package com.jia.fantatic4.reunionnote.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.jia.fantatic4.reunionnote.R;

/**
 * Created by jia on 2017/1/3.
 */

public class WeatherActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (prefs.getString("weather",null)!=null){
            Intent intent=new Intent(WeatherActivity.this,WeatherDetailsActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
