package com.ryan.bilibili_client.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.module.home.attention.HomeAttentionFragment;
import com.ryan.bilibili_client.module.home.bangumi.HomeBangumiFragment;
import com.ryan.bilibili_client.module.home.discover.HomeDiscoverFragment;
import com.ryan.bilibili_client.module.home.live.HomeLiveFragment;
import com.ryan.bilibili_client.module.home.recommended.HomeRecommendedFragment;
import com.ryan.bilibili_client.module.home.region.HomeRegionFragment;

/**
 * Created by MUFCRyan on 2017/5/26.
 *
 */

public class HomePageAdapter extends FragmentPagerAdapter {
    private final String[] TITLES;
    private Fragment[] mFragments;
    public HomePageAdapter(FragmentManager fm, Context context) {
        super(fm);
        TITLES = context.getResources().getStringArray(R.array.sections);
        mFragments = new Fragment[TITLES.length];
    }

    @Override
    public Fragment getItem(int position) {
        if (mFragments[position] == null){
            switch (position){
                case 0:
                    mFragments[position] = HomeLiveFragment.newInstance();
                    break;
                case 1:
                    mFragments[position] = HomeRecommendedFragment.newInstance();
                    break;
                case 2:
                    mFragments[position] = HomeBangumiFragment.newInstance();
                    break;
                case 3:
                    mFragments[position] = HomeRegionFragment.newInstance();
                    break;
                case 4:
                    mFragments[position] = HomeAttentionFragment.newInstance();
                    break;
                case 5:
                    mFragments[position] = HomeDiscoverFragment.newInstance();
                    break;
                default:
                    break;

            }
        }
        return mFragments[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }
}
