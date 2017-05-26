package com.ryan.bilibili_client.module.common;

import android.content.Intent;
import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.PreferenceUtil;
import com.ryan.bilibili_client.utils.SystemUiVisibilityUtil;
import com.trello.rxlifecycle.components.RxActivity;

import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class SplashActivity extends RxActivity {
    private Unbinder mBind;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mBind = ButterKnife.bind(this);
        SystemUiVisibilityUtil.hideStatusBar(getWindow(), true);
        setUpSplash();
    }

    private void setUpSplash() {
        Observable.timer(2000, TimeUnit.MILLISECONDS)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> {finishTask();});
    }

    private void finishTask() {
        boolean isLogin = PreferenceUtil.getBoolean(ConstantUtil.KEY, false);
        if (isLogin) {
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
        } else {
            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
        }

        SplashActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBind.unbind();
    }
}
