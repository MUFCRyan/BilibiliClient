package com.ryan.bilibili_client.module.video;

import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.VideoHotCommentAdapter;
import com.ryan.bilibili_client.adapter.helper.HeaderViewRecyclerAdapter;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.video.VideoCommentInfo;
import com.ryan.bilibili_client.utils.ConstantUtil;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by MUFCRyan on 2017/6/1.
 * 视频评论界面
 */

public class VideoCommentFragment extends RxLazyFragment {

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    private ArrayList<VideoCommentInfo.List> mComments = new ArrayList<>();
    private ArrayList<VideoCommentInfo.HotList> mHotComments = new ArrayList<>();
    private HeaderViewRecyclerAdapter mAdapter;
    private int mPageNum = 1;
    private int mPageSize = 20;
    private View mLoadMoreView;
    private int mAid;
    private VideoHotCommentAdapter mVideoHotCommentAdapter;
    private View mHeadView;

    public static VideoCommentFragment newInstance(int aid){
        VideoCommentFragment fragment = new VideoCommentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtil.AID, aid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_video_comment;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {

    }
}
