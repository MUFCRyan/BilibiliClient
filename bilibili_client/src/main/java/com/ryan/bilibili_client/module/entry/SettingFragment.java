package com.ryan.bilibili_client.module.entry;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.module.common.AppIntroduceActivity;
import com.ryan.bilibili_client.module.common.LoginActivity;
import com.ryan.bilibili_client.module.common.MainActivity;
import com.ryan.bilibili_client.module.common.RyanInfoActivity;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.PreferenceUtil;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * Created by MUFCRyan on 2017/5/26.
 * 设置与帮助
 */

public class SettingFragment extends RxLazyFragment {
    @BindView(R.id.app_version_code)
    TextView mVersionCode;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    public static SettingFragment newInstance(){
        return new SettingFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_setting;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        mToolbar.setTitle("设置与帮助");
        mToolbar.setNavigationIcon(R.drawable.ic_navigation_drawer);
        mToolbar.setNavigationOnClickListener(v -> {

            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).toggleDrawer();
            }
        });

        mVersionCode.setText("v" + getVersionCode());
    }

    @OnClick(R.id.layout_about_me)
    void startAboutMeActivity(){
        startActivity(new Intent(getActivity(), RyanInfoActivity.class));
    }

    @OnClick(R.id.layout_about_app)
    void startAboutAppActivity(){
        startActivity(new Intent(getActivity(), AppIntroduceActivity.class));
    }

    @OnClick(R.id.btn_logout)
    void logout(){
        PreferenceUtil.putBoolean(ConstantUtil.KEY, false);
        startActivity(new Intent(getActivity(), LoginActivity.class));
        getActivity().finish();
    }

    private String getVersionCode() {
        PackageInfo packageInfo = null;
        try {
            packageInfo = getActivity().getPackageManager()
                    .getPackageInfo(getActivity().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        assert packageInfo != null;
        return packageInfo.versionName;
    }

}
