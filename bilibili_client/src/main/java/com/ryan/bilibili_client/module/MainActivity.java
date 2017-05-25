package com.ryan.bilibili_client.module;

import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.trello.rxlifecycle.components.RxActivity;

public class MainActivity extends RxActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
