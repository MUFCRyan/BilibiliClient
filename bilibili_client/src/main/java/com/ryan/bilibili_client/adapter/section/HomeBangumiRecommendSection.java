package com.ryan.bilibili_client.adapter.section;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.HomeBangumiRecommendAdapter;
import com.ryan.bilibili_client.entity.bangumi.BangumiRecommendInfo;
import com.ryan.bilibili_client.module.common.BrowserActivity;
import com.ryan.bilibili_client.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MUFCRyan on 2017/6/5.
 * 首页番剧推荐
 */

public class HomeBangumiRecommendSection extends StatelessSection {
    private Context mContext;
    private List<BangumiRecommendInfo.ResultBean> mRecommends;
    public HomeBangumiRecommendSection(Context context, List<BangumiRecommendInfo.ResultBean> recommends) {
        super(R.layout.layout_home_bangumi_recommend_head, R.layout.layout_home_recommend_empty);
        this.mContext = context;
        this.mRecommends = recommends;
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
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        RecyclerViewHolder recyclerViewHolder = (RecyclerViewHolder) holder;
        recyclerViewHolder.mRecyclerView.setHasFixedSize(false);
        recyclerViewHolder.mRecyclerView.setNestedScrollingEnabled(false);
        recyclerViewHolder.mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false));
        HomeBangumiRecommendAdapter recommendAdapter = new HomeBangumiRecommendAdapter(recyclerViewHolder.mRecyclerView, mRecommends);
        recyclerViewHolder.mRecyclerView.setAdapter(recommendAdapter);
        recommendAdapter.setOnItemClickListener(((position, holder1) -> BrowserActivity.launch((Activity) mContext, mRecommends.get(position).getLink(), mRecommends.get(position).getTitle())));
    }

    static class RecyclerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.home_bangumi_recommend_recycler)
        RecyclerView mRecyclerView;

        RecyclerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    private static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }
}
