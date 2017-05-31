package com.ryan.bilibili_client.module.home.bangumi;

import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;

/**
 * Created by MUFCRyan on 2017/5/31.
 *
 */

public class HomeBangumiFragment extends RxLazyFragment {

    public static HomeBangumiFragment newInstance(){
        return new HomeBangumiFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_bangumi;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {

    }
}
