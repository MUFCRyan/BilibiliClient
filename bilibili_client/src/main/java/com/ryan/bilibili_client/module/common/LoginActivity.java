package com.ryan.bilibili_client.module.common;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.utils.CommonUtil;
import com.ryan.bilibili_client.utils.ConstantUtil;
import com.ryan.bilibili_client.utils.PreferenceUtil;
import com.ryan.bilibili_client.utils.ToastUtil;

import butterknife.BindView;
import butterknife.OnClick;

public class LoginActivity extends RxBaseActivity {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.iv_icon_left)
    ImageView mIvLeftLogo;

    @BindView(R.id.iv_icon_right)
    ImageView mIvRightLogo;

    @BindView(R.id.delete_username)
    ImageButton mIbDeleteUserName;

    @BindView(R.id.et_username)
    EditText mEtUserName;

    @BindView(R.id.et_password)
    EditText mEtPassword;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        mEtUserName.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus && mEtUserName.getText().length() > 0){
                mIbDeleteUserName.setVisibility(View.VISIBLE);
            } else
                mIbDeleteUserName.setVisibility(View.GONE);
            mIvLeftLogo.setImageResource(R.drawable.ic_22);
            mIvRightLogo.setImageResource(R.drawable.ic_33);
        });

        mEtPassword.setOnFocusChangeListener((v, hasFocus) -> {
            mIvLeftLogo.setImageResource(R.drawable.ic_22_hide);
            mIvRightLogo.setImageResource(R.drawable.ic_33_hide);
        });

        mEtUserName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                mEtPassword.setText("");
                if (s.length() > 0){
                    mIbDeleteUserName.setVisibility(View.VISIBLE);
                } else
                    mIbDeleteUserName.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void initToolBar() {
        mToolbar.setNavigationIcon(R.drawable.ic_cancel);
        mToolbar.setTitle("登录");
        mToolbar.setOnClickListener(v -> finish());
    }

    @OnClick(R.id.btn_login)
    void startLogin(){
        boolean networkAvailable = CommonUtil.isNetworkAvailable(this);
        if (!networkAvailable){
            ToastUtil.ShortToast("当前网络不可用,请检查网络设置");
            return;
        }
        login();
    }

    @OnClick(R.id.delete_username)
    void delete() {
        // 清空用户名以及密码
        mEtUserName.setText("");
        mEtPassword.setText("");
        mIbDeleteUserName.setVisibility(View.GONE);
        mEtUserName.setFocusable(true);
        mEtUserName.setFocusableInTouchMode(true);
        mEtUserName.requestFocus();
    }

    private void login() {
        String name = mEtUserName.getText().toString();
        String password = mEtPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            ToastUtil.ShortToast("用户名不能为空");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            ToastUtil.ShortToast("密码不能为空");
            return;
        }
        PreferenceUtil.putBoolean(ConstantUtil.KEY, true);
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        finish();
    }
}
