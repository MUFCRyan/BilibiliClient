package com.ryan.bilibili_client.module.video;

import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;

/**
 * Created by MUFCRyan on 2017/6/1.
 * 视频评论界面
 */

public class VideoCommentFragment extends RxLazyFragment {

    public static VideoCommentFragment newInstance(){
        return new VideoCommentFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_video_comment;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {

    }
}
