package com.ryan.bilibili_client.module.entry;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.module.base.RxLazyFragment;
import com.ryan.bilibili_client.module.common.MainActivity;
import com.ryan.bilibili_client.widget.CustomEmptyView;

import butterknife.BindView;

/**
 * Created by MUFCRyan on 2017/5/26.
 * 消费历史记录
 */

public class ConsumeHistoryFragment extends RxLazyFragment {
    @BindView(R.id.empty_view)
    CustomEmptyView mEmptyView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static ConsumeHistoryFragment newInstance(){
        return new ConsumeHistoryFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_empty;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mToolbar.setTitle("我的钱包");
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        mToolbar.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity instanceof MainActivity)
                ((MainActivity) activity).toggleDrawer();
        });

        mEmptyView.setEmptyImage(R.drawable.ic_movie_pay_area_limit);
        mEmptyView.setEmptyText("你还没有消费记录哟");
    }
}
