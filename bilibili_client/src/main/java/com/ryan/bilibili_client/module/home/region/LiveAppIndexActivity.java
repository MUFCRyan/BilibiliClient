package com.ryan.bilibili_client.module.home.region;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.LiveAppIndexAdapter;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.network.RetrofitHelper;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class LiveAppIndexActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private LiveAppIndexAdapter mLiveAppIndexAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_live_app_index;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("直播");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.setOnRefreshListener(this::loadData);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            loadData();
        });
    }

    @Override
    public void initRecyclerView() {
        mLiveAppIndexAdapter = new LiveAppIndexAdapter(this);
        mRecyclerView.setAdapter(mLiveAppIndexAdapter);
        GridLayoutManager manager = new GridLayoutManager(this, 12, LinearLayoutManager.VERTICAL, false);
        manager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return mLiveAppIndexAdapter.getSpanSize(position);
            }
        });
        mRecyclerView.setLayoutManager(manager);
    }

    @Override
    public void loadData() {
        RetrofitHelper.getLiveAPI()
                .getLiveAppIndex()
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(liveAppIndexInfo -> {
                    mLiveAppIndexAdapter.setLiveInfo(liveAppIndexInfo);
                    finishTask();
                }, throwable -> {});
    }

    @Override
    public void finishTask() {
        mSwipeRefreshLayout.setRefreshing(false);
        mLiveAppIndexAdapter.notifyDataSetChanged();
        mRecyclerView.scrollToPosition(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
