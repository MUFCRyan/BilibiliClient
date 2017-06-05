package com.ryan.bilibili_client.adapter.section;

import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.entity.bangumi.BangumiScheduleInfo;
import com.ryan.bilibili_client.utils.DateUtil;
import com.ryan.bilibili_client.utils.WeekDayUtil;
import com.ryan.bilibili_client.widget.sectioned.StatelessSection;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.ryan.bilibili_client.utils.ConstantUtil.FRIDAY_TYEP;
import static com.ryan.bilibili_client.utils.ConstantUtil.MONDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.SATURDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.SUNDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.THURSDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.TUESDAY_TYPE;
import static com.ryan.bilibili_client.utils.ConstantUtil.WEDNESDAY_TYPE;

/**
 * Created by MUFCRyan on 2017/6/5.
 * 番剧放送表 section
 */

public class BangumiScheduleSection extends StatelessSection {
    private Context mContext;
    private List<BangumiScheduleInfo.ResultBean> mBangumiShcedules;
    private String mWeekDay, mDate;
    public BangumiScheduleSection(Context context, List<BangumiScheduleInfo.ResultBean> bangumiSchedules, String weekDay,  String date) {
        super(R.layout.layout_bangumi_schedule_head, R.layout.layout_bangumi_schedule_boby);
        mContext = context;
        mBangumiShcedules = bangumiSchedules;
        mWeekDay = weekDay;
        mDate = date;
    }

    @Override
    public int getContentItemsTotal() {
        return mBangumiShcedules.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        BangumiScheduleInfo.ResultBean schedule = mBangumiShcedules.get(position);
        Glide.with(mContext)
                .load(schedule.getCover())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(itemViewHolder.mImageView);
        itemViewHolder.mTitle.setText(schedule.getTitle());
        itemViewHolder.mUpdate.setText("第" + schedule.getEp_index() + "话");
        itemViewHolder.mTimeLine.setText(schedule.getOntime());
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder headerViewHolder = (HeaderViewHolder) holder;
        setWeekDay(headerViewHolder);
    }

    private void setWeekDay(HeaderViewHolder viewHolder) {
        switch(mWeekDay){
            case SUNDAY_TYPE:
                setWeekDayIconAndTitle(viewHolder, R.drawable.bangumi_timeline_weekday_7, "周日");
                break;
            case MONDAY_TYPE:
                setWeekDayIconAndTitle(viewHolder, R.drawable.bangumi_timeline_weekday_1, "周一");
                break;
            case TUESDAY_TYPE:
                setWeekDayIconAndTitle(viewHolder, R.drawable.bangumi_timeline_weekday_2, "周二");
                break;
            case WEDNESDAY_TYPE:
                setWeekDayIconAndTitle(viewHolder, R.drawable.bangumi_timeline_weekday_3, "周三");
                break;
            case THURSDAY_TYPE:
                setWeekDayIconAndTitle(viewHolder, R.drawable.bangumi_timeline_weekday_4, "周四");
                break;
            case FRIDAY_TYEP:
                setWeekDayIconAndTitle(viewHolder, R.drawable.bangumi_timeline_weekday_5, "周五");
                break;
            case SATURDAY_TYPE:
                setWeekDayIconAndTitle(viewHolder, R.drawable.bangumi_timeline_weekday_6, "周六");
                break;
        }
    }

    private void setWeekDayIconAndTitle(HeaderViewHolder viewHolder, int iconRes, String title) {
        if (mDate.equals(WeekDayUtil.formatDate(DateUtil.getCurrentTime("yyyy-MM-dd")))){
            viewHolder.mUpdateTime.setText("今天");
            viewHolder.mUpdateTime.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            viewHolder.mWeekDayText.setTextColor(mContext.getResources().getColor(R.color.colorPrimary));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.mWeekDayIcon.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.colorPrimary)));
            }
        } else {
            viewHolder.mUpdateTime.setText(mDate);
            viewHolder.mUpdateTime.setTextColor(mContext.getResources().getColor(R.color.black_alpha_30));
            viewHolder.mWeekDayText.setTextColor(mContext.getResources().getColor(R.color.gray_80));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                viewHolder.mWeekDayIcon.setImageTintList(ColorStateList.valueOf(mContext.getResources().getColor(R.color.gray_80)));
            }
        }
        viewHolder.mWeekDayIcon.setImageResource(iconRes);
        viewHolder.mWeekDayText.setText(title);
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_weekday_text)
        TextView mWeekDayText;

        @BindView(R.id.item_weekday_icon)
        ImageView mWeekDayIcon;

        @BindView(R.id.item_update_time)
        TextView mUpdateTime;

        HeaderViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_img)
        ImageView mImageView;

        @BindView(R.id.item_title)
        TextView mTitle;

        @BindView(R.id.item_update)
        TextView mUpdate;

        @BindView(R.id.item_time_line)
        TextView mTimeLine;

        public ItemViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}
