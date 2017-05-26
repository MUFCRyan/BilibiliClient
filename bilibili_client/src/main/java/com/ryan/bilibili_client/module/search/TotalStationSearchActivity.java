package com.ryan.bilibili_client.module.search;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ryan.bilibili_client.R;

public class TotalStationSearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total_station_search);
    }

    public static void launch(Activity activity, String query){

    }
}
