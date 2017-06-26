package com.ryan.bilibili_client.module.home.region;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.region.RegionRecommendInfo;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.widget.banner.BannerEntity;
import com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by MUFCRyan on 2017/6/26.
 * 分区推荐 Fragment
 */

public class RegionTypeRecommendFragment extends RxLazyFragment {

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private int mRid;
    private boolean mIsRefreshing = false;
    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;
    private List<BannerEntity> mBannerEntities = new ArrayList<>();
    private List<RegionRecommendInfo.DataBean.BannerBean.TopBean> mBanners = new ArrayList<>();
    private List<RegionRecommendInfo.DataBean.RecommendBean> mRecommends = new ArrayList<>();
    private List<RegionRecommendInfo.DataBean.NewBean> mNews = new ArrayList<>();
    private List<RegionRecommendInfo.DataBean.DynamicBean> mDynamics = new ArrayList<>();

    public static RegionTypeRecommendFragment newInstance(int rid){
        RegionTypeRecommendFragment recommendFragment = new RegionTypeRecommendFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtil.EXTRA_RID, rid);
        recommendFragment.setArguments(bundle);
        return recommendFragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_region_recommend;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mRid = getArguments().getInt(ConstantUtil.EXTRA_RID);
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    protected void initRefreshLayout() {

    }

    @Override
    protected void initRecyclerView() {

    }

    @Override
    protected void loadData() {

    }

    @Override
    protected void finishTask() {

    }
}
