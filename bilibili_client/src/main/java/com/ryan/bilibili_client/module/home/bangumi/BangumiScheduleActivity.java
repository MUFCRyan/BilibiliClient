package com.ryan.bilibili_client.module.home.bangumi;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.bangumi.BangumiScheduleInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ToastUtil;
import com.ryan.bilibili_client.widget.CircleProgressView;
import com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BangumiScheduleActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    private List<BangumiScheduleInfo.ResultBean> mBangumiSchedules = new ArrayList<>();
    private List<BangumiScheduleInfo.ResultBean> sundayBangumis = new ArrayList<>();
    private List<BangumiScheduleInfo.ResultBean> mondayBangumis = new ArrayList<>();
    private List<BangumiScheduleInfo.ResultBean> tuesdayBangumis = new ArrayList<>();
    private List<BangumiScheduleInfo.ResultBean> wednesdayBangumis = new ArrayList<>();
    private List<BangumiScheduleInfo.ResultBean> thursdayBangumis = new ArrayList<>();
    private List<BangumiScheduleInfo.ResultBean> fridayBangumis = new ArrayList<>();
    private List<BangumiScheduleInfo.ResultBean> saturdayBangumis = new ArrayList<>();
    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;

    @Override
    public int getLayoutId() {
        return R.layout.activity_bangumi_schedule;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initRecyclerView();
        loadData();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("番剧时间表");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void initRecyclerView() {
        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(BangumiScheduleActivity.this, 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch(mSectionedRecyclerViewAdapter.getSectionItemViewType(position)){
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 3;
                    default :
                        return 1;
                }
            }
        });
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);
    }

    @Override
    public void loadData() {
        RetrofitHelper.getBangumiAPI()
                .getBangumiSchedules()
                .compose(bindToLifecycle())
                .doOnSubscribe(this::showProgressBar)
                .delay(2000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bangumiScheduleInfo -> {
                    mBangumiSchedules.addAll(bangumiScheduleInfo.getResult());
                    finishTask();
                }, throwable -> {
                    hideProgressBar();
                    ToastUtil.shortToast("加载失败啦，请重新加载~");
                });
    }

    @Override
    public void finishTask() {
        Observable.from(mBangumiSchedules)
                .compose(bindToLifecycle())
                .forEach(this::accordingWeekGroup);
        //mSectionedRecyclerViewAdapter.addSection();
    }

    private void accordingWeekGroup(BangumiScheduleInfo.ResultBean resultBean) {

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
        super.showProgressBar();
        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
    }

    @Override
    public void hideProgressBar() {
        super.hideProgressBar();
        mCircleProgressView.setVisibility(View.GONE);
        mCircleProgressView.stopSpinning();
    }
}
