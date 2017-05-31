package com.ryan.bilibili_client.module.home.discover;

import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;

/**
 * Created by MUFCRyan on 2017/5/31.
 *
 */

public class HomeDiscoverFragment extends RxLazyFragment {

    public static HomeDiscoverFragment newInstance(){
        return new HomeDiscoverFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_discover;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {

    }
}
