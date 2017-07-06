package com.ryan.bilibili_client.module.home.discover;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.utils.ConstantUtil;

import butterknife.BindView;

/**
 * Created by MUFCRyan on 2017/7/6.
 * 原创排行 Fragment 详情界面
 */

public class OriginalRankFragment extends RxLazyFragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    private String mOrder;
    private boolean mIsRefershing = false;


    public static OriginalRankFragment newInstance(String order){
        OriginalRankFragment rankFragment = new OriginalRankFragment();
        Bundle bundle = new Bundle();
        bundle.putString(ConstantUtil.EXTRA_ORDER, order);
        rankFragment.setArguments(bundle);
        return rankFragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_original_rank;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mOrder = getArguments().getString(ConstantUtil.EXTRA_ORDER);
        initRefreshLayout();
        initRecyclerView();
    }

    @Override
    protected void initRefreshLayout() {

    }

    @Override
    protected void initRecyclerView() {

    }
}
