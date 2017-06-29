package com.ryan.bilibili_client.module.home.discover;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.TopicCenterAdapter;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.discover.TopicCenterInfo;
import com.ryan.bilibili_client.module.common.BrowserActivity;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class TopicCenterActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    private List<TopicCenterInfo.ListBean> mTopicCenters = new ArrayList<>();

    private TopicCenterAdapter mAdapter;

    private boolean mIsRefreshing = false;

    @Override
    public int getLayoutId() {
        return R.layout.activity_topic_center;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("话题中心");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void initRefreshLayout() {
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mIsRefreshing = true;
            mSwipeRefreshLayout.setRefreshing(true);
            loadData();
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mIsRefreshing = true;
            mTopicCenters.clear();
            loadData();
        });
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new TopicCenterAdapter(mRecyclerView, mTopicCenters);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener((position, holder) -> BrowserActivity.launch(
                TopicCenterActivity.this, mTopicCenters.get(position).getLink(),
                mTopicCenters.get(position).getTitle()));
        setRecycleNoScroll();
    }

    @Override
    public void loadData() {
        RetrofitHelper.getBiliAPI()
                .getTopicCenterList()
                .compose(bindToLifecycle())
                .map(TopicCenterInfo::getList)
                .map(listBeans -> {
                    List<TopicCenterInfo.ListBean> tempList = new ArrayList<>();
                    for (TopicCenterInfo.ListBean listBean : listBeans) {
                        if (!Objects.equals(listBean.getCover(), ""))
                            tempList.add(listBean);
                    }
                    return tempList;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBeans -> {
                    mTopicCenters.addAll(listBeans);
                    finishTask();
                }, throwable -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    ToastUtil.shortToast("加载失败啦,请重新加载~");
                });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finishTask() {
        mIsRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
}
