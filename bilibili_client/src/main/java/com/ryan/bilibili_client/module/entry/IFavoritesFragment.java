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
 * 我的收藏
 */

public class IFavoritesFragment extends RxLazyFragment {
    @BindView(R.id.empty_view)
    CustomEmptyView mEmptyView;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static IFavoritesFragment newInstance(){
        return new IFavoritesFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_empty;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mToolbar.setTitle("我的收藏");
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        mToolbar.setOnClickListener(v -> {
            Activity activity = getActivity();
            if (activity instanceof MainActivity)
                ((MainActivity) activity).toggleDrawer();
        });

        mEmptyView.setEmptyImage(R.drawable.img_tips_error_fav_no_data);
        mEmptyView.setEmptyText("没有找到你的收藏哟");
    }
}
