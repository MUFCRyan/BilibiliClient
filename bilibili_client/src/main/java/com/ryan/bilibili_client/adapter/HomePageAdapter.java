package com.ryan.bilibili_client.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.ryan.bilibili_client.R;

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
        if (mFragments[0] == null){

        }
        return null;
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
