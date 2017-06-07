package com.ryan.bilibili_client.adapter.helper;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by MUFCRyan on 2017/6/7.
 * RecyclerView 添加 Header、Footer 辅助类
 */

public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int HEADERS_START = Integer.MIN_VALUE;
    private static final int FOOTERS_START = Integer.MIN_VALUE + 10;
    private static final int ITEMS_START = Integer.MIN_VALUE + 20;
    private static final int ADAPTER_MAX_TYPES = 100;
    private RecyclerView.Adapter mWrappedAdapter;
    private List<View> mHeaderViews, mFooterViews;
    private Map<Class, Integer> mItemTypesOffset;

    public HeaderViewRecyclerAdapter(RecyclerView.Adapter adapter){
        mHeaderViews = new ArrayList<>();
        mFooterViews = new ArrayList<>();
        mItemTypesOffset = new HashMap<>();
        setWrappedAdapter(adapter);
    }

    public void setAdapter(RecyclerView.Adapter adapter){
        if (mWrappedAdapter != null && mWrappedAdapter.getItemCount() > 0){
            notifyItemRangeRemoved(getHeaderCount(), mWrappedAdapter.getItemCount());
        }
        setWrappedAdapter(adapter);
        notifyItemRangeInserted(getHeaderCount(), mWrappedAdapter.getItemCount());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < HEADERS_START + getHeaderCount())
            return new StaticViewHolder(mHeaderViews.get(viewType - HEADERS_START));
        else if (viewType < FOOTERS_START + getFooterCount())
            return new StaticViewHolder(mFooterViews.get(viewType - FOOTERS_START));
        return mWrappedAdapter.onCreateViewHolder(parent, viewType - getAdapterTypeOffset());
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int headerCount = getHeaderCount();
        if (position >= headerCount && position <= headerCount + getWrappedItemCount())
            mWrappedAdapter.onBindViewHolder(holder, position - headerCount);
    }

    @Override
    public int getItemViewType(int position) {
        int headerCount = getHeaderCount();
        if (position < headerCount)
            return HEADERS_START + position;
        else {
            int itemCount = mWrappedAdapter.getItemCount();
            if (position < headerCount + itemCount)
                return getAdapterTypeOffset() + mWrappedAdapter.getItemViewType(position - headerCount);
            else
                return FOOTERS_START + position - headerCount - itemCount;
        }
    }

    @Override
    public int getItemCount() {
        return getHeaderCount() + getFooterCount() + getWrappedItemCount();
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
    }

    public void addFooterView(View view) {
        mFooterViews.add(view);
    }

    public void removeHeadView() {
        mHeaderViews.clear();
    }

    public void removeFootView() {
        mFooterViews.clear();
    }

    private void setWrappedAdapter(RecyclerView.Adapter adapter) {
        if (mWrappedAdapter != null)
            mWrappedAdapter.unregisterAdapterDataObserver(mDataObserver);
        mWrappedAdapter = adapter;
        Class<? extends RecyclerView.Adapter> adapterClass = mWrappedAdapter.getClass();
        if (!mItemTypesOffset.containsKey(adapterClass))
            putAdapterTypeOffset(adapterClass);
    }

    private void putAdapterTypeOffset(Class<? extends RecyclerView.Adapter> adapterClass) {
        mItemTypesOffset.put(adapterClass, ITEMS_START + mItemTypesOffset.size() * ADAPTER_MAX_TYPES);
    }

    private int getHeaderCount() {
        return mHeaderViews.size();
    }

    private int getFooterCount() {
        return mFooterViews.size();
    }

    private int getAdapterTypeOffset() {
        return mItemTypesOffset.get(mWrappedAdapter.getClass());
    }

    private int getWrappedItemCount() {
        return mWrappedAdapter.getItemCount();
    }

    private RecyclerView.AdapterDataObserver mDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            notifyItemRangeChanged(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            notifyItemRangeInserted(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            notifyItemRangeRemoved(positionStart + getHeaderCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            int headerCount = getHeaderCount();
            notifyItemRangeChanged(fromPosition + headerCount, toPosition + headerCount + itemCount);
        }
    };

    private static class StaticViewHolder extends RecyclerView.ViewHolder {
        public StaticViewHolder(View itemView) {
            super(itemView);
        }
    }
}
