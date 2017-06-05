package com.ryan.bilibili_client.adapter.section;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.module.home.bangumi.BangumiIndexActivity;
import com.ryan.bilibili_client.module.home.bangumi.BangumiScheduleActivity;
import com.ryan.bilibili_client.module.home.bangumi.ChaseBangumiActivity;
import com.ryan.bilibili_client.widget.sectioned.StatelessSection;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MUFCRyan on 2017/6/5.
 * 首页番剧顶部追番，放送表，索引条目 Section
 */

public class HomeBangumiItemSection extends StatelessSection {
    private Context mContext;
    public HomeBangumiItemSection(Context context) {
        super(R.layout.layout_home_bangumi_top_item, R.layout.layout_home_recommend_empty);
        mContext = context;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new EmptyViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new TopItemViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        TopItemViewHolder topItemViewHolder = (TopItemViewHolder) holder;
        // 前往追番
        topItemViewHolder.mTvChaseBangumi.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, ChaseBangumiActivity.class)));
        // 前往番剧放送表
        topItemViewHolder.mTvBangumiSchedule.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, BangumiScheduleActivity.class)));
        // 前往番剧索引
        topItemViewHolder.mTvBangumiIndex.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, BangumiIndexActivity.class)));
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class TopItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.layout_chase_bangumi)
        TextView mTvChaseBangumi;

        @BindView(R.id.layout_bangumi_schedule)
        TextView mTvBangumiSchedule;

        @BindView(R.id.layout_bangumi_index)
        TextView mTvBangumiIndex;

        public TopItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
