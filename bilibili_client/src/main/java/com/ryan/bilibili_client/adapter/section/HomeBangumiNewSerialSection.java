package com.ryan.bilibili_client.adapter.section;

import android.app.Activity;
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
import com.ryan.bilibili_client.module.home.bangumi.BangumiDetailsActivity;
import com.ryan.bilibili_client.module.home.bangumi.NewSerialBangumiActivity;
import com.ryan.bilibili_client.utils.NumberUtil;
import com.ryan.bilibili_client.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MUFCRyan on 2017/6/5.
 * 首页番剧新番连载 Section
 */

public class HomeBangumiNewSerialSection extends StatelessSection {
    private Context mContext;
    private List<BangumiAppIndexInfo.ResultBean.SerializingBean> mSerials;
    public HomeBangumiNewSerialSection(Context context, List<BangumiAppIndexInfo.ResultBean.SerializingBean> serials) {
        super(R.layout.layout_home_bangumi_new_serial_head, R.layout.layout_home_bangumi_new_serial_body);
        this.mContext = context;
        mSerials = serials;
    }

    @Override
    public int getContentItemsTotal() {
        return mSerials.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        BangumiAppIndexInfo.ResultBean.SerializingBean serializingBean = mSerials.get(position);
        Glide.with(mContext)
                .load(serializingBean.getCover())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(itemViewHolder.mImage);
        itemViewHolder.mTitle.setText(serializingBean.getTitle());
        itemViewHolder.mPlay.setText(NumberUtil.converString(serializingBean.getWatching_count()) + "人在看");
        itemViewHolder.mUpdate.setText("更新至第" + serializingBean.getNewest_ep_index() + "话");
        itemViewHolder.mCardView.setOnClickListener(v -> BangumiDetailsActivity.launch((Activity) mContext, serializingBean.getSeason_id()));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        headerViewHolder.mAllSerial.setOnClickListener(v -> mContext.startActivity(new Intent(mContext, NewSerialBangumiActivity.class)));
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_all_serial)
        TextView mAllSerial;

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

        @BindView(R.id.item_update)
        TextView mUpdate;

        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
