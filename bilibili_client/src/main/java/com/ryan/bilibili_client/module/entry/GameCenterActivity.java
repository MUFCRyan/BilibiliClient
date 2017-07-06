package com.ryan.bilibili_client.module.entry;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.GameCentreAdapter;
import com.ryan.bilibili_client.adapter.helper.HeaderViewRecyclerAdapter;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.entity.discover.GameCenterInfo;
import com.ryan.bilibili_client.entity.discover.VipGameInfo;
import com.ryan.bilibili_client.module.common.BrowserActivity;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.widget.CircleProgressView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class GameCenterActivity extends RxBaseActivity {

    @BindView(R.id.recycle)
    RecyclerView mRecycle;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.circle_progress)
    CircleProgressView mCircleProgressView;

    private List<GameCenterInfo.ItemsBean> mItems = new ArrayList<>();
    private HeaderViewRecyclerAdapter mHeaderViewRecyclerAdapter;
    private ImageView mVipGameImage;
    private VipGameInfo.DataBean mVipGameInfoData;

    @Override
    public int getLayoutId() {
        return R.layout.activity_game_center;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        loadData();
    }

    @Override
    public void initToolBar() {
        mToolbar.setTitle("游戏中心");
        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void loadData() {
        RetrofitHelper.getVipAPI()
                .getVipGame()
                .compose(bindToLifecycle())
                .doOnSubscribe(this::showProgressBar)
                .delay(2000, TimeUnit.MILLISECONDS)
                .flatMap(new Func1<VipGameInfo, Observable<String>>() {
                    @Override
                    public Observable<String> call(VipGameInfo vipGameInfo) {
                        mVipGameInfoData = vipGameInfo.getData();
                        return Observable.just(readAssetsJson());
                    }
                })
                .compose(this.bindToLifecycle())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                    GameCenterInfo gameCenterInfo = new Gson().fromJson(s, GameCenterInfo.class);
                    mItems.addAll(gameCenterInfo.getItems());
                    finishTask();
                }, throwable -> {
                    hideProgressBar();
                });
    }

    private String readAssetsJson() {
        AssetManager manager = getAssets();
        try {
            InputStream is = manager.open("gamecenter.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String string;
            while ((string = reader.readLine()) != null){
                builder.append(string);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void finishTask() {
        initRecyclerView();
        hideProgressBar();
    }

    @Override
    public void initRecyclerView() {
        mRecycle.setHasFixedSize(true);
        mRecycle.setLayoutManager(new LinearLayoutManager(GameCenterActivity.this));
        GameCentreAdapter centreAdapter = new GameCentreAdapter(mRecycle, mItems);
        mHeaderViewRecyclerAdapter = new HeaderViewRecyclerAdapter(centreAdapter);
        createHeadView();
        mRecycle.setAdapter(mHeaderViewRecyclerAdapter);
    }

    private void createHeadView() {
        View headView = LayoutInflater.from(this).inflate(R.layout.layout_vip_game_head_view, mRecycle, false);
        mVipGameImage = (ImageView) headView.findViewById(R.id.vip_game_image);
        Glide.with(this)
                .load(mVipGameInfoData.getImgPath())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mVipGameImage);
        mVipGameImage.setOnClickListener(v -> BrowserActivity.launch(this, mVipGameInfoData
                .getLink(), "年度大会员游戏礼包专区"));
        mHeaderViewRecyclerAdapter.addHeaderView(headView);
    }

    @Override
    public void showProgressBar() {
        mCircleProgressView.setVisibility(View.VISIBLE);
        mCircleProgressView.spin();
        mRecycle.setVisibility(View.GONE);
    }

    @Override
    public void hideProgressBar() {
        mCircleProgressView.setVisibility(View.GONE);
        mCircleProgressView.stopSpinning();
        mRecycle.setVisibility(View.VISIBLE);
    }
}
