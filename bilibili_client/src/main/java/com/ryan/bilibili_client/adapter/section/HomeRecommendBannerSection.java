package com.ryan.bilibili_client.adapter.section;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.widget.banner.BannerEntity;
import com.ryan.bilibili_client.widget.banner.BannerView;
import com.ryan.bilibili_client.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MUFCRyan on 2017/6/1.
 *
 */

public class HomeRecommendBannerSection extends StatelessSection {
    private List<BannerEntity> mBanners;
    public HomeRecommendBannerSection(List<BannerEntity> banners) {
        super(R.layout.layout_banner, R.layout.layout_home_recommend_empty);
        mBanners = banners;
    }

    @Override
    public int getContentItemsTotal() {
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new BannerViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        BannerViewHolder bannerViewHolder = (BannerViewHolder) holder;
        bannerViewHolder.mBannerView.delayTime(5).build(mBanners);
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        public ItemViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class BannerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.home_recommended_banner)
        BannerView mBannerView;

        BannerViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
