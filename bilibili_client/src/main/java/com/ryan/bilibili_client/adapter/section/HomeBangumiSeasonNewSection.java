package com.ryan.bilibili_client.adapter.section;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.entity.bangumi.BangumiAppIndexInfo;
import com.ryan.bilibili_client.module.home.bangumi.SeasonNewBangumiActivity;
import com.ryan.bilibili_client.utils.NumberUtil;
import com.ryan.bilibili_client.widget.sectioned.StatelessSection;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MUFCRyan on 2017/6/5.
 * 首页番剧分季新番
 */

public class HomeBangumiSeasonNewSection extends StatelessSection {
    private Context mContext;
    private int mSeason;
    private List<BangumiAppIndexInfo.ResultBean.PreviousBean.ListBean> mSeasonNewBangumis;
    public HomeBangumiSeasonNewSection(Context context, int season, List<BangumiAppIndexInfo.ResultBean.PreviousBean.ListBean> seasonNewBangumis) {
        super(R.layout.layout_home_bangumi_season_new_head, R.layout.layout_home_bangumi_season_new_body);
        this.mContext = context;
        this.mSeason = season;
        this.mSeasonNewBangumis = seasonNewBangumis;
    }

    @Override
    public int getContentItemsTotal() {
        return mSeasonNewBangumis.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        BangumiAppIndexInfo.ResultBean.PreviousBean.ListBean listBean = mSeasonNewBangumis.get(position);
        Glide.with(mContext)
                .load(listBean.getCover())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(itemViewHolder.mImage);
        itemViewHolder.mTitle.setText(listBean.getTitle());
        itemViewHolder.mPlay.setText(NumberUtil.converString(Integer.valueOf(listBean.getFavourites())) + "人追番");
        itemViewHolder.mCardView.setOnClickListener(v -> {});
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        setSeasonIcon(headerViewHolder);
        headerViewHolder.mAllNewBangumi.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, SeasonNewBangumiActivity.class)));
    }

    @SuppressLint("SetTextI18n")
    private void setSeasonIcon(HeaderViewHolder viewHolder) {
        switch(mSeason){
            case 1: // 冬季
                viewHolder.mSeasonText.setText("1月新番");
                viewHolder.mSeasonIcon.setImageResource(R.drawable.bangumi_home_ic_season_1);
                break;
            case 2: // 春季
                viewHolder.mSeasonText.setText("4月新番");
                viewHolder.mSeasonIcon.setImageResource(R.drawable.bangumi_home_ic_season_2);
                break;
            case 3: // 夏季
                viewHolder.mSeasonText.setText("7月新番");
                viewHolder.mSeasonIcon.setImageResource(R.drawable.bangumi_home_ic_season_3);
                break;
            case 4: // 秋季
                viewHolder.mSeasonText.setText("10月新番");
                viewHolder.mSeasonIcon.setImageResource(R.drawable.bangumi_home_ic_season_4);
                break;
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_all_new_bangumi)
        TextView mAllNewBangumi;

        @BindView(R.id.iv_season)
        ImageView mSeasonIcon;

        @BindView(R.id.tv_season)
        TextView mSeasonText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view)
        LinearLayout mCardView;

        @BindView(R.id.item_img)
        ImageView mImage;

        @BindView(R.id.item_title)
        TextView mTitle;

        @BindView(R.id.item_play)
        TextView mPlay;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
