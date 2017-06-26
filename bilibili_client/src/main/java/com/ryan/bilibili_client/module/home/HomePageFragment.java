package com.ryan.bilibili_client.module.home;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.flyco.tablayout.SlidingTabLayout;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.adapter.pager.HomePageAdapter;
import com.ryan.bilibili_client.base.RxLazyFragment;
import com.ryan.bilibili_client.module.common.MainActivity;
import com.ryan.bilibili_client.module.entry.GameCenterActivity;
import com.ryan.bilibili_client.module.entry.OfflineDownloadActivity;
import com.ryan.bilibili_client.module.search.TotalStationSearchActivity;
import com.ryan.bilibili_client.widget.CircleImageView;
import java.util.ArrayList;
import butterknife.BindView;
import butterknife.OnClick;
import static android.app.Activity.RESULT_OK;
import static com.ryan.bilibili_client.R.id.id_action_download;
import static com.ryan.bilibili_client.R.id.id_action_game;

/**
 * Created by MUFCRyan on 2017/5/26.
 * 首页模块主界面
 */

public class HomePageFragment extends RxLazyFragment {
    @BindView(R.id.view_pager)
    ViewPager mViewPager;

    @BindView(R.id.sliding_tabs)
    SlidingTabLayout mSlidingTabLayout;

    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.search_view)
    MaterialSearchView mSearchView;

    @BindView(R.id.toolbar_user_avatar)
    CircleImageView mUserAvatar;

    public static HomePageFragment newInstance(){
        return new HomePageFragment();
    }

    @Override
    public int getLayoutResId() {
        return R.layout.fragment_home_page;
    }

    @Override
    public void finishCreatedView(Bundle saveInstanceState) {
        setHasOptionsMenu(true);
        initToolBar();
        initSearchView();
        initViewPager();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_main, menu);
        // 设置 SearchViewItemMenu
        MenuItem item = menu.findItem(R.id.id_action_search);
        mSearchView.setMenuItem(item);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId){
            case id_action_game:
                // 游戏中心
                startActivity(new Intent(getActivity(), GameCenterActivity.class));
                break;
            case id_action_download:
                startActivity(new Intent(getActivity(), OfflineDownloadActivity.class));
                break;
            default :
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.navigation_layout)
    void toggleDrawer(){
        Activity activity = getActivity();
        if (activity instanceof MainActivity)
            ((MainActivity)activity).toggleDrawer();
    }

    public boolean isOpenSearchView() {

        return mSearchView.isSearchOpen();
    }

    public void closeSearchView() {

        mSearchView.closeSearch();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0){
                String searchWord = matches.get(0);
                if (!TextUtils.isEmpty(searchWord)){
                    mSearchView.setQuery(searchWord, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void initToolBar() {
        mToolbar.setTitle("");
        ((MainActivity)getActivity()).setSupportActionBar(mToolbar);
        mUserAvatar.setImageResource(R.drawable.ic_ryan_avatar);
    }

    private void initSearchView() {
        mSearchView.setVoiceSearch(false);
        mSearchView.setCursorDrawable(R.drawable.custom_cursor);
        mSearchView.setEllipsize(false);
        mSearchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                TotalStationSearchActivity.launch(getActivity(), query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    private void initViewPager() {
        HomePageAdapter pageAdapter = new HomePageAdapter(getChildFragmentManager(), getApplicationContext());
        mViewPager.setOffscreenPageLimit(5);
        mViewPager.setAdapter(pageAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
        mViewPager.setCurrentItem(1);
    }
}
