package com.ryan.bilibili_client.module.home.discover;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.OriginalRankAdapter;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.discover.OriginalRankInfo;
import com.ryan.bilibili_client.module.video.VideoDetailsActivity;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.LogUtil;
import com.ryan.bilibili_client.utils.ToastUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private boolean mIsRefreshing = false;
    private OriginalRankAdapter mAdapter;
    private List<OriginalRankInfo.RankBean.ListBean> mOriginalRanks = new ArrayList<>();

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
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        mSwipeRefreshLayout.post(() -> {
            mSwipeRefreshLayout.setRefreshing(true);
            mIsRefreshing = true;
            loadData();
        });
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mIsRefreshing = true;
            mOriginalRanks.clear();
            loadData();
        });
    }

    @Override
    protected void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new OriginalRankAdapter(mRecyclerView, mOriginalRanks);
        mRecyclerView.setAdapter(mAdapter);
        setRecycleNoScroll();
        mAdapter.setOnItemClickListener((position, holder) -> VideoDetailsActivity.launch
                (getActivity(), mOriginalRanks.get(position).getAid(), mOriginalRanks.get
                        (position).getPic()));
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getRankAPI()
                .getOriginalRanks(mOrder)
                .compose(bindToLifecycle())
                .map(originalRankInfo -> originalRankInfo.getRank().getList())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBeans -> {
                    mOriginalRanks.addAll(listBeans.subList(0, 20));
                    finishTask();
                }, throwable -> {
                    mSwipeRefreshLayout.setRefreshing(false);
                    LogUtil.all(throwable.getMessage());
                    ToastUtil.shortToast("加载失败啦,请重新加载~");
                });
    }

    @Override
    protected void finishTask() {
        mIsRefreshing = false;
        mSwipeRefreshLayout.setRefreshing(false);
        mAdapter.notifyDataSetChanged();
    }

    private void setRecycleNoScroll() {
        mRecyclerView.setOnTouchListener((v, event) -> mIsRefreshing);
    }
}
