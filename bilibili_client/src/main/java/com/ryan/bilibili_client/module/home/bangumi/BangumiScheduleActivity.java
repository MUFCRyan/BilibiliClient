package com.ryan.bilibili_client.module.home.bangumi;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.section.BangumiScheduleSection;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.bangumi.BangumiScheduleInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.ToastUtil;
import com.ryan.bilibili_client.utils.WeekDayUtil;
import com.ryan.bilibili_client.widget.CircleProgressView;
import com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.ryan.bilibili_client.utils.ConstantUtil.FRIDAY_TYEP;
import static com.ryan.bilibili_client.utils.ConstantUtil.MONDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.SATURDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.SUNDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.THURSDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.TUESDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.WEDNESDAY_TYPE;
import static com.ryan.bilibili_client.utils.WeekDayUtil.getWeek;

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
        mSectionedRecyclerViewAdapter.addSection(new BangumiScheduleSection(this, sundayBangumis, SUNDAY_TYPE, WeekDayUtil.formatDate(sundayBangumis.get(0).getPub_date())));
        mSectionedRecyclerViewAdapter.addSection(new BangumiScheduleSection(this, mondayBangumis, ConstantUtil.MONDAY_TYPE, WeekDayUtil.formatDate(mondayBangumis.get(0).getPub_date())));
        mSectionedRecyclerViewAdapter.addSection(new BangumiScheduleSection(this, tuesdayBangumis, ConstantUtil.TUESDAY_TYPE, WeekDayUtil.formatDate(tuesdayBangumis.get(0).getPub_date())));
        mSectionedRecyclerViewAdapter.addSection(new BangumiScheduleSection(this, wednesdayBangumis, ConstantUtil.WEDNESDAY_TYPE, WeekDayUtil.formatDate(wednesdayBangumis.get(0).getPub_date())));
        mSectionedRecyclerViewAdapter.addSection(new BangumiScheduleSection(this, thursdayBangumis, ConstantUtil.THURSDAY_TYPE, WeekDayUtil.formatDate(thursdayBangumis.get(0).getPub_date())));
        mSectionedRecyclerViewAdapter.addSection(new BangumiScheduleSection(this, fridayBangumis, ConstantUtil.FRIDAY_TYEP, WeekDayUtil.formatDate(fridayBangumis.get(0).getPub_date())));
        mSectionedRecyclerViewAdapter.addSection(new BangumiScheduleSection(this, saturdayBangumis, ConstantUtil.SATURDAY_TYPE, WeekDayUtil.formatDate(saturdayBangumis.get(0).getPub_date())));
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
        hideProgressBar();
    }

    private void accordingWeekGroup(BangumiScheduleInfo.ResultBean resultBean) {
        switch(getWeek(resultBean.getPub_date())){
            case SUNDAY_TYPE:
                sundayBangumis.add(resultBean);
                break;
            case MONDAY_TYPE:
                mondayBangumis.add(resultBean);
                break;
            case TUESDAY_TYPE:
                tuesdayBangumis.add(resultBean);
                break;
            case WEDNESDAY_TYPE:
                wednesdayBangumis.add(resultBean);
                break;
            case THURSDAY_TYPE:
                thursdayBangumis.add(resultBean);
                break;
            case FRIDAY_TYEP:
                fridayBangumis.add(resultBean);
                break;
            case SATURDAY_TYPE:
                saturdayBangumis.add(resultBean);
                break;
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
