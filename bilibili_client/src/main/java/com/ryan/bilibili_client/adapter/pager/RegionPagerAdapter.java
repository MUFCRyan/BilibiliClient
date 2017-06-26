package com.ryan.bilibili_client.adapter.pager;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ryan.bilibili_client.entity.region.RegionTypesInfo;
import com.ryan.bilibili_client.module.home.region.RegionTypeDetailsFragment;
import com.ryan.bilibili_client.module.home.region.RegionTypeRecommendFragment;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;

/**
 * Created by MUFCRyan on 2017/6/26.
 * 分区 PagerAdapter
 */

public class RegionPagerAdapter extends FragmentStatePagerAdapter {

    private int mRid;
    private List<String> mTitles;
    private List<RegionTypesInfo.DataBean.ChildrenBean> mChildren;
    private List<Fragment> mFragments = new ArrayList<>();

    public RegionPagerAdapter(FragmentManager fm, int rid, List<String> titles, List<RegionTypesInfo.DataBean.ChildrenBean> children) {
        super(fm);
        mRid = rid;
        mTitles = titles;
        mChildren = children;
        initFragments();
    }

    private void initFragments() {
        mFragments.add(RegionTypeRecommendFragment.newInstance(mRid));
        Observable.from(mChildren)
                .subscribe(childrenBean -> mFragments.add(RegionTypeDetailsFragment.newInstance(childrenBean.getTid())));
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles.get(position);
    }
}
