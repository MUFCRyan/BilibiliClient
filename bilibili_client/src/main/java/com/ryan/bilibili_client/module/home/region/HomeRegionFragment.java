package com.ryan.bilibili_client.module.home.region;

import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;

/**
 * Created by MUFCRyan on 2017/5/31.
 *
 */

public class HomeRegionFragment extends RxLazyFragment {

    public static HomeRegionFragment newInstance(){
        return new HomeRegionFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_region;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {

    }
}
