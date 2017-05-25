package com.hotbitmapgg.bilibili.adapter;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hotbitmapgg.bilibili.adapter.helper.AbsRecyclerViewAdapter;
import com.hotbitmapgg.bilibili.entity.user.UserChaseBangumiInfo;
import com.hotbitmapgg.ohmybilibili.R;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by hcc on 2016/10/12 19:08
 * 100332338@qq.com
 * <p>
 * 用户详情追番adapter
 */

public class UserChaseBangumiAdapter extends AbsRecyclerViewAdapter {

  private List<UserChaseBangumiInfo.DataBean.ResultBean> userChaseBangumis;


  public UserChaseBangumiAdapter(RecyclerView recyclerView, List<UserChaseBangumiInfo.DataBean.ResultBean> userChaseBangumis) {

    super(recyclerView);
    this.userChaseBangumis = userChaseBangumis;
  }


  @Override
  public ClickableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

    bindContext(parent.getContext());
    return new ItemViewHolder(
        LayoutInflater.from(getContext()).inflate(R.layout.item_user_chase_bangumi, parent, false));
  }


  @SuppressLint("SetTextI18n")
  @Override
  public void onBindViewHolder(ClickableViewHolder holder, int position) {

    if (holder instanceof ItemViewHolder) {
      ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
      UserChaseBangumiInfo.DataBean.ResultBean resultBean = userChaseBangumis.get(position);

      Glide.with(getContext())
          .load(resultBean.getCover())
          .centerCrop()
          .diskCacheStrategy(DiskCacheStrategy.ALL)
          .placeholder(R.drawable.bili_default_image_tv)
          .into(itemViewHolder.mImage);

      if (resultBean.getIs_finish() == 1) {
        itemViewHolder.mUpdate.setText(resultBean.getTotal_count() + "话全");
      } else {
        itemViewHolder.mUpdate.setText("更新至第" + resultBean.getTotal_count() + "话");
      }

      itemViewHolder.mTitle.setText(resultBean.getTitle());
    }

    super.onBindViewHolder(holder, position);
  }


  @Override
  public int getItemCount() {

    return userChaseBangumis.size();
  }


  private class ItemViewHolder extends AbsRecyclerViewAdapter.ClickableViewHolder {

    ImageView mImage;

    TextView mTitle;

    TextView mUpdate;


    public ItemViewHolder(View itemView) {

      super(itemView);
      mImage = $(R.id.item_img);
      mTitle = $(R.id.item_title);
      mUpdate = $(R.id.item_update);
    }
  }
}
