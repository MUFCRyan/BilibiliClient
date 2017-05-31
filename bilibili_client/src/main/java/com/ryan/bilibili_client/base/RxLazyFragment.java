package com.ryan.bilibili_client.base;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.trello.rxlifecycle.components.support.RxFragment;

import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by MUFCRyan on 2017/5/26.
 *
 */

public abstract class RxLazyFragment extends RxFragment {
    private View mParentView;
    private FragmentActivity mActivity;
    protected boolean mPrepared;
    protected boolean mVisible;
    private Unbinder mBind;
    @LayoutRes
    public abstract int getLayoutResId();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mParentView = inflater.inflate(getLayoutResId(), container, false);
        mActivity = getSupportActivity();
        return mParentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mBind = ButterKnife.bind(this, view);
        finishCreatedView(savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = (FragmentActivity) activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }

    public abstract void finishCreatedView(Bundle saveInstanceState);

    public FragmentActivity getSupportActivity() {
        return super.getActivity();
    }

    public ActionBar getSuupportActionBar(){
        return getSupportActivity().getActionBar();
    }

    public Context getApplicationContext(){
        return mActivity == null ? (getActivity() == null ? null : mActivity.getApplicationContext()) : mActivity.getApplicationContext();
    }

    /** Fragment 数据的懒加载 */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()){
            mVisible = true;
            onVisible();
        } else {
            mVisible = false;
            onInvisible();
        }
    }

    protected void onVisible(){
        lazyLoad();
    }
    protected void onInvisible(){}
    protected void lazyLoad(){}
    protected void loadData() {}
    protected void showProgressBar() {}
    protected void hideProgressBar() {}
    protected void initRecyclerView() {}
    protected void initRefreshLayout() {}
    protected void finishTask() {}

    @SuppressWarnings("unchecked")
    public <T extends View> T $(int resId){
        return (T) mParentView.findViewById(resId);
    }
}
