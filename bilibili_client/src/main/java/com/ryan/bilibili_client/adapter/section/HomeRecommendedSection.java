package com.ryan.bilibili_client.adapter.section;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.entity.recommend.RecommendInfo;
import com.ryan.bilibili_client.module.home.bangumi.BangumiIndexActivity;
import com.ryan.bilibili_client.module.home.bangumi.BangumiScheduleActivity;
import com.ryan.bilibili_client.module.home.discover.OriginalRankActivity;
import com.ryan.bilibili_client.module.home.live.LivePlayerActivity;
import com.ryan.bilibili_client.module.video.VideoDetailsActivity;
import com.ryan.bilibili_client.utils.DisplayUtil;
import com.ryan.bilibili_client.widget.sectioned.StatelessSection;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by MUFCRyan on 2017/6/1.
 * 首页推荐界面
 */

public class HomeRecommendedSection extends StatelessSection {
    private static final String TYPE_RECOMMENDED = "recommend";
    private static final String TYPE_LIVE = "live";
    private static final String TYPE_BANGUMI = "bangumi_2";
    private static final String GOTO_BANGUMI = "bangumi_list";
    private static final String TYPE_ACTIVITY = "activity";

    private Context mContext;
    private String title, type;
    private int liveCount;
    private List<RecommendInfo.ResultBean.BodyBean> datas = new ArrayList<>();
    private final Random mRandom;
    private int[] icons = new int[] {
            R.drawable.ic_header_hot, R.drawable.ic_head_live,
            R.drawable.ic_category_t13, R.drawable.ic_category_t1,
            R.drawable.ic_category_t3, R.drawable.ic_category_t129,
            R.drawable.ic_category_t4, R.drawable.ic_category_t119,
            R.drawable.ic_category_t36, R.drawable.ic_category_t160,
            R.drawable.ic_category_t155, R.drawable.ic_category_t5,
            R.drawable.ic_category_t11, R.drawable.ic_category_t23
    };

    public HomeRecommendedSection(Context context, String title, String type, int liveCount, List<RecommendInfo.ResultBean.BodyBean> datas){
        super(R.layout.layout_home_recommend_head, R.layout.layout_home_recommend_foot, R.layout.layout_home_recommend_boby);
        this.mContext = context;
        this.title = title;
        this.type = type;
        this.liveCount = liveCount;
        this.datas = datas;
        mRandom = new Random();
    }

    @Override
    public int getContentItemsTotal() {
        return datas.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
        RecommendInfo.ResultBean.BodyBean bodyBean = datas.get(position);
        Glide.with(mContext)
                .load(bodyBean.getCover())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(itemViewHolder.mVideoImg);
        itemViewHolder.mVideoTitle.setText(bodyBean.getTitle());
        itemViewHolder.mCardView.setOnClickListener(v -> {
            String gotoX = bodyBean.getGotoX();
            switch(gotoX){
                case TYPE_LIVE:
                    LivePlayerActivity.launch((Activity) mContext, Integer.valueOf(bodyBean.getParam()), bodyBean.getTitle(), bodyBean.getOnline(), bodyBean.getUpFace(), bodyBean.getUp(), 0);
                    break;
                case GOTO_BANGUMI:
                    break;
                default :
                    VideoDetailsActivity.launch((Activity) mContext, Integer.parseInt(bodyBean.getParam()), bodyBean.getCover());
                    break;
            }
        });
        
        switch(type){
            case TYPE_LIVE:
                itemViewHolder.mLiveLayout.setVisibility(View.VISIBLE);
                itemViewHolder.mVideoLayout.setVisibility(View.GONE);
                itemViewHolder.mBangumiLayout.setVisibility(View.GONE);
                itemViewHolder.mLiveUp.setText(bodyBean.getUp());
                itemViewHolder.mLiveOnline.setText(String.valueOf(bodyBean.getOnline()));
                break;
            case TYPE_BANGUMI:
                itemViewHolder.mLiveLayout.setVisibility(View.GONE);
                itemViewHolder.mVideoLayout.setVisibility(View.GONE);
                itemViewHolder.mBangumiLayout.setVisibility(View.VISIBLE);
                itemViewHolder.mBangumiUpdate.setText(bodyBean.getDesc1());
                break;
            case TYPE_ACTIVITY:
                ViewGroup.LayoutParams params = itemViewHolder.mCardView.getLayoutParams();
                params.height = DisplayUtil.dp2px(mContext, 200f);
                itemViewHolder.mCardView.setLayoutParams(params);
                itemViewHolder.mLiveLayout.setVisibility(View.GONE);
                itemViewHolder.mVideoLayout.setVisibility(View.GONE);
                itemViewHolder.mBangumiLayout.setVisibility(View.GONE);
                break;
            default :
                itemViewHolder.mLiveLayout.setVisibility(View.GONE);
                itemViewHolder.mBangumiLayout.setVisibility(View.GONE);
                itemViewHolder.mVideoLayout.setVisibility(View.VISIBLE);
                itemViewHolder.mVideoPlayNum.setText(bodyBean.getPlay());
                itemViewHolder.mVideoReviewCount.setText(bodyBean.getDanmaku());
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
        setTypeIcon(viewHolder);
        viewHolder.mTypeTv.setText(title);
        viewHolder.mTypeRankBtn.setOnClickListener(v -> {
            mContext.startActivity(new Intent(mContext, OriginalRankActivity.class));
        });
        switch(type){
            case TYPE_RECOMMENDED:
                viewHolder.mTypeMore.setVisibility(View.GONE);
                viewHolder.mTypeRankBtn.setVisibility(View.VISIBLE);
                viewHolder.mAllLiveNum.setVisibility(View.GONE);
                break;
            case TYPE_LIVE:
                viewHolder.mTypeMore.setVisibility(View.VISIBLE);
                viewHolder.mTypeRankBtn.setVisibility(View.GONE);
                viewHolder.mAllLiveNum.setVisibility(View.VISIBLE);
                SpannableStringBuilder builder = new SpannableStringBuilder("当前" + liveCount + "个直播");
                ForegroundColorSpan colorSpan = new ForegroundColorSpan(mContext.getResources().getColor(R.color.pink_text_color));
                builder.setSpan(colorSpan, 2, builder.length() - 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                viewHolder.mAllLiveNum.setText(builder);
                break;
            default :
                viewHolder.mTypeMore.setVisibility(View.VISIBLE);
                viewHolder.mTypeRankBtn.setVisibility(View.GONE);
                viewHolder.mAllLiveNum.setVisibility(View.GONE);
                break;
        }
    }

    @Override
    public RecyclerView.ViewHolder getFooterViewHolder(View view) {
        return new FootViewHolder(view);
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {
        FootViewHolder viewHolder = (FootViewHolder) holder;
        viewHolder.mDynamic.setText(String.valueOf(mRandom.nextInt(200) + "条新动态，点这里刷新"));
        viewHolder.mRefreshBtn.setOnClickListener(v -> viewHolder.mRefreshBtn
                .animate()
                .rotation(360)
                .setInterpolator(new LinearInterpolator())
                .setDuration(1000)
                .start()
        );
        viewHolder.mRecommendRefresh.setOnClickListener(v -> viewHolder.mRecommendRefresh
                .animate()
                .rotation(360)
                .setInterpolator(new LinearInterpolator())
                .setDuration(1000)
                .start()
        );
        viewHolder.mBangumiIndexBtn.setOnClickListener(v -> mContext.startActivity(
                new Intent(mContext, BangumiIndexActivity.class)));
        viewHolder.mBangumiTimelineBtn.setOnClickListener(v -> mContext.startActivity(
                new Intent(mContext, BangumiScheduleActivity.class)));
        switch(type){
            case TYPE_RECOMMENDED:
                viewHolder.mMoreBtn.setVisibility(View.GONE);
                viewHolder.mRefreshLayout.setVisibility(View.GONE);
                viewHolder.mBangumiLayout.setVisibility(View.GONE);
                viewHolder.mRecommendRefreshLayout.setVisibility(View.VISIBLE);
                break;
            case TYPE_BANGUMI:
                viewHolder.mMoreBtn.setVisibility(View.GONE);
                viewHolder.mRefreshLayout.setVisibility(View.GONE);
                viewHolder.mBangumiLayout.setVisibility(View.VISIBLE);
                viewHolder.mRecommendRefreshLayout.setVisibility(View.GONE);
                break;
            case TYPE_ACTIVITY:
                viewHolder.mMoreBtn.setVisibility(View.GONE);
                viewHolder.mRefreshLayout.setVisibility(View.GONE);
                viewHolder.mBangumiLayout.setVisibility(View.GONE);
                viewHolder.mRecommendRefreshLayout.setVisibility(View.GONE);
                break;
            default :
                viewHolder.mMoreBtn.setVisibility(View.VISIBLE);
                viewHolder.mRefreshLayout.setVisibility(View.VISIBLE);
                viewHolder.mBangumiLayout.setVisibility(View.GONE);
                viewHolder.mRecommendRefreshLayout.setVisibility(View.GONE);
                break;
        }
    }

    private void setTypeIcon(HeaderViewHolder viewHolder) {
        switch (title) {
            case "热门焦点": // Hardcode...
                viewHolder.mTypeImg.setImageResource(icons[0]);
                break;
            case "正在直播":
                viewHolder.mTypeImg.setImageResource(icons[1]);
                break;
            case "番剧推荐":
                viewHolder.mTypeImg.setImageResource(icons[2]);
                break;
            case "动画区":
                viewHolder.mTypeImg.setImageResource(icons[3]);
                break;
            case "音乐区":
                viewHolder.mTypeImg.setImageResource(icons[4]);
                break;
            case "舞蹈区":
                viewHolder.mTypeImg.setImageResource(icons[5]);
                break;
            case "游戏区":
                viewHolder.mTypeImg.setImageResource(icons[6]);
                break;
            case "鬼畜区":
                viewHolder.mTypeImg.setImageResource(icons[7]);
                break;
            case "科技区":
                viewHolder.mTypeImg.setImageResource(icons[8]);
                break;
            case "生活区":
                viewHolder.mTypeImg.setImageResource(icons[9]);
                break;
            case "时尚区":
                viewHolder.mTypeImg.setImageResource(icons[10]);
                break;
            case "娱乐区":
                viewHolder.mTypeImg.setImageResource(icons[11]);
                break;
            case "电视剧区":
                viewHolder.mTypeImg.setImageResource(icons[12]);
                break;
            case "电影区":
                viewHolder.mTypeImg.setImageResource(icons[13]);
                break;
        }
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_type_img)
        ImageView mTypeImg;
        @BindView(R.id.item_type_tv)
        TextView mTypeTv;
        @BindView(R.id.item_type_more)
        TextView mTypeMore;
        @BindView(R.id.item_type_rank_btn)
        TextView mTypeRankBtn;
        @BindView(R.id.item_live_all_num)
        TextView mAllLiveNum;
        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.card_view)
        CardView mCardView;
        @BindView(R.id.video_preview)
        ImageView mVideoImg;
        @BindView(R.id.video_title)
        TextView mVideoTitle;
        @BindView(R.id.video_play_num)
        TextView mVideoPlayNum;
        @BindView(R.id.video_review_count)
        TextView mVideoReviewCount;
        @BindView(R.id.layout_live)
        RelativeLayout mLiveLayout;
        @BindView(R.id.layout_video)
        LinearLayout mVideoLayout;
        @BindView(R.id.item_live_up)
        TextView mLiveUp;
        @BindView(R.id.item_live_online)
        TextView mLiveOnline;
        @BindView(R.id.layout_bangumi)
        RelativeLayout mBangumiLayout;
        @BindView(R.id.item_bangumi_update)
        TextView mBangumiUpdate;
        public ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    static class FootViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.item_btn_more)
        Button mMoreBtn;
        @BindView(R.id.item_dynamic)
        TextView mDynamic;
        @BindView(R.id.item_btn_refresh)
        ImageView mRefreshBtn;
        @BindView(R.id.item_refresh_layout)
        LinearLayout mRefreshLayout;
        @BindView(R.id.item_recommend_refresh_layout)
        LinearLayout mRecommendRefreshLayout;
        @BindView(R.id.item_recommend_refresh)
        ImageView mRecommendRefresh;
        @BindView(R.id.item_bangumi_layout)
        LinearLayout mBangumiLayout;
        @BindView(R.id.item_btn_bangumi_index)
        ImageView mBangumiIndexBtn;
        @BindView(R.id.item_btn_bangumi_timeline)
        ImageView mBangumiTimelineBtn;
        FootViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
