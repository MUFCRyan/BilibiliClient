package com.ryan.bilibili_client.widget.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.utils.ThemeHelper;
import com.ryan.bilibili_client.utils.ViewUtil;

/**
 * Created by MUFCRyan on 2017/5/26.
 *
 */

public class CardPickerDialog extends DialogFragment implements View.OnClickListener{
    ImageView[] mCards = new ImageView[8];
    Button mConfirm, mCancel;
    private int mCurrentTheme;
    private ClickListener mClickListener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_AppCompat_Dialog_Alert);
        mCurrentTheme = ThemeHelper.getTheme(getActivity());
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_theme_picker, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mConfirm = ViewUtil.find(view, android.R.id.button1);
        mCancel = ViewUtil.find(view, android.R.id.button2);
        mCards[0] = ViewUtil.find(view, R.id.theme_pink);
        mCards[1] = ViewUtil.find(view, R.id.theme_purple);
        mCards[2] = ViewUtil.find(view, R.id.theme_blue);
        mCards[3] = ViewUtil.find(view, R.id.theme_green);
        mCards[4] = ViewUtil.find(view, R.id.theme_green_light);
        mCards[5] = ViewUtil.find(view, R.id.theme_yellow);
        mCards[6] = ViewUtil.find(view, R.id.theme_orange);
        mCards[7] = ViewUtil.find(view, R.id.theme_red);
        setImageButtons(mCurrentTheme);
        mConfirm.setOnClickListener(this);
        mCancel.setOnClickListener(this);
        for (ImageView card : mCards) {
            card.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case android.R.id.button1:
                if (mClickListener != null)
                    mClickListener.onConfirm(mCurrentTheme);
                break;
            case android.R.id.button2:
                dismiss();
                break;
            case R.id.theme_pink:
                mCurrentTheme = ThemeHelper.CARD_SAKURA;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_purple:
                mCurrentTheme = ThemeHelper.CARD_HOPE;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_blue:
                mCurrentTheme = ThemeHelper.CARD_STORM;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_green:
                mCurrentTheme = ThemeHelper.CARD_WOOD;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_green_light:
                mCurrentTheme = ThemeHelper.CARD_LIGHT;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_yellow:
                mCurrentTheme = ThemeHelper.CARD_THUNDER;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_orange:
                mCurrentTheme = ThemeHelper.CARD_SAND;
                setImageButtons(mCurrentTheme);
                break;
            case R.id.theme_red:
                mCurrentTheme = ThemeHelper.CARD_FIREY;
                setImageButtons(mCurrentTheme);
                break;
            default:
                break;
        }
    }

    public void setImageButtons(int theme) {
        mCards[0].setSelected(theme == ThemeHelper.CARD_SAKURA);
        mCards[1].setSelected(theme == ThemeHelper.CARD_HOPE);
        mCards[2].setSelected(theme == ThemeHelper.CARD_STORM);
        mCards[3].setSelected(theme == ThemeHelper.CARD_WOOD);
        mCards[4].setSelected(theme == ThemeHelper.CARD_LIGHT);
        mCards[5].setSelected(theme == ThemeHelper.CARD_THUNDER);
        mCards[6].setSelected(theme == ThemeHelper.CARD_SAND);
        mCards[7].setSelected(theme == ThemeHelper.CARD_FIREY);
    }

    public void setClickListener(ClickListener listener){
        mClickListener = listener;
    }

    public interface ClickListener{
        void onConfirm(int currentTheme);
    }
}
