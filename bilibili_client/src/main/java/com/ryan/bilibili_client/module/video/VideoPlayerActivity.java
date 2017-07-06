package com.ryan.bilibili_client.module.video;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.utils.ConstantUtil;

public class VideoPlayerActivity extends RxBaseActivity {

    public static void launch(Activity activity, int cid, String title) {
        Intent mIntent = new Intent(activity, VideoPlayerActivity.class);
        mIntent.putExtra(ConstantUtil.EXTRA_CID, cid);
        mIntent.putExtra(ConstantUtil.EXTRA_TITLE, title);
        activity.startActivity(mIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {

    }

    @Override
    public void initToolBar() {

    }
}
