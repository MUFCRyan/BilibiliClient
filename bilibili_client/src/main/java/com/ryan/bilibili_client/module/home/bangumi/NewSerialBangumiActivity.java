package com.ryan.bilibili_client.module.home.bangumi;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.NewBangumiSerialAdapter;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.bangumi.NewBangumiSerialInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.LogUtil;
import com.ryan.bilibili_client.widget.CircleProgressView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NewSerialBangumiActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    private List<NewBangumiSerialInfo.ListBean> mNewBangumiSerials = new ArrayList<>();
    private NewBangumiSerialAdapter mSerialAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_new_serial_bangumi;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initRecyclerView();
        loadData();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("全部新番连载");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mSerialAdapter = new NewBangumiSerialAdapter(mRecyclerView, mNewBangumiSerials, true);
        mRecyclerView.setAdapter(mSerialAdapter);
        mSerialAdapter.setOnItemClickListener((position, holder) -> {});
    }

    @Override
    public void loadData() {
        RetrofitHelper.getBiliGoAPI()
                .getNewBangumiSerialList()
                .compose(bindToLifecycle())
                .doOnSubscribe(this::showProgressBar)
                .map(NewBangumiSerialInfo::getList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBeans -> {
                    mNewBangumiSerials.addAll(listBeans);
                    finishTask();
                }, throwable -> {
                    LogUtil.e(throwable.getMessage());
                    hideProgressBar();
                });
    }

    @Override
    public void finishTask() {
        hideProgressBar();
        mSerialAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
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
