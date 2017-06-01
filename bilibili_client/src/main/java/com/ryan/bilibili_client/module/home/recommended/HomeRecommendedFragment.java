package com.ryan.bilibili_client.module.home.recommended;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.section.HomeRecommendPicSection;
import com.ryan.bilibili_client.adapter.section.HomeRecommendedSection;
import com.ryan.bilibili_client.adapter.section.HomeRecommendActivityCenterSection;
import com.ryan.bilibili_client.adapter.section.HomeRecommendBannerSection;
import com.ryan.bilibili_client.adapter.section.HomeRecommendTopicSection;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.recommend.RecommendBannerInfo;
import com.ryan.bilibili_client.entity.recommend.RecommendInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;
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

import static com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter.VIEW_TYPE_FOOTER;
import static com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER;

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
    private SectionedRecyclerViewAdapter mSectionedAdapter;

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
        mSectionedAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 2);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mSectionedAdapter.getSectionItemViewType(position)){
                    case VIEW_TYPE_HEADER:
                    case VIEW_TYPE_FOOTER:
                        return 2;
                    default :
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mSectionedAdapter);
        setRecycleScroll();
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getBiliAppAPI()
                .getRecommendedBannerInfo()
                .compose(bindToLifecycle())
                .map(RecommendBannerInfo::getData)
                .flatMap(new Func1<List<RecommendBannerInfo.DataBean>, Observable<RecommendInfo>>() {
                    @Override
                    public Observable<RecommendInfo> call(List<RecommendBannerInfo.DataBean> dataBeans) {
                        mRecommendedBanners.addAll(dataBeans);
                        return RetrofitHelper.getBiliAppAPI().getRecommendedInfo();
                    }
                })
                .compose(bindToLifecycle())
                .map(RecommendInfo::getResult)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBeans -> {
                    mResults.addAll(resultBeans);
                    finishTask();
                }, throwable -> {
                    initEmptyView();
                });
    }

    @Override
    protected void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mRefreshing = false;
        hideEmptyView();
        convertBanner();
        mSectionedAdapter.addSection(new HomeRecommendBannerSection(mBanners));
        int size = mResults.size();
        for (int i = 0; i < size; i++) {
            String type = mResults.get(i).getType();
            if (!TextUtils.isEmpty(type)){
                switch(type){
                    case ConstantUtil.TYPE_TOPIC:
                        mSectionedAdapter.addSection(new HomeRecommendTopicSection(getActivity(),
                                mResults.get(i).getBody().get(0).getCover(),
                                mResults.get(i).getBody().get(0).getTitle(),
                                mResults.get(i).getBody().get(0).getParam()));
                        break;
                    case ConstantUtil.TYPE_ACTIVITY_CENTER:
                        mSectionedAdapter.addSection(new HomeRecommendActivityCenterSection(getActivity(), mResults.get(i).getBody()));
                        break;
                    default :
                        mSectionedAdapter.addSection(new HomeRecommendedSection(getActivity(),
                                mResults.get(i).getHead().getTitle(),
                                mResults.get(i).getType(),
                                mResults.get(1).getHead().getCount(),
                                mResults.get(i).getBody()));
                        break;
                }
            }

            String style = mResults.get(i).getHead().getStyle();
            if (style.equals(ConstantUtil.STYLE_PIC))
                mSectionedAdapter.addSection(new HomeRecommendPicSection(getActivity(),
                        mResults.get(i).getBody().get(0).getCover(),
                        mResults.get(i).getBody().get(0).getParam()));
        }
        mSectionedAdapter.notifyDataSetChanged();
    }

    private void clearData() {
        mBanners.clear();
        mRecommendedBanners.clear();
        mResults.clear();
        mRefreshing = true;
        mSectionedAdapter.removeAllSections();
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

    private void convertBanner() {
        Observable.from(mRecommendedBanners)
                .compose(bindToLifecycle())
                .forEach(dataBean -> mBanners.add(new BannerEntity(dataBean.getValue(), dataBean.getTitle(), dataBean.getImage())));
    }

    private void setRecycleScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mRefreshing);
    }
}
