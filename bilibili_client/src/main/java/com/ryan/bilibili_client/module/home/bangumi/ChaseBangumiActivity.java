package com.ryan.bilibili_client.module.home.bangumi;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.ChaseBangumiAdapter;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.user.UserChaseBangumiInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.widget.CircleProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 追番界面
 */
public class ChaseBangumiActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    private static final int MID = 9467159;
    private List<UserChaseBangumiInfo.DataBean.ResultBean> mChaseBangumis = new ArrayList<>();
    private ChaseBangumiAdapter mChaseBangumiAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_chase_bangumi;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initRecyclerView();
        loadData();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("追番");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(ChaseBangumiActivity.this));
        mChaseBangumiAdapter = new ChaseBangumiAdapter(mRecyclerView, mChaseBangumis);
        mRecyclerView.setAdapter(mChaseBangumiAdapter);
    }

    @Override
    public void loadData() {
        RetrofitHelper.getUserAPI()
                .getUserChaseBangumis(MID)
                .compose(bindToLifecycle())
                .doOnSubscribe(this::showProgressBar)
                .map(userChaseBangumiInfo -> userChaseBangumiInfo.getData().getResult())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(resultBeans -> {
                    mChaseBangumis.addAll(resultBeans);
                    finishTask();
                }, throwable -> {});
    }

    @Override
    public void finishTask() {
        hideProgressBar();
        mChaseBangumiAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chase_bangumi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
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
