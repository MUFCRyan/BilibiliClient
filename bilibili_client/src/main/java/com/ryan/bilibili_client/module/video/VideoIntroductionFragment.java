package com.ryan.bilibili_client.module.video;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.VideoRelatedAdapter;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.video.VideoDetailsInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.NumberUtil;
import com.ryan.bilibili_client.widget.UserTagView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by MUFCRyan on 2017/6/1.
 * 视频简介界面
 */

public class VideoIntroductionFragment extends RxLazyFragment {
    public static VideoIntroductionFragment newInstance(int aid){
        VideoIntroductionFragment fragment = new VideoIntroductionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtil.EXTRA_AV, aid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @BindView(R.id.tv_title)
    TextView mTitleText;

    @BindView(R.id.tv_play_time)
    TextView mPlayTimeText;

    @BindView(R.id.tv_review_count)
    TextView mReviewCountText;

    @BindView(R.id.tv_description)
    TextView mDescText;

    @BindView(R.id.author_tag)
    UserTagView mAuthorTagView;

    @BindView(R.id.share_num)
    TextView mShareNum;

    @BindView(R.id.coin_num)
    TextView mCoinNum;

    @BindView(R.id.fav_num)
    TextView mFavNum;

    @BindView(R.id.tags_layout)
    TagFlowLayout mTagFlowLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.layout_video_related)
    RelativeLayout mVideoRelatedLayout;

    private int mAv;

    private VideoDetailsInfo.DataBean mVideoDetailsInfo;

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_video_introduction;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mAv = getArguments().getInt(ConstantUtil.EXTRA_AV);
        loadData();
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getBiliAppAPI()
                .getVideoDetails(mAv)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videoDetails -> {
                    mVideoDetailsInfo = videoDetails.getData();
                    finishTask();
                });
    }

    @Override
    protected void finishTask() {
        // 设置视频标题
        mTitleText.setText(mVideoDetailsInfo.getTitle());
        // 设置视频播放数量
        mPlayTimeText.setText(NumberUtil.converString(mVideoDetailsInfo.getStat().getView()));
        // 设置视频弹幕数量
        mReviewCountText.setText(NumberUtil.converString(mVideoDetailsInfo.getStat().getDanmaku()));
        // 设置 Up 主信息
        mDescText.setText(mVideoDetailsInfo.getDesc());
        mAuthorTagView.setUpWithInfo(getActivity(), mVideoDetailsInfo.getOwner().getName(),
                mVideoDetailsInfo.getOwner().getMid(), mVideoDetailsInfo.getOwner().getFace());
        // 设置分享、收藏、投币数量
        mShareNum.setText(NumberUtil.converString(mVideoDetailsInfo.getStat().getShare()));
        mFavNum.setText(NumberUtil.converString(mVideoDetailsInfo.getStat().getFavorite()));
        mCoinNum.setText(NumberUtil.converString(mVideoDetailsInfo.getStat().getCoin()));
        // 设置视频 Tags
        setVideoTags();
        // 设置视频相关
        setVideoRelated();
    }

    private void setVideoTags() {
        List<String> tags = mVideoDetailsInfo.getTags();
        mTagFlowLayout.setAdapter(new TagAdapter<String>(tags) {
            @Override
            public View getView(FlowLayout parent, int position, String tag) {
                TextView tagView = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_tags_item, parent, false);
                tagView.setText(tag);
                return tagView;
            }
        });
    }

    private void setVideoRelated() {
        List<VideoDetailsInfo.DataBean.RelatesBean> relates = mVideoDetailsInfo.getRelates();
        if (relates == null){
            mVideoRelatedLayout.setVisibility(View.GONE);
            return;
        }
        VideoRelatedAdapter relatedAdapter = new VideoRelatedAdapter(mRecyclerView, relates);
        mRecyclerView.setHasFixedSize(false);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, true));
        mRecyclerView.setAdapter(relatedAdapter);
        relatedAdapter.setOnItemClickListener((position, holder) -> VideoDetailsActivity.launch(getActivity(), relates.get(position).getAid(), relates.get(position).getPic()));
    }

    @OnClick(R.id.btn_share)
    void share(){
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
        intent.putExtra(Intent.EXTRA_TEXT, "来自「哔哩哔哩」的分享:" + mVideoDetailsInfo.getDesc());
        startActivity(Intent.createChooser(intent, mVideoDetailsInfo.getTitle()));
    }
}
