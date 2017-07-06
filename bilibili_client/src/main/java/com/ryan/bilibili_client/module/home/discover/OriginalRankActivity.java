package com.ryan.bilibili_client.module.home.discover;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.flyco.tablayout.SlidingTabLayout;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.widget.NoScrollViewPager;

import butterknife.BindView;

public class OriginalRankActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    @BindView(R.id.view_pager)
    NoScrollViewPager mViewPager;

    private String[] mTitles = new String[] { "原创", "全站", "番剧" };
    private String[] mOrders = new String[] { "origin-03.json", "all-03.json", "all-3-33.json" };

    @Override
    public int getLayoutId() {
        return R.layout.activity_original_rank;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mViewPager.setAdapter(new OriginalRankPagerAdapter(getSupportFragmentManager(), mTitles, mOrders));
        mViewPager.setOffscreenPageLimit(mOrders.length);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("排行榜");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_rank, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static class OriginalRankPagerAdapter extends FragmentStatePagerAdapter{
        private String[] mTitles;
        private String[] mOrders;
        public OriginalRankPagerAdapter(FragmentManager fm, String[] titles, String[] orders) {
            super(fm);
            mTitles = titles;
            mOrders = orders;
        }

        @Override
        public Fragment getItem(int position) {
            return OriginalRankFragment.newInstance(mOrders[position]);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }

        @Override
        public int getCount() {
            return mTitles.length;
        }
    }
}
