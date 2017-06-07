package com.ryan.bilibili_client.module.home.bangumi;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.BangumiIndexAdapter;
import com.ryan.bilibili_client.adapter.helper.HeaderViewRecyclerAdapter;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.bangumi.BangumiIndexInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.widget.CircleProgressView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BangumiIndexActivity extends RxBaseActivity {

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    private List<BangumiIndexInfo.ResultBean.CategoryBean> mCategories = new ArrayList<>();
    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bangumi_index;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadData();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("番剧索引");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadData() {
        RetrofitHelper.getBangumiAPI()
                .getBangumiIndex()
                .compose(bindToLifecycle())
                .doOnSubscribe(this::showProgressBar)
                .subscribeOn(Schedulers.io())
                .delay(2000, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bangumiIndexInfo -> {
                    mCategories.addAll(bangumiIndexInfo.getResult().getCategory());
                    finishTask();
                }, throwable -> hideProgressBar());

    }

    @Override
    public void finishTask() {
        initRecyclerView();
        hideProgressBar();
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        GridLayoutManager layoutManager = new GridLayoutManager(this, 3);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 0 == position ? layoutManager.getSpanCount() : 1;
            }
        });
        mRecyclerView.setLayoutManager(layoutManager);
        BangumiIndexAdapter indexAdapter = new BangumiIndexAdapter(mRecyclerView, mCategories);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(indexAdapter);
        createHeadLayout();
        mRecyclerView.setAdapter(mHeaderViewRecyclerAdapter);
    }

    private void createHeadLayout() {
        View headView = LayoutInflater.from(this).inflate(R.layout.layout_bangumi_index_head, mRecyclerView, false);
        mHeaderViewRecyclerAdapter.addHeaderView(headView);
    }

    @Override
    public void showProgressBar() {
        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
    }


    @Override
    public void hideProgressBar() {
        mCircleProgressView.setVisibility(View.GONE);
        mCircleProgressView.stopSpinning();
    }
}
