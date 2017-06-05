package com.ryan.bilibili_client.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.helper.AbsRecyclerViewAdapter;
import com.ryan.bilibili_client.entity.bangumi.BangumiRecommendInfo;
import java.util.List;

/**
 * Created by MUFCRyan on 2017/6/5.
 * 首页番剧推荐 adapter
 */

public class HomeBangumiRecommendAdapter extends AbsRecyclerViewAdapter {
    private List<BangumiRecommendInfo.ResultBean> mResultRecommends;
    public HomeBangumiRecommendAdapter(RecyclerView recyclerView, List<BangumiRecommendInfo
            .ResultBean> resultRecommends) {
        super(recyclerView);
        this.mResultRecommends = resultRecommends;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_bangumi_recommend, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            BangumiRecommendInfo.ResultBean resultBean = mResultRecommends.get(position);
            Glide.with(getContext())
                    .load(resultBean.getCover())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.bili_default_image_tv)
                    .dontAnimate()
                    .into(itemViewHolder.mImage);
            itemViewHolder.mTitle.setText(resultBean.getTitle());
            itemViewHolder.mDesc.setText(resultBean.getDesc());
            if (resultBean.getIs_new() == 1)
                itemViewHolder.mIsNew.setVisibility(View.VISIBLE);
            else
                itemViewHolder.mIsNew.setVisibility(View.GONE);
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mResultRecommends.size();
    }

    private class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {
        ImageView mImage;
        TextView mTitle;
        TextView mDesc;
        ImageView mIsNew;
        public ItemViewHolder(View itemView) {
            super(itemView);
            mImage = $(R.id.item_img);
            mTitle = $(R.id.item_title);
            mDesc = $(R.id.item_desc);
            mIsNew = $(R.id.item_is_new);
        }
    }
}
