package com.ryan.bilibili_client.module.home.bangumi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.utils.ConstantUtil;

public class BangumiDetailsActivity extends RxBaseActivity {

    public static void launch(Activity activity, int seasonId){
        Intent mIntent = new Intent(activity, BangumiDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtil.EXTRA_BANGUMI_KEY, seasonId);
        mIntent.putExtras(bundle);
        activity.startActivity(mIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bangumi_details;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initToolBar() {

    }
}
