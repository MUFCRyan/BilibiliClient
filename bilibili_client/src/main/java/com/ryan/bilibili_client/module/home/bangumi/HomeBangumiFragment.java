package com.ryan.bilibili_client.module.home.bangumi;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.bangumi.BangumiAppIndexInfo;
import com.ryan.bilibili_client.entity.bangumi.BangumiRecommendInfo;
import com.ryan.bilibili_client.widget.CustomEmptyView;
import com.ryan.bilibili_client.widget.banner.BannerEntity;
import com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by MUFCRyan on 2017/5/31.
 */

public class HomeBangumiFragment extends RxLazyFragment {
    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    CustomEmptyView mEmptyView;

    private boolean mRefreshing = false;
    private List<BannerEntity> mBannerList = new ArrayList<>();
    private List<BangumiRecommendInfo.ResultBean> mBangumiRecommends = new ArrayList<>();
    private List<BangumiAppIndexInfo.ResultBean.AdBean.HeadBean> mBanners = new ArrayList<>();
    private List<BangumiAppIndexInfo.ResultBean.AdBean.BodyBean> mBangumiBodies = new ArrayList<>();
    private List<BangumiAppIndexInfo.ResultBean.PreviousBean.ListBean> mSeasonNewBangumis = new ArrayList<>();
    private List<BangumiAppIndexInfo.ResultBean.SerializingBean> mNewBangumiSerials = new ArrayList<>();
    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;
    private int mSeason;

    public static HomeBangumiFragment newInstance() {
        return new HomeBangumiFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_bangumi;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!mPrepared || !mVisible)
            return;
        initRefreshLayout();
        initRecyclerView();
        mPrepared = false;
    }

    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mRefreshing = true;
            loadData();
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            clearData();
            loadData();
        });
    }

    @Override
    protected void initRecyclerView() {
        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getActivity(), 3);
        mGridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mSectionedRecyclerViewAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setNestedScrollingEnabled(true);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);
        setRecycleNoScroll();
    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void finishTask() {

    }

    private void clearData() {
        mRefreshing = true;
        mBanners.clear();
        mBannerList.clear();
        mBangumiBodies.clear();
        mBangumiRecommends.clear();
        mNewBangumiSerials.clear();
        mSeasonNewBangumis.clear();
        mSectionedRecyclerViewAdapter.removeAllSections();
    }

    private void initEmptyView() {

    }

    private void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mRefreshing);
    }
}
