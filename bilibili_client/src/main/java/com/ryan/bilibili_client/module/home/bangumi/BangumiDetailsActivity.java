package com.ryan.bilibili_client.module.home.bangumi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.BangumiDetailsCommentAdapter;
import com.ryan.bilibili_client.adapter.BangumiDetailsHotCommentAdapter;
import com.ryan.bilibili_client.adapter.BangumiDetailsRecommendAdapter;
import com.ryan.bilibili_client.adapter.BangumiDetailsSeasonsAdapter;
import com.ryan.bilibili_client.adapter.BangumiDetailsSelectionAdapter;
import com.ryan.bilibili_client.adapter.helper.HeaderViewRecyclerAdapter;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.bangumi.BangumiDetailsCommentInfo;
import com.ryan.bilibili_client.entity.bangumi.BangumiDetailsInfo;
import com.ryan.bilibili_client.entity.bangumi.BangumiDetailsRecommendInfo;
import com.ryan.bilibili_client.module.video.VideoDetailsActivity;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.LogUtil;
import com.ryan.bilibili_client.utils.NumberUtil;
import com.ryan.bilibili_client.utils.SystemBarHelper;
import com.ryan.bilibili_client.widget.CircleProgressView;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class BangumiDetailsActivity extends RxBaseActivity {

    @BindView(R.id.nested_scroll_view)
    NestedScrollView mNestedScrollView;

    @BindView(R.id.bangumi_bg)
    ImageView mIvBangumiBg;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.bangumi_pic)
    ImageView mIvBangumiPic;

    @BindView(R.id.bangumi_details_layout)
    LinearLayout mLlDetailsLayout;

    @BindView(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    @BindView(R.id.bangumi_title)
    TextView mTvBangumiTitle;

    @BindView(R.id.bangumi_update)
    TextView mTvBangumiUpdate;

    @BindView(R.id.bangumi_play)
    TextView mTvBangumiPlay;

    @BindView(R.id.bangumi_selection_recycler)
    RecyclerView mBangumiSelectionRecycler;

    @BindView(R.id.tags_layout)
    TagFlowLayout mTagFlowLayout;

    @BindView(R.id.bangumi_details_introduction)
    TextView mTvBangumiIntroduction;

    @BindView(R.id.tv_update_index)
    TextView mTvUpdateIndex;

    @BindView(R.id.bangumi_seasons_recycler)
    RecyclerView mBangumiSeasonsRecycler;

    @BindView(R.id.bangumi_comment_recycler)
    RecyclerView mBangumiCommentRecycler;

    @BindView(R.id.bangumi_recommend_recycler)
    RecyclerView mBangumiRecommendRecycler;

    @BindView(R.id.tv_bangumi_comment_count)
    TextView mTvBangumiCommentCount;

    private int mSeasonId;
    private BangumiDetailsInfo.ResultBean mResult;
    private List<BangumiDetailsRecommendInfo.ResultBean.ListBean> mBangumiRecommends = new ArrayList<>();
    private List<BangumiDetailsCommentInfo.DataBean.HotsBean> mHotComments = new ArrayList<>();
    private List<BangumiDetailsCommentInfo.DataBean.RepliesBean> mReplies = new ArrayList<>();
    private BangumiDetailsCommentInfo.DataBean.PageBean mPageInfo;

    public static void launch(Activity activity, int seasonId){
        Intent mIntent = new Intent(activity, BangumiDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt(ConstantUtil.EXTRA_BANGUMI_KEY, seasonId);
        mIntent.putExtras(bundle);
        activity.startActivity(mIntent);
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_bangumi_details;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null)
            mSeasonId = intent.getIntExtra(ConstantUtil.EXTRA_BANGUMI_KEY, 0);
        loadData();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("番剧详情");
        setSupportActionBar(mToolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // 设置 Toolbar 的透明度
        mToolbar.setBackgroundColor(Color.argb(0, 251, 114, 163));

        // 设置 StatusBar 的透明度
        SystemBarHelper.immersiveStatusBar(this);
        SystemBarHelper.setPadding(this, mToolbar);

        //获取顶部image高度和toolbar高度
        float imageHeight = getResources().getDimension(R.dimen.bangumi_details_top_layout_height);
        float toolBarHeight = getResources().getDimension(R.dimen.action_bar_default_height);

        mNestedScrollView.setNestedScrollingEnabled(true);
        mNestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
            @Override
            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                // 根据 NestedScrollView 滑动改变 Toolbar 的透明度
                float offsetY = toolBarHeight - imageHeight;
                //计算滑动距离的偏移量
                float offset = 1 - Math.max((offsetY - scrollY) / offsetY, 0f);
                float absOffset = Math.abs(offset);
                //如果滑动距离大于1就设置完全不透明度
                if (absOffset >= 1) {
                    absOffset = 1;
                }
                mToolbar.setBackgroundColor(Color.argb((int) (absOffset * 255), 251, 114, 153));
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_bangumi_details, menu);
        return true;
    }


    @Override
    public void showProgressBar() {

        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
        mLlDetailsLayout.setVisibility(View.GONE);
    }


    @Override
    public void hideProgressBar() {

        mCircleProgressView.setVisibility(View.GONE);
        mCircleProgressView.stopSpinning();
        mLlDetailsLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void loadData() {
        RetrofitHelper.getBangumiAPI()
                .getBangumiDetails()
                .compose(bindToLifecycle())
                .doOnSubscribe(this::showProgressBar)
                .flatMap(new Func1<BangumiDetailsInfo, Observable<BangumiDetailsRecommendInfo>>() {
                    @Override
                    public Observable<BangumiDetailsRecommendInfo> call(BangumiDetailsInfo bangumiDetailsInfo) {
                        mResult = bangumiDetailsInfo.getResult();
                        return RetrofitHelper.getBangumiAPI().getBangumiDetailsRecommend();
                    }
                })
                .compose(bindToLifecycle())
                .map(bangumiDetailsRecommendInfo -> bangumiDetailsRecommendInfo.getResult().getList())
                .flatMap(new Func1<List<BangumiDetailsRecommendInfo.ResultBean.ListBean>, Observable<BangumiDetailsCommentInfo>>() {

                    @Override
                    public Observable<BangumiDetailsCommentInfo> call(List<BangumiDetailsRecommendInfo.ResultBean.ListBean> listBeans) {
                        mBangumiRecommends.addAll(listBeans);
                        return RetrofitHelper.getBiliAPI().getBangumiDetailsComments();
                    }
                })
                .compose(bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(bangumiDetailsCommentInfo -> {
                    mHotComments.addAll(bangumiDetailsCommentInfo.getData().getHots());
                    mReplies.addAll(bangumiDetailsCommentInfo.getData().getReplies());
                    mPageInfo = bangumiDetailsCommentInfo.getData().getPage();
                    finishTask();
                }, throwable -> {
                    LogUtil.all(throwable.getMessage());
                    hideProgressBar();
                });
    }

    @Override
    public void finishTask() {
        // 设置番剧封面
        Glide.with(this)
                .load(mResult.getCover())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .placeholder(R.drawable.bili_default_image_tv)
                .dontAnimate()
                .into(mIvBangumiPic);

        // 设置背景高斯模糊图片
        Glide.with(this)
                .load(mResult.getCover())
                .bitmapTransform(new BlurTransformation(this))
                .into(mIvBangumiBg);

        // 设置番剧标题
        mTvBangumiTitle.setText(mResult.getTitle());
        // 设置番剧更新状态
        if (mResult.getIs_finish().equals("0")){
            mTvUpdateIndex.setText("更新至第" + mResult.getNewest_ep_index() + "话");
            mTvBangumiUpdate.setText("连载中");
        } else {
            mTvUpdateIndex.setText(mResult.getNewest_ep_index() + "话全");
            mTvBangumiUpdate.setText("已完结" + mResult.getNewest_ep_index() + "话全");
        }

        // 设置番剧播放和追番数量
        mTvBangumiPlay.setText("播放：" + NumberUtil.converString(Integer.valueOf(mResult.getPlay_count()))
                + "  追番：" + NumberUtil.converString(Integer.valueOf(mResult.getFavorites())));

        //设置番剧简介
        mTvBangumiIntroduction.setText(mResult.getEvaluate());
        //设置评论数量
        mTvBangumiCommentCount.setText("评论 第1话(" + mPageInfo.getAcount() + ")");
        //设置标签布局
        List<BangumiDetailsInfo.ResultBean.TagsBean> tags = mResult.getTags();
        mTagFlowLayout.setAdapter(new TagAdapter<BangumiDetailsInfo.ResultBean.TagsBean>(tags) {

            @Override
            public View getView(FlowLayout parent, int position, BangumiDetailsInfo.ResultBean.TagsBean tagsBean) {
                TextView tvTag = (TextView) LayoutInflater.from(BangumiDetailsActivity.this).inflate(R.layout.layout_tags_item, parent, false);
                tvTag.setText(tagsBean.getTag_name());
                return tvTag;
            }
        });

        //设置番剧分季版本
        initSeasonsRecycler();
        //设置番剧选集
        initSelectionRecycler();
        //设置番剧推荐
        initRecommendRecycler();
        //设置番剧评论
        initCommentRecycler();
        //加载完毕隐藏进度条
        hideProgressBar();
    }

    private void initSeasonsRecycler() {
        List<BangumiDetailsInfo.ResultBean.SeasonsBean> seasons = mResult.getSeasons();
        mBangumiSeasonsRecycler.setHasFixedSize(false);
        mBangumiSeasonsRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mBangumiSeasonsRecycler.setLayoutManager(manager);
        BangumiDetailsSeasonsAdapter seasonsAdapter = new BangumiDetailsSeasonsAdapter(mBangumiSeasonsRecycler, seasons);
        mBangumiSeasonsRecycler.setAdapter(seasonsAdapter);
        for (int i = 0; i < seasons.size(); i++) {
            if (seasons.get(i).getSeason_id().equals(mResult.getSeason_id())){
                seasonsAdapter.notifyItemForeground(i);
            }
        }
    }

    private void initSelectionRecycler() {
        List<BangumiDetailsInfo.ResultBean.EpisodesBean> episodes = mResult.getEpisodes();
        mBangumiSelectionRecycler.setHasFixedSize(false);
        mBangumiSelectionRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, true);
        mBangumiSelectionRecycler.setLayoutManager(manager);
        BangumiDetailsSelectionAdapter selectionAdapter = new BangumiDetailsSelectionAdapter(mBangumiSelectionRecycler, episodes);
        mBangumiSelectionRecycler.setAdapter(selectionAdapter);
        selectionAdapter.notifyItemForeground(episodes.size() - 1);
        mBangumiSelectionRecycler.scrollToPosition(episodes.size() - 1);
        selectionAdapter.setOnItemClickListener(((position, holder) -> {
            selectionAdapter.notifyItemForeground(holder.getLayoutPosition());
            VideoDetailsActivity.launch(BangumiDetailsActivity.this, Integer.valueOf(episodes.get(position).getAv_id()), episodes.get(position).getCover());
        }));
    }

    private void initRecommendRecycler() {
        mBangumiRecommendRecycler.setHasFixedSize(false);
        mBangumiRecommendRecycler.setNestedScrollingEnabled(false);
        mBangumiRecommendRecycler.setLayoutManager(new GridLayoutManager(BangumiDetailsActivity.this, 3));
        BangumiDetailsRecommendAdapter recommendAdapter = new BangumiDetailsRecommendAdapter(mBangumiRecommendRecycler, mBangumiRecommends);
        mBangumiRecommendRecycler.setAdapter(recommendAdapter);
    }

    private void initCommentRecycler() {
        mBangumiCommentRecycler.setHasFixedSize(false);
        mBangumiCommentRecycler.setNestedScrollingEnabled(false);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this);
        mBangumiCommentRecycler.setLayoutManager(mLinearLayoutManager);
        BangumiDetailsCommentAdapter commentAdapter = new BangumiDetailsCommentAdapter(mBangumiCommentRecycler, mReplies);
        HeaderViewRecyclerAdapter headerAdapter = new HeaderViewRecyclerAdapter(commentAdapter);
        View headerView = LayoutInflater.from(this).inflate(R.layout.layout_video_hot_comment_head, mBangumiCommentRecycler, false);
        RecyclerView hotCommentRecycler = (RecyclerView) headerView.findViewById(R.id.hot_comment_recycler);
        hotCommentRecycler.setHasFixedSize(false);
        hotCommentRecycler.setNestedScrollingEnabled(false);
        hotCommentRecycler.setLayoutManager(new LinearLayoutManager(this));
        BangumiDetailsHotCommentAdapter hotCommentAdapter = new BangumiDetailsHotCommentAdapter(hotCommentRecycler, mHotComments);
        hotCommentRecycler.setAdapter(hotCommentAdapter);
        headerAdapter.addHeaderView(headerView);
        mBangumiCommentRecycler.setAdapter(headerAdapter);
    }
}
