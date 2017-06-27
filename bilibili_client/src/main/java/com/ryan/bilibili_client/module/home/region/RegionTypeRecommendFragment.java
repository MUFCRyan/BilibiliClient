package com.ryan.bilibili_client.module.home.region;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.section.RegionRecommendBannerSection;
import com.ryan.bilibili_client.adapter.section.RegionRecommendDynamicSection;
import com.ryan.bilibili_client.adapter.section.RegionRecommendHotSection;
import com.ryan.bilibili_client.adapter.section.RegionRecommendNewSection;
import com.ryan.bilibili_client.adapter.section.RegionRecommendTypesSection;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.region.RegionRecommendInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.LogUtil;
import com.ryan.bilibili_client.utils.ToastUtil;
import com.ryan.bilibili_client.widget.banner.BannerEntity;
import com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
        mRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mRecyclerView.post(() -> {
            mRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });
        mRefreshLayout.setOnRefreshListener(() -> {
            clearData();
            loadData();
        });
    }

    @Override
    protected void initRecyclerView() {
        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (mSectionedRecyclerViewAdapter.getSectionItemViewType(position) == SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER)
                    return 2;
                return 1;
            }
        });
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);
        setRecycleNoScroll();
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getBiliAppAPI()
                .getRegionRecommends(mRid)
                .map(RegionRecommendInfo::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBean -> {
                    mBanners.addAll(dataBean.getBanner().getTop());
                    mRecommends.addAll(dataBean.getRecommend());
                    mNews.addAll(dataBean.getNewX());
                    mDynamics.addAll(dataBean.getDynamic());
                    finishTask();
                }, throwable -> {
                    LogUtil.all(throwable.getMessage());
                    mRefreshLayout.setRefreshing(false);
                    ToastUtil.shortToast("加载失败啦,请重新加载~");
                });
    }

    @Override
    protected void finishTask() {
        transferBanner();
        mSectionedRecyclerViewAdapter.addSection(new RegionRecommendBannerSection(mBannerEntities));
        mSectionedRecyclerViewAdapter.addSection(new RegionRecommendTypesSection(getActivity(), mRid));
        mSectionedRecyclerViewAdapter.addSection(new RegionRecommendHotSection(getActivity(), mRid, mRecommends));
        mSectionedRecyclerViewAdapter.addSection(new RegionRecommendNewSection(getActivity(), mRid, mNews));
        mSectionedRecyclerViewAdapter.addSection(new RegionRecommendDynamicSection(getActivity(), mDynamics));
        mIsRefreshing = false;
        mRefreshLayout.setRefreshing(false);
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }

    private void transferBanner() {
        Observable.from(mBanners)
                .compose(bindToLifecycle())
                .forEach(topBean -> mBannerEntities.add(new BannerEntity(topBean.getUri(), topBean.getTitle(), topBean.getImage())));
    }

    private void clearData(){
        mBannerEntities.clear();
        mBanners.clear();
        mRecommends.clear();
        mNews.clear();
        mDynamics.clear();
        mIsRefreshing = true;
        mSectionedRecyclerViewAdapter.removeAllSections();
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
}

