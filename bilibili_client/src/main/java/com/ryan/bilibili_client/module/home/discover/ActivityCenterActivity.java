package com.ryan.bilibili_client.module.home.discover;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.ActivityCenterAdapter;
import com.ryan.bilibili_client.adapter.helper.HeaderViewRecyclerAdapter;
import com.ryan.bilibili_client.adapter.pager.EndlessRecyclerOnScrollListener;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.discover.ActivityCenterInfo;
import com.ryan.bilibili_client.module.common.BrowserActivity;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class ActivityCenterActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    private int mPageNum = 1;

    private int mPageSize = 20;

    private View mLoadMoreView;

    private List<ActivityCenterInfo.ListBean> mActivityCenters = new ArrayList<>();

    private ActivityCenterAdapter mAdapter;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    private boolean mIsRefreshing = false;

    private EndlessRecyclerOnScrollListener mEndlessRecyclerOnScrollListener;

    @Override
    public int getLayoutId() {
        return R.layout.activity_activity_center;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("活动中心");
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
    public void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            loadData();
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mPageNum = 1;
            mIsRefreshing = true;
            mActivityCenters.clear();
            mEndlessRecyclerOnScrollListener.refresh();
            loadData();
        });
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mAdapter = new ActivityCenterAdapter(mRecyclerView, mActivityCenters);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(mAdapter);
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
        createLoadMoreView();
        mEndlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                mPageNum ++;
                loadData();
                mLoadMoreView.setVisibility(View.VISIBLE);
            }
        };
        mRecyclerView.addOnScrollListener(mEndlessRecyclerOnScrollListener);
        mAdapter.setOnItemClickListener((position, holder) -> BrowserActivity.launch(ActivityCenterActivity.this, mActivityCenters.get(position).getLink(), mActivityCenters.get(position).getTitle()));
        setRecycleNoScroll();
    }

    private void createLoadMoreView() {
        mLoadMoreView = LayoutInflater.from(this).inflate(R.layout.layout_load_more, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addFooterView(mLoadMoreView);
        mLoadMoreView.setVisibility(View.GONE);
    }

    @Override
    public void loadData() {
        RetrofitHelper.getBiliAPI()
                .getActivityCenterList(mPageNum, mPageSize)
                .compose(bindToLifecycle())
                .delay(1000, TimeUnit.MILLISECONDS)
                .map(ActivityCenterInfo::getList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(listBeans -> {
                    if (listBeans.size() < mPageSize){
                        mLoadMoreView.setVisibility(View.GONE);
                        mHeaderViewRecyclerAdapter.removeFootView();
                    }
                })
                .subscribe(listBeans -> {
                    mActivityCenters.addAll(listBeans);
                    finishTask();
                }, throwable -> {
                    if (mSwipeRefreshLayout.isRefreshing()){
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                    mLoadMoreView.setVisibility(View.GONE);
                    ToastUtil.shortToast("加载失败啦,请重新加载~");
                });
    }

    @Override
    public void finishTask() {
        mIsRefreshing = false;
        mLoadMoreView.setVisibility(View.GONE);
        if (mSwipeRefreshLayout.isRefreshing())
            mSwipeRefreshLayout.setRefreshing(false);
        if (mPageNum * mPageSize - mPageSize - 1 > 0){
            mAdapter.notifyItemRangeChanged(mPageNum * mPageSize - mPageSize - 1, mPageSize);
        } else {
            mAdapter.notifyDataSetChanged();
        }
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
}
