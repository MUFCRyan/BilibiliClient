package com.ryan.bilibili_client.module.video;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.VideoCommentAdapter;
import com.ryan.bilibili_client.adapter.VideoHotCommentAdapter;
import com.ryan.bilibili_client.adapter.helper.HeaderViewRecyclerAdapter;
import com.ryan.bilibili_client.adapter.pager.EndlessRecyclerOnScrollListener;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.video.VideoCommentInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;

import java.util.ArrayList;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        mAid = getArguments().getInt(ConstantUtil.AID);
        initRecyclerView();
        loadData();
    }

    @Override
    protected void initRecyclerView() {
        VideoCommentAdapter adapter = new VideoCommentAdapter(mRecyclerView, mComments);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mAdapter = new HeaderViewRecyclerAdapter(adapter);
        mRecyclerView.setAdapter(mAdapter);
        createHeadView();
        createLoadMoreView();
        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(manager) {
            @Override
            public void onLoadMore(int currentPage) {
                mPageNum ++;
                loadData();
                mLoadMoreView.setVisibility(View.VISIBLE);
            }
        });
    }

    private void createHeadView() {
        mHeadView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_video_hot_comment_head, mRecyclerView, false);
        RecyclerView recyclerView = (RecyclerView) mHeadView.findViewById(R.id.hot_comment_recycler);
        recyclerView.setHasFixedSize(false);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mVideoHotCommentAdapter = new VideoHotCommentAdapter(recyclerView, mHotComments);
        recyclerView.setAdapter(mVideoHotCommentAdapter);
        mAdapter.addHeaderView(recyclerView);
    }

    private void createLoadMoreView() {
        mLoadMoreView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_load_more, mRecyclerView, false);
        mAdapter.addFooterView(mLoadMoreView);
        mLoadMoreView.setVisibility(View.GONE);
    }

    @Override
    protected void loadData() {
        int ver = 3;
        RetrofitHelper.getBiliAPI()
                .getVideoComment(mAid, mPageNum, mPageSize, ver)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(videoCommentInfo -> {
                    ArrayList<VideoCommentInfo.List> list = videoCommentInfo.list;
                    ArrayList<VideoCommentInfo.HotList> hotList = videoCommentInfo.hotList;
                    if (list.size() < mPageSize){
                        mLoadMoreView.setVisibility(View.GONE);
                        mAdapter.removeFootView();
                    }
                    mComments.addAll(list);
                    mHotComments.addAll(hotList);
                    finishTask();
                }, throwable -> {
                    mLoadMoreView.setVisibility(View.GONE);
                    mHeadView.setVisibility(View.GONE);
                });
    }

    @Override
    protected void finishTask() {
        mLoadMoreView.setVisibility(View.GONE);
        mVideoHotCommentAdapter.notifyDataSetChanged();
        if (mPageNum * mPageSize - mPageSize - 1 > 0)
            mAdapter.notifyItemRangeChanged(mPageNum * mPageSize - mPageSize - 1, mPageNum);
        else
            mAdapter.notifyDataSetChanged();
    }
}
