package com.ryan.bilibili_client.module.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatDelegate;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.module.entry.AttentionPeopleFragment;
import com.ryan.bilibili_client.module.entry.ConsumeHistoryFragment;
import com.ryan.bilibili_client.module.entry.HistoryFragment;
import com.ryan.bilibili_client.module.entry.IFavoritesFragment;
import com.ryan.bilibili_client.module.entry.OfflineDownloadActivity;
import com.ryan.bilibili_client.module.entry.SettingFragment;
import com.ryan.bilibili_client.module.entry.VIPActivity;
import com.ryan.bilibili_client.module.home.HomePageFragment;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.PreferenceUtil;
import com.ryan.bilibili_client.utils.ToastUtil;
import com.ryan.bilibili_client.utils.ViewUtil;
import com.ryan.bilibili_client.widget.CircleImageView;
import butterknife.BindView;
import static com.ryan.bilibili_client.R.id.item_app;
import static com.ryan.bilibili_client.R.id.item_download;
import static com.ryan.bilibili_client.R.id.item_favourite;
import static com.ryan.bilibili_client.R.id.item_group;
import static com.ryan.bilibili_client.R.id.item_history;
import static com.ryan.bilibili_client.R.id.item_home;
import static com.ryan.bilibili_client.R.id.item_settings;
import static com.ryan.bilibili_client.R.id.item_theme;
import static com.ryan.bilibili_client.R.id.item_tracker;
import static com.ryan.bilibili_client.R.id.item_vip;

public class MainActivity extends RxBaseActivity implements NavigationView.OnNavigationItemSelectedListener{
    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.navigation_view)
    NavigationView mNavigationView;

    private Fragment[] mFragments;
    private int mCurrentTabIndex;
    private int mIndex;
    private long mExitTime;
    private HomePageFragment mHomePageFragment;

    @Override
    public int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        initFragments();
        initNavigationView();
    }

    @Override
    public void initToolBar() {

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        mDrawerLayout.closeDrawer(GravityCompat.START);
        switch(item.getItemId()){
            case item_home:
                changeFragmentIndex(item, 0);
                return true;
            case item_download:
                startActivity(new Intent(MainActivity.this, OfflineDownloadActivity.class));
                return true;
            case item_vip:
                startActivity(new Intent(MainActivity.this, VIPActivity.class));
                return true;
            case item_favourite:
                changeFragmentIndex(item, 1);
                return true;
            case item_history:
                changeFragmentIndex(item, 2);
                return true;
            case item_group:
                changeFragmentIndex(item, 3);
                return true;
            case item_tracker:
                changeFragmentIndex(item, 4);
                return true;
            case item_theme: // 主题选择
                return true;
            case item_app: // 应用推荐
                return true;
            case item_settings:
                changeFragmentIndex(item, 5);
                return true;
        }
        return false;
    }

    public void toggleDrawer(){
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START))
            mDrawerLayout.closeDrawer(GravityCompat.START);
        else
            mDrawerLayout.openDrawer(GravityCompat.START);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            if (mDrawerLayout.isDrawerOpen(mDrawerLayout.getChildAt(1)))
                mDrawerLayout.closeDrawers();
            else {
                if (mHomePageFragment != null){
                    if (mHomePageFragment.isOpenSearchView())
                        mHomePageFragment.closeSearchView();
                    else
                        exitApp();
                } else
                    exitApp();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        //super.onSaveInstanceState(outState, outPersistentState); // 解决重启 APP Fragment 重叠的问题
    }

    private void initFragments() {
        mHomePageFragment = HomePageFragment.newInstance();
        IFavoritesFragment iFavoritesFragment = IFavoritesFragment.newInstance();
        HistoryFragment historyFragment = HistoryFragment.newInstance();
        AttentionPeopleFragment attentionPeopleFragment = AttentionPeopleFragment.newInstance();
        ConsumeHistoryFragment consumeHistoryFragment = ConsumeHistoryFragment.newInstance();
        SettingFragment settingFragment = SettingFragment.newInstance();
        mFragments = new Fragment[]{mHomePageFragment, iFavoritesFragment, historyFragment, attentionPeopleFragment, consumeHistoryFragment, settingFragment};

        // 添加并显示第一个 Fragment
        getSupportFragmentManager().beginTransaction()
                .add(R.id.container, mHomePageFragment)
                .show(mHomePageFragment)
                .commit();
    }

    private void initNavigationView() {
        mNavigationView.setNavigationItemSelectedListener(this);
        View headerView = mNavigationView.getHeaderView(0);
        CircleImageView userAvatar = ViewUtil.find(headerView, R.id.user_avatar_view);
        TextView userName = ViewUtil.find(headerView, R.id.user_name);
        TextView userSign = ViewUtil.find(headerView, R.id.user_other_info);
        ImageView switchMode = ViewUtil.find(headerView, R.id.iv_head_switch_mode);
        userAvatar.setImageResource(R.drawable.ic_ryan_avatar);
        userName.setText(R.string.MUFCRyan);
        userSign.setText(R.string.about_user_head_layout);
        switchMode.setOnClickListener(v -> switchNightMode());

        boolean flag = PreferenceUtil.getBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        if (flag)
            switchMode.setImageResource(R.drawable.ic_switch_daily);
        else
            switchMode.setImageResource(R.drawable.ic_switch_night);
    }

    private void switchNightMode() {
        boolean flag = PreferenceUtil.getBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        if (flag){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            PreferenceUtil.putBoolean(ConstantUtil.SWITCH_MODE_KEY, false);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            PreferenceUtil.putBoolean(ConstantUtil.SWITCH_MODE_KEY, true);
        }
        recreate();
    }

    private void changeFragmentIndex(MenuItem item, int currIndex) {
        mIndex = currIndex;
        switchFragment();
        item.setChecked(true);
    }

    private void switchFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(mFragments[mCurrentTabIndex]);
        if (mFragments[mIndex].isAdded()){
            transaction.add(R.id.container, mFragments[mIndex]);
        }
        transaction.show(mFragments[mIndex]);
        mCurrentTabIndex = mIndex;
    }

    private void exitApp() {
        if (System.currentTimeMillis() - mExitTime > 2000) {
            ToastUtil.shortToast("再按一次退出");
            mExitTime = System.currentTimeMillis();
        } else {
            PreferenceUtil.remove(ConstantUtil.SWITCH_MODE_KEY);
            finish();
        }

    }
}
