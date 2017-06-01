package com.ryan.bilibili_client.module.video;


import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;

/**
 * Created by MUFCRyan on 2017/6/1.
 * 视频简介界面
 */

public class VideoIntroductionFragment extends RxLazyFragment {
    public static VideoIntroductionFragment newInstance(){
        return new VideoIntroductionFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_video_introduction;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {

    }
}
