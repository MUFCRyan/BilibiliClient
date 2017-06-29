package com.ryan.bilibili_client.module.home.attention;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.AttentionBangumiAdapter;
import com.ryan.bilibili_client.adapter.AttentionDynamicAdapter;
import com.ryan.bilibili_client.adapter.helper.HeaderViewRecyclerAdapter;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.attention.AttentionDynamicInfo;
import com.ryan.bilibili_client.entity.user.UserChaseBangumiInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.SnackbarUtil;
import com.ryan.bilibili_client.utils.ViewUtil;
import com.ryan.bilibili_client.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by MUFCRyan on 2017/5/31.
 * 主页面关注界面
 */

public class HomeAttentionFragment extends RxLazyFragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    private static final int MID = 9467159;

    private boolean mIsRefreshing = false;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    private List<UserChaseBangumiInfo.DataBean.ResultBean> mChaseBangumis = new ArrayList<>();

    private List<AttentionDynamicInfo.DataBean.FeedsBean> mDynamics = new ArrayList<>();

    public static HomeAttentionFragment newInstance(){
        return new HomeAttentionFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_attention;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!mPrepared || !mVisible) {
            return;
        }
        initRefreshLayout();
        mPrepared = false;
    }

    @Override
    protected void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            clearData();
            loadData();
        });
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getUserAPI()
                .getUserChaseBangumis(MID)
                .compose(bindToLifecycle())
                .map(userChaseBangumiInfo -> userChaseBangumiInfo.getData().getResult())
                .flatMap(new Func1<List<UserChaseBangumiInfo.DataBean.ResultBean>, Observable<AttentionDynamicInfo>>() {
                    @Override
                    public Observable<AttentionDynamicInfo> call(List<UserChaseBangumiInfo.DataBean.ResultBean> resultBeans) {
                        mChaseBangumis.addAll(resultBeans);
                        return RetrofitHelper.getBiliAPI().getAttentionDynamic();
                    }
                })
                .map(attentionDynamicInfo -> attentionDynamicInfo.getData().getFeeds())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(feedsBeans -> {
                    mDynamics.addAll(feedsBeans);
                    finishTask();
                }, throwable -> {
                    initEmptyView();
                });

    }

    @Override
    protected void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefreshing = false;
        hideEmptyView();
        initRecyclerView();
    }

    @Override
    protected void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AttentionDynamicAdapter adapter = new AttentionDynamicAdapter(mRecyclerView, mDynamics);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(adapter);
        createHeaderView();
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        setRecycleNoScroll();
    }

    private void createHeaderView() {
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.layout_attention_head_view, mRecyclerView, false);
        RecyclerView bangumiRecycler = ViewUtil.find(headerView, R.id.focus_head_recycler);
        bangumiRecycler.setHasFixedSize(false);
        bangumiRecycler.setNestedScrollingEnabled(false);
        bangumiRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        bangumiRecycler.setAdapter(new AttentionBangumiAdapter(bangumiRecycler, mChaseBangumis));
        mHeaderViewRecyclerAdapter.addHeaderView(headerView);
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }

    private void initEmptyView() {
        mSwipeRefreshLayout.setRefreshing(false);
        mCustomEmptyView.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.GONE);
        mCustomEmptyView.setEmptyImage(R.drawable.img_tips_error_load_error);
        mCustomEmptyView.setEmptyText("加载失败~(≧▽≦)~啦啦啦.");
        SnackbarUtil.showMessage(mRecyclerView, "数据加载失败,请重新加载或者检查网络是否链接");
    }

    private void hideEmptyView() {
        mCustomEmptyView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void clearData() {
        mIsRefreshing = true;
        mChaseBangumis.clear();
        mDynamics.clear();
    }
}
