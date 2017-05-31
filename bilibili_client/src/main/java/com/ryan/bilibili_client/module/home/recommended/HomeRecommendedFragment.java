package com.ryan.bilibili_client.module.home.recommended;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.recommend.RecommendBannerInfo;
import com.ryan.bilibili_client.entity.recommend.RecommendInfo;
import com.ryan.bilibili_client.widget.CustomEmptyView;
import com.ryan.bilibili_client.widget.banner.BannerEntity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by MUFCRyan on 2017/5/31.
 *
 */

public class HomeRecommendedFragment extends RxLazyFragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    CustomEmptyView mEmptyView;

    private List<RecommendInfo.ResultBean> mResults = new ArrayList<>();
    private List<BannerEntity> mBanners = new ArrayList<>();
    private List<RecommendBannerInfo.DataBean> mRecommendedBanners = new ArrayList<>();
    private boolean mRefreshing = false;


    public static HomeRecommendedFragment newInstance(){
        return new HomeRecommendedFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_recommended;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mPrepared = true;
        lazyLoad();
    }
}
