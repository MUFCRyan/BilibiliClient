package com.ryan.bilibili_client.adapter.section;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.entity.bangumi.BangumiAppIndexInfo;
import com.ryan.bilibili_client.module.common.BrowserActivity;
import com.ryan.bilibili_client.widget.sectioned.StatelessSection;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MUFCRyan on 2017/6/5.
 * 首页番剧界面内容
 */

public class HomeBangumiBodySection extends StatelessSection {
    private Context mContext;
    private List<BangumiAppIndexInfo.ResultBean.AdBean.BodyBean> mBodies;
    public HomeBangumiBodySection(Context context, List<BangumiAppIndexInfo.ResultBean.AdBean.BodyBean> bodies) {
        super(R.layout.layout_home_bangumi_boby, R.layout.layout_home_recommend_empty);
        this.mContext = context;
        this.mBodies = bodies;
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
        return new BangumiBodyViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        BangumiBodyViewHolder bodyViewHolder = (BangumiBodyViewHolder) holder;
        Glide.with(mContext)
                .load(mBodies.get(0).getImg())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(bodyViewHolder.mBobyImage);
        bodyViewHolder.mCardView.setOnClickListener(v -> BrowserActivity.launch((Activity) mContext, mBodies.get(0).getLink(), mBodies.get(0).getTitle()));
    }

    static class EmptyViewHolder extends RecyclerView.ViewHolder {
        EmptyViewHolder(View itemView) {
            super(itemView);
        }
    }

    static class BangumiBodyViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.home_bangumi_boby_image)
        ImageView mBobyImage;

        @BindView(R.id.card_view)
        CardView mCardView;

        BangumiBodyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
