package com.ryan.bilibili_client.module.home.bangumi;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.section.HomeBangumiBannerSection;
import com.ryan.bilibili_client.adapter.section.HomeBangumiBodySection;
import com.ryan.bilibili_client.adapter.section.HomeBangumiItemSection;
import com.ryan.bilibili_client.adapter.section.HomeBangumiNewSerialSection;
import com.ryan.bilibili_client.adapter.section.HomeBangumiRecommendSection;
import com.ryan.bilibili_client.adapter.section.HomeBangumiSeasonNewSection;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.bangumi.BangumiAppIndexInfo;
import com.ryan.bilibili_client.entity.bangumi.BangumiRecommendInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.SnackbarUtil;
import com.ryan.bilibili_client.widget.CustomEmptyView;
import com.ryan.bilibili_client.widget.banner.BannerEntity;
import com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by MUFCRyan on 2017/5/31.
 * 首页番剧界面
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
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
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
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);
        setRecycleNoScroll();
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getBangumiAPI()
                .getBangumiAppIndex()
                .compose(bindToLifecycle())
                .flatMap(new Func1<BangumiAppIndexInfo, Observable<BangumiRecommendInfo>>() {
                    @Override
                    public Observable<BangumiRecommendInfo> call(BangumiAppIndexInfo bangumiAppIndexInfo) {
                        mBanners.addAll(bangumiAppIndexInfo.getResult().getAd().getHead());
                        mBangumiBodies.addAll(bangumiAppIndexInfo.getResult().getAd().getBody());
                        mSeasonNewBangumis.addAll(bangumiAppIndexInfo.getResult().getPrevious().getList());
                        mSeason = bangumiAppIndexInfo.getResult().getPrevious().getSeason();
                        mNewBangumiSerials.addAll(bangumiAppIndexInfo.getResult().getSerializing());
                        return RetrofitHelper.getBangumiAPI().getBangumiRecommended();
                    }
                })
                .compose(bindToLifecycle())
                .map(BangumiRecommendInfo::getResult)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBeans -> {
                    mBangumiRecommends.addAll(resultBeans);
                    finishTask();
                }, throwable -> initEmptyView());
    }

    @Override
    protected void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mRefreshing = false;
        hideEmptyView();
        Observable.from(mBanners)
                .compose(bindToLifecycle())
                .forEach(bannerBean -> mBannerList.add(new BannerEntity(bannerBean.getLink(), bannerBean.getTitle(), bannerBean.getImg())));
        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiBannerSection(mBannerList));
        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiItemSection(getActivity()));
        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiNewSerialSection(getActivity(), mNewBangumiSerials));
        if (!mBangumiBodies.isEmpty())
            mSectionedRecyclerViewAdapter.addSection(new HomeBangumiBodySection(getActivity(), mBangumiBodies));
        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiSeasonNewSection(getActivity(), mSeason, mSeasonNewBangumis));
        mSectionedRecyclerViewAdapter.addSection(new HomeBangumiRecommendSection(getActivity(), mBangumiRecommends));
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
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
        mSwipeRefreshLayout.setRefreshing(false);
        mEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        mEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }

    private void hideEmptyView() {
        mEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mRefreshing);
    }
}
