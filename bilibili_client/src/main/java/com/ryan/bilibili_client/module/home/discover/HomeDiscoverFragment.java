package com.ryan.bilibili_client.module.home.discover;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.discover.HotSearchTag;
import com.ryan.bilibili_client.module.common.BrowserActivity;
import com.ryan.bilibili_client.module.entry.GameCenterActivity;
import com.ryan.bilibili_client.module.search.TotalStationSearchActivity;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.zhy.view.flowlayout.FlowLayout;
import com.zhy.view.flowlayout.TagAdapter;
import com.zhy.view.flowlayout.TagFlowLayout;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by MUFCRyan on 2017/5/31.
 *
 */

public class HomeDiscoverFragment extends RxLazyFragment {

    @BindView(R.id.tags_layout)
    TagFlowLayout mTagFlowLayout;

    @BindView(R.id.hide_scroll_view)
    NestedScrollView mScrollView;

    @BindView(R.id.hide_tags_layout)
    TagFlowLayout mHideTagLayout;

    @BindView(R.id.more_layout)
    LinearLayout mMoreLayout;

    @BindView(R.id.tv_more)
    TextView mMoreText;

    private boolean mIsShowMore = true;

    private List<HotSearchTag.ListBean> mHotSearchTags = new ArrayList<>();

    public static HomeDiscoverFragment newInstance(){
        return new HomeDiscoverFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_discover;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mScrollView.setNestedScrollingEnabled(true);
        getTags();
    }

    private void getTags() {
        RetrofitHelper.getSearchAPI()
                .getHotSearchTags()
                .compose(bindToLifecycle())
                .map(HotSearchTag::getList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(listBeans -> {
                    mHotSearchTags.addAll(listBeans);
                    initTagLayout();
                }, throwable -> {});
    }

    private void initTagLayout() {
        // 获取热搜标签集合的前九个作默认显示
        List<HotSearchTag.ListBean> defaultHotTags = mHotSearchTags.subList(0, 8);
        mTagFlowLayout.setAdapter(new TagAdapter<HotSearchTag.ListBean>(defaultHotTags){

            @Override
            public View getView(FlowLayout parent, int position, HotSearchTag.ListBean listBean) {
                TextView tag = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_tags_item, parent, false);
                tag.setText(listBean.getKeyword());
                tag.setOnClickListener(v -> TotalStationSearchActivity.launch(getActivity(), listBean.getKeyword()));
                return tag;
            }
        });
        mHideTagLayout.setAdapter(new TagAdapter<HotSearchTag.ListBean>(mHotSearchTags){

            @Override
            public View getView(FlowLayout parent, int position, HotSearchTag.ListBean listBean) {
                TextView tag = (TextView) LayoutInflater.from(getActivity()).inflate(R.layout.layout_tags_item, parent, false);
                tag.setText(listBean.getKeyword());
                tag.setOnClickListener(v -> TotalStationSearchActivity.launch(getActivity(), listBean.getKeyword()));
                return tag;
            }
        });
    }

    @OnClick(R.id.more_layout)
    void showAndHideMoreLayout(){
        if (mIsShowMore){
            mIsShowMore = false;
            mScrollView.setVisibility(View.VISIBLE);
            mMoreText.setText("收起");
            mTagFlowLayout.setVisibility(View.GONE);
            Drawable upDrawable = getResources().getDrawable(R.drawable.ic_arrow_up_gray_round);
            upDrawable.setBounds(0, 0, upDrawable.getMinimumWidth(), upDrawable.getMinimumHeight());
            mMoreText.setCompoundDrawables(upDrawable, null, null, null);
        } else {
            mIsShowMore = true;
            mScrollView.setVisibility(View.GONE);
            mMoreText.setText("查看更多");
            mTagFlowLayout.setVisibility(View.VISIBLE);
            Drawable downDrawable = getResources().getDrawable(R.drawable.ic_arrow_down_gray_round);
            downDrawable.setBounds(0, 0, downDrawable.getMinimumWidth(), downDrawable.getMinimumHeight());
            mMoreText.setCompoundDrawables(downDrawable, null, null, null);
        }
    }

    /** 前往话题中心页面 */
    @OnClick(R.id.topic_center_layout)
    void startTopicCenterActivity(){
        startActivity(new Intent(getActivity(), TopicCenterActivity.class));
    }

    /** 前往活动中心界面 */
    @OnClick(R.id.activity_center_layout)
    void startActivityCenterActivity() {
        startActivity(new Intent(getActivity(), ActivityCenterActivity.class));
    }


    /** 前往全区排行榜界面 */
    @OnClick(R.id.layout_all_rank)
    void startAllRankActivity() {
        startActivity(new Intent(getActivity(), AllAreasRankActivity.class));
    }


    /** 前往原创排行榜界面 */
    @OnClick(R.id.layout_original)
    void startOriginalRankActivity() {
        startActivity(new Intent(getActivity(), OriginalRankActivity.class));
    }


    /** 前往游戏中心界面 */
    @OnClick(R.id.layout_game_center)
    void startGameCenterActivity() {
        startActivity(new Intent(getActivity(), GameCenterActivity.class));
    }


    /** 前往搜索界面 */
    @OnClick(R.id.card_view)
    void startSearchActivity() {
        startActivity(new Intent(getActivity(), TotalStationSearchActivity.class));
    }

    /** 前往商城 */
    @OnClick(R.id.layout_shop)
    void startShop() {
        BrowserActivity.launch(getActivity(), ConstantUtil.SHOP_URL, "bilibili - 周边商城");
    }
}
