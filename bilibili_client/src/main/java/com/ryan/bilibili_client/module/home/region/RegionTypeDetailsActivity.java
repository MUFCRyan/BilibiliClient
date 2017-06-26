package com.ryan.bilibili_client.module.home.region;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.flyco.tablayout.SlidingTabLayout;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.pager.RegionPagerAdapter;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.region.RegionTypesInfo;
import com.ryan.bilibili_client.rx.RxBus;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.widget.NoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;

public class RegionTypeDetailsActivity extends RxBaseActivity {

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.view_pager)
    NoScrollViewPager mViewPager;

    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTab;

    private RegionTypesInfo.DataBean mDataBean;

    private List<String> mTitles = new ArrayList<>();

    public static void launch(Activity activity, RegionTypesInfo.DataBean dataBean) {
        Intent intent = new Intent(activity, RegionTypeDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putParcelable(ConstantUtil.EXTRA_PARTITION, dataBean);
        intent.putExtras(bundle);
        activity.startActivity(intent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_region_type_details;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            mDataBean = bundle.getParcelable(ConstantUtil.EXTRA_PARTITION);

        initViewPager();
        initRxBus();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle(mDataBean.getName());
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_region, menu);
        return true;
    }

    private void initViewPager() {
        mTitles.add("推荐");
        Observable.from(mDataBean.getChildren())
                .subscribe(childrenBean -> mTitles.add(childrenBean.getName()));
        RegionPagerAdapter pagerAdapter = new RegionPagerAdapter(getSupportFragmentManager(), mDataBean.getTid(), mTitles, mDataBean.getChildren());
        mViewPager.setOffscreenPageLimit(mTitles.size());
        mViewPager.setAdapter(pagerAdapter);
        mSlidingTab.setViewPager(mViewPager);
        measureTabLayoutTextWidth(0);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                measureTabLayoutTextWidth(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void initRxBus() {
        RxBus.getInstance()
                .toObservable(Integer.class)
                .compose(bindToLifecycle())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::switchPager);
    }

    private void measureTabLayoutTextWidth(int position) {
        String title = mTitles.get(position);
        TextView titleView = mSlidingTab.getTitleView(position);
        TextPaint paint = titleView.getPaint();
        float width = paint.measureText(title);
        mSlidingTab.setIndicatorWidth(width / 3);
    }

    private void switchPager(int position){
        switch (position) {
            case 0:
                mViewPager.setCurrentItem(1);
                break;

            case 1:
                mViewPager.setCurrentItem(2);
                break;

            case 2:
                mViewPager.setCurrentItem(3);
                break;

            case 3:
                mViewPager.setCurrentItem(4);
                break;

            case 4:
                mViewPager.setCurrentItem(5);
                break;

            case 5:
                mViewPager.setCurrentItem(6);
                break;

            case 6:
                mViewPager.setCurrentItem(7);
                break;
        }
    }
}
