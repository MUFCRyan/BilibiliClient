package com.ryan.bilibili_client.module.home.region;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.section.RegionRecommendBannerSection;
import com.ryan.bilibili_client.adapter.section.RegionRecommendDynamicSection;
import com.ryan.bilibili_client.adapter.section.RegionRecommendHotSection;
import com.ryan.bilibili_client.adapter.section.RegionRecommendNewSection;
import com.ryan.bilibili_client.base.RxBaseActivity;
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

public class AdvertisingActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mRefreshLayout;

    private boolean mIsRefreshing = false;

    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;

    private List<BannerEntity> mBannerEntities = new ArrayList<>();

    private List<RegionRecommendInfo.DataBean.BannerBean.TopBean> mBanners = new ArrayList<>();

    private List<RegionRecommendInfo.DataBean.RecommendBean> mRecommends = new ArrayList<>();

    private List<RegionRecommendInfo.DataBean.NewBean> mNews = new ArrayList<>();

    private List<RegionRecommendInfo.DataBean.DynamicBean> mDynamics = new ArrayList<>();


    @Override
    public int getLayoutId() {

        return R.layout.activity_advertising;
    }


    @Override
    public void initViews(Bundle savedInstanceState) {

        initRefreshLayout();
        initRecyclerView();
    }


    @Override
    public void initToolBar() {
        mToolbar.setTitle("广告");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_region, menu);
        return true;
    }


    @Override
    public void initRefreshLayout() {

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


    private void clearData() {

        mBannerEntities.clear();
        mBanners.clear();
        mRecommends.clear();
        mNews.clear();
        mDynamics.clear();
        mIsRefreshing = true;
        mSectionedRecyclerViewAdapter.removeAllSections();
    }


    @Override
    public void initRecyclerView() {

        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(AdvertisingActivity.this, 2);
        mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {

            @Override
            public int getSpanSize(int position) {

                switch (mSectionedRecyclerViewAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 2;

                    default:
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);
        setRecycleNoScroll();
    }


    @Override
    public void loadData() {

        RetrofitHelper.getBiliAppAPI()
                .getRegionRecommends(ConstantUtil.ADVERTISING_RID)
                .compose(bindToLifecycle())
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
    public void finishTask() {

        converBanner();
        mSectionedRecyclerViewAdapter.addSection(new RegionRecommendBannerSection(mBannerEntities));
        mSectionedRecyclerViewAdapter.addSection(
                new RegionRecommendHotSection(AdvertisingActivity.this, ConstantUtil.ADVERTISING_RID,
                        mRecommends));
        mSectionedRecyclerViewAdapter.addSection(
                new RegionRecommendNewSection(AdvertisingActivity.this, ConstantUtil.ADVERTISING_RID,
                        mNews));
        mSectionedRecyclerViewAdapter.addSection(
                new RegionRecommendDynamicSection(AdvertisingActivity.this, mDynamics));

        mIsRefreshing = false;
        mRefreshLayout.setRefreshing(false);
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }


    private void converBanner() {

        Observable.from(mBanners)
                .compose(bindToLifecycle())
                .forEach(topBean -> mBannerEntities.add(new BannerEntity(
                        topBean.getUri(), topBean.getTitle(), topBean.getImage())));
    }


    private void setRecycleNoScroll() {

        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
}
