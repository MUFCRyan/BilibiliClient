package com.ryan.bilibili_client.module.home.attention;

import android.os.Bundle;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;

/**
 * Created by MUFCRyan on 2017/5/31.
 *
 */

public class HomeAttentionFragment extends RxLazyFragment {

    public static HomeAttentionFragment newInstance(){
        return new HomeAttentionFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_attention;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {

    }
}
