package com.ryan.bilibili_client.module.home.attention;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.helper.HeaderViewRecyclerAdapter;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.attention.AttentionDynamicInfo;
import com.ryan.bilibili_client.entity.user.UserChaseBangumiInfo;
import com.ryan.bilibili_client.widget.CustomEmptyView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

/**
 * Created by MUFCRyan on 2017/5/31.
 * 主页面关注界面
 */

public class HomeAttentionFragment extends RxLazyFragment {

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    @BindView(R.id.empty_layout)
    CustomEmptyView mCustomEmptyView;

    private static final int MID = 9467159;

    private boolean mIsRefreshing = false;

    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;

    private List<UserChaseBangumiInfo.DataBean.ResultBean> mChaseBangumis = new ArrayList<>();

    private List<AttentionDynamicInfo.DataBean.FeedsBean> mDynamics = new ArrayList<>();

    public static HomeAttentionFragment newInstance(){
        return new HomeAttentionFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_attention;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mPrepared = true;
        lazyLoad();
    }

    @Override
    protected void lazyLoad() {
        if (!mPrepared || !mVisible) {
            return;
        }
        initRefreshLayout();
        mPrepared = false;
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
            clearData();
            loadData();
        });
    }

    @Override
    protected void loadData() {
        /*RetrofitHelper.getUserAPI()
                .getUserChaseBangumis(MID)
                .compose(bindToLifecycle())
                .map(userChaseBangumiInfo -> userChaseBangumiInfo.getData().getResult())
                .flatMap(new Func1<List<UserChaseBangumiInfo.DataBean.ResultBean>, Observable<AttentionDynamicInfo>>() {
                    @Override
                    public Observable<AttentionDynamicInfo> call(List<UserChaseBangumiInfo.DataBean.ResultBean> resultBeen) {
                        return null;
                    }
                })*/

    }

    private void clearData() {
        mIsRefreshing = true;
        mChaseBangumis.clear();
        mDynamics.clear();
    }
}
