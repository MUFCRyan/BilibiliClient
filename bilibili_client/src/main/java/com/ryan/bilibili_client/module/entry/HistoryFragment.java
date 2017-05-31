package com.ryan.bilibili_client.module.entry;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.module.common.MainActivity;
import com.ryan.bilibili_client.widget.CustomEmptyView;

import butterknife.BindView;

/**
 * Created by MUFCRyan on 2017/5/26.
 * 观看历史记录
 */

public class HistoryFragment extends RxLazyFragment {
    @BindView(R.id.empty_view)
    CustomEmptyView mEmptyView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static HistoryFragment newInstance(){
        return new HistoryFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_empty;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mToolbar.setTitle("历史记录");
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        mToolbar.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity instanceof MainActivity)
                ((MainActivity) activity).toggleDrawer();
        });

        mEmptyView.setEmptyImage(R.drawable.ic_movie_pay_order_error);
        mEmptyView.setEmptyText("暂时还没有观看记录哟");
    }
}
