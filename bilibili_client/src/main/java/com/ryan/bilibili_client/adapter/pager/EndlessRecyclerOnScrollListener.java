package com.ryan.bilibili_client.adapter.pager;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by MUFCRyan on 2017/6/29.
 * 自定义RecylcerView上拉加载处理
 */

public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private int mPreviousTotal = 0;
    private boolean mLoading = true;
    private int mCurrentPage = 1;
    private LinearLayoutManager mLinearLayoutManager;
    protected EndlessRecyclerOnScrollListener(LinearLayoutManager layoutManager){
        mLinearLayoutManager= layoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dy == 0)
            return;
        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        int lastCompletelyVisibleItemPosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
        if (mLoading){
            if (totalItemCount > mPreviousTotal){
                mLoading = false;
                mPreviousTotal = totalItemCount;
            }
        }

        if (!mLoading && visibleItemCount > 0 && lastCompletelyVisibleItemPosition >= totalItemCount - 1){
            mCurrentPage ++;
            onLoadMore(mCurrentPage);
            mLoading = true;
        }
    }

    public abstract void onLoadMore(int currentPage);

    public void refresh(){
        mLoading = true;
        mPreviousTotal = 0;
        mCurrentPage = 1;
    }
}
