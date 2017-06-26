package com.ryan.bilibili_client.module.home.region;

import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.google.gson.Gson;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.HomeRegionItemAdapter;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.entity.region.RegionTypesInfo;
import com.ryan.bilibili_client.module.entry.GameCenterActivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by MUFCRyan on 2017/5/31.
 *
 */

public class HomeRegionFragment extends RxLazyFragment {

    @BindView(R.id.recycle)
    RecyclerView mRecyclerView;

    private List<RegionTypesInfo.DataBean> mRegionTypes = new ArrayList<>();

    public static HomeRegionFragment newInstance(){
        return new HomeRegionFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_region;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        initRecyclerView();
        loadData();
    }

    @Override
    protected void initRecyclerView() {
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        HomeRegionItemAdapter regionItemAdapter = new HomeRegionItemAdapter(mRecyclerView);
        mRecyclerView.setAdapter(regionItemAdapter);
        regionItemAdapter.setOnItemClickListener((position, holder) -> {
            switch(position){
                case 0:
                    //直播
                    startActivity(new Intent(getActivity(), LiveAppIndexActivity.class));
                    break;

                case 1:
                    //番剧
                    RegionTypesInfo.DataBean mBangumi = mRegionTypes.get(1);
                    RegionTypeDetailsActivity.launch(getActivity(), mBangumi);
                    break;

                case 2:
                    //动画
                    RegionTypesInfo.DataBean mAnimation = mRegionTypes.get(2);
                    RegionTypeDetailsActivity.launch(getActivity(), mAnimation);
                    break;

                case 3:
                    //音乐
                    RegionTypesInfo.DataBean mMuise = mRegionTypes.get(3);
                    RegionTypeDetailsActivity.launch(getActivity(), mMuise);
                    break;

                case 4:
                    //舞蹈
                    RegionTypesInfo.DataBean mDence = mRegionTypes.get(4);
                    RegionTypeDetailsActivity.launch(getActivity(), mDence);
                    break;

                case 5:
                    //游戏
                    RegionTypesInfo.DataBean mGame = mRegionTypes.get(5);
                    RegionTypeDetailsActivity.launch(getActivity(), mGame);
                    break;

                case 6:
                    //科技
                    RegionTypesInfo.DataBean mScience = mRegionTypes.get(6);
                    RegionTypeDetailsActivity.launch(getActivity(), mScience);
                    break;

                case 7:
                    //生活
                    RegionTypesInfo.DataBean mLife = mRegionTypes.get(7);
                    RegionTypeDetailsActivity.launch(getActivity(), mLife);
                    break;

                case 8:
                    //鬼畜
                    RegionTypesInfo.DataBean mKichiku = mRegionTypes.get(8);
                    RegionTypeDetailsActivity.launch(getActivity(), mKichiku);
                    break;

                case 9:
                    //时尚
                    RegionTypesInfo.DataBean mFashion = mRegionTypes.get(9);
                    RegionTypeDetailsActivity.launch(getActivity(), mFashion);
                    break;

                case 10:
                    //广告
                    startActivity(new Intent(getActivity(), AdvertisingActivity.class));
                    break;

                case 11:
                    //娱乐
                    RegionTypesInfo.DataBean mRecreation = mRegionTypes.get(10);
                    RegionTypeDetailsActivity.launch(getActivity(), mRecreation);
                    break;

                case 12:
                    //电影
                    RegionTypesInfo.DataBean mMovei = mRegionTypes.get(11);
                    RegionTypeDetailsActivity.launch(getActivity(), mMovei);
                    break;

                case 13:
                    //电视剧
                    RegionTypesInfo.DataBean mTv = mRegionTypes.get(12);
                    RegionTypeDetailsActivity.launch(getActivity(), mTv);
                    break;

                case 14:
                    // 游戏中心
                    startActivity(new Intent(getActivity(), GameCenterActivity.class));
                    break;

                default:
                    break;
            }
        });
    }

    @Override
    protected void loadData() {
        Observable.just(readAssetJson()) // 原样发送接收到的数据
                .compose(bindToLifecycle())
                .map(s -> new Gson().fromJson(s, RegionTypesInfo.class))
                .map(RegionTypesInfo::getData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(dataBeans -> {
                    mRegionTypes.addAll(dataBeans);
                    finishTask();
                }, throwable -> {

                });
    }

    private String readAssetJson() {
        AssetManager assetManager = getActivity().getAssets();
        try {
            InputStream in = assetManager.open("region.json");
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null){
                builder.append(line);
            }
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
