package com.ryan.bilibili_client.module.home.region;

import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.section.RegionDetailsHotVideoSection;
import com.ryan.bilibili_client.adapter.section.RegionDetailsNewsVideoSection;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.region.RegionDetailsInfo;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.LogUtil;
import com.ryan.bilibili_client.utils.ToastUtil;
import com.ryan.bilibili_client.widget.CircleProgressView;
import com.ryan.bilibili_client.widget.sectioned.SectionedRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by MUFCRyan on 2017/6/26.
 * 分区推荐 Fragment
 */

public class RegionTypeDetailsFragment extends RxLazyFragment {

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    private int mRid;

    private SectionedRecyclerViewAdapter mSectionedRecyclerViewAdapter;

    private List<RegionDetailsInfo.DataBean.NewBean> mNewsVideos = new ArrayList<>();

    private List<RegionDetailsInfo.DataBean.RecommendBean> mRecommendVideos = new ArrayList<>();

    public static RegionTypeDetailsFragment newInstance(int rid){
        RegionTypeDetailsFragment fragment = new RegionTypeDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtil.EXTRA_RID, rid);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_region_details;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mRid = getArguments().getInt(ConstantUtil.EXTRA_RID);
        mPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!mPrepared || ! mVisible)
            return;
        showProgressBar();
        initRecyclerView();
        loadData();
        mPrepared = false;
    }

    @Override
    protected void initRecyclerView() {
        mSectionedRecyclerViewAdapter = new SectionedRecyclerViewAdapter();
        GridLayoutManager manager = new GridLayoutManager(getActivity(), 1);
        mRecyclerView.setLayoutManager(manager);
        mRecyclerView.setAdapter(mSectionedRecyclerViewAdapter);
    }

    @Override
    protected void loadData() {
        RetrofitHelper.getBiliAppAPI()
                .getRegionDetails(mRid)
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(regionDetailsInfo -> {
                    mRecommendVideos.addAll(regionDetailsInfo.getData().getRecommend());
                    mNewsVideos.addAll(regionDetailsInfo.getData().getNewX());
                    finishTask();
                }, throwable -> {
                    LogUtil.all(throwable.getMessage());
                    hideProgressBar();
                    ToastUtil.shortToast("加载失败啦,请重新加载~");
                });
    }

    @Override
    protected void finishTask() {
        hideProgressBar();
        mSectionedRecyclerViewAdapter.addSection(
                new RegionDetailsHotVideoSection(getActivity(), mRecommendVideos));
        mSectionedRecyclerViewAdapter.addSection(
                new RegionDetailsNewsVideoSection(getActivity(), mNewsVideos));
        mSectionedRecyclerViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void showProgressBar() {
        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
    }

    @Override
    protected void hideProgressBar() {
        mCircleProgressView.setVisibility(View.GONE);
        mCircleProgressView.stopSpinning();
    }
}
