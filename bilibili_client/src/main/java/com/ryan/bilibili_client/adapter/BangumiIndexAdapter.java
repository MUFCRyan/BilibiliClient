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
import com.ryan.bilibili_client.entity.bangumi.BangumiIndexInfo;

import java.util.List;

/**
 * Created by MUFCRyan on 2017/6/7.
 * 番剧索引 adapter
 */

public class BangumiIndexAdapter extends AbsRecyclerViewAdapter {
    private List<BangumiIndexInfo.ResultBean.CategoryBean> mCategoties;
    public BangumiIndexAdapter(RecyclerView recyclerView, List<BangumiIndexInfo.ResultBean.CategoryBean> categories) {
        super(recyclerView);
        mCategoties = categories;
    }

    @Override
    public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        bindContext(parent.getContext());
        return new ItemViewHolder(LayoutInflater.from(getContext()).inflate(R.layout.item_bangumi_index, parent, false));
    }

    @Override
    public void onBindViewHolder(ClickableViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder){
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            BangumiIndexInfo.ResultBean.CategoryBean categoryBean = mCategoties.get(position);
            Glide.with(getContext())
                    .load(categoryBean.getCover())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.bili_default_image_tv)
                    .dontAnimate()
                    .into(itemViewHolder.mImageView);
            itemViewHolder.mTextView.setText(categoryBean.getTag_name());
        }
        super.onBindViewHolder(holder, position);
    }

    @Override
    public int getItemCount() {
        return mCategoties.size();
    }

    public class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {
        ImageView mImageView;
        TextView mTextView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            mImageView = $(R.id.item_img);
            mTextView = $(R.id.item_title);
        }
    }
}
