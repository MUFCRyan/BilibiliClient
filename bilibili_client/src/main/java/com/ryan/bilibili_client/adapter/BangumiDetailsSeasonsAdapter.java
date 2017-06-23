package com.ryan.bilibili_client.adapter;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.helper.AbsRecyclerViewAdapter;
import com.ryan.bilibili_client.entity.bangumi.BangumiDetailsInfo;

import java.util.List;

/**
 * Created by MUFCRyan on 2017/6/23.
 * 番剧详情分季版本adapter
 */

public class BangumiDetailsSeasonsAdapter extends AbsRecyclerViewAdapter {

    private int mLayoutPosition;
    private List<BangumiDetailsInfo.ResultBean.SeasonsBean> mSeasons;
    public BangumiDetailsSeasonsAdapter(RecyclerView recyclerView, List<BangumiDetailsInfo.ResultBean.SeasonsBean> seasons){
        super(recyclerView);
        mSeasons = seasons;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_bangumi_details_seasons, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            BangumiDetailsInfo.ResultBean.SeasonsBean seasonsBean = mSeasons.get(position);
            itemViewHolder.mSeasons.setText(seasonsBean.getTitle());

            if (position == mLayoutPosition){
                itemViewHolder.mCardView.setForeground(getContext().getResources().getDrawable(R.drawable.bg_selection));
                itemViewHolder.mSeasons.setTextColor(getContext().getResources().getColor(R.color.colorPrimary));
            } else {
                itemViewHolder.mCardView.setForeground(getContext().getResources().getDrawable(R.drawable.bg_normal));
                itemViewHolder.mSeasons.setTextColor(getContext().getResources().getColor(R.color.font_normal));
            }
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mSeasons.size();
    }

    public void notifyItemForeground(int clickPosition){
        mLayoutPosition = clickPosition;
        notifyDataSetChanged();
    }

    private class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {

        CardView mCardView;

        TextView mSeasons;


        public ItemViewHolder(View itemView) {

            super(itemView);
            mCardView = $(R.id.card_view);
            mSeasons = $(R.id.tv_seasons);
        }
    }
}
