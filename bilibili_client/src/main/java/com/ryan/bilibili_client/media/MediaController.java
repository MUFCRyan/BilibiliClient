package com.ryan.bilibili_client.media;

import android.content.Context;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ryan.bilibili_client.media.callback.DanmakuSwitchListener;
import com.ryan.bilibili_client.media.callback.MediaPlayerListener;
import com.ryan.bilibili_client.media.callback.VideoBackListener;

/**
 * Created by MUFCRyan on 2017/7/7.
 * 播放器控制器
 */

public class MediaController extends FrameLayout {
    private static final int sDefaultTimeout = 3000;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private MediaPlayerListener mPlayerListener;
    private Context mContext;
    private PopupWindow mWindow;
    private int mAnimStyle;
    private View mAnchor;
    private View mRoot;
    private ProgressBar mProgressBar;
    private TextView mEndTime, mCurrentTime;
    private TextView mTitleView;
    private OutlineTextView mInfoView;
    private String mTitle;
    private long mDuration;
    private boolean mShowing, mDragging, mInstantSeeking = true, mFromXml = false;
    private ImageButton mIbPause;
    private AudioManager mAudioManager;
    private OnShownListener mShowListener;
    private OnHiddenListener mHiddenListener;
    private boolean mDanmakuShow = false;
    private DanmakuSwitchListener mDanmakuSwitchListener;
    private ImageView mIvBack;
    private VideoBackListener mVideoBackListener;
    private ImageView mTvPlay;

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            long position;
            switch(msg.what){
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    position = setProgress();
                    break;
                default :
                    break;
            }
        }
    };

    private long setProgress() {
        return 0;
    }

    private void hide() {

    }

    public MediaController(@NonNull Context context) {
        super(context);
        if (!mFromXml && initController(context))
            initFloatingWindow();
    }

    public MediaController(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mRoot = this;
        mFromXml = true;
        initController(context);
    }

    private boolean initController(Context context) {
        mContext = context;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        return true;
    }

    private void initFloatingWindow() {
        mWindow = new PopupWindow(mContext);
        mWindow.setFocusable(false);
        mWindow.setBackgroundDrawable(null);
        mWindow.setOutsideTouchable(true);
        mAnimStyle = android.R.style.Animation;
    }

    private void setDanmakuSwitchListener(DanmakuSwitchListener listener){
        mDanmakuSwitchListener = listener;
    }

    private void setVideoBackListener(VideoBackListener listener){
        mVideoBackListener = listener;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    public void show(int timeout) {

    }

    public interface OnShownListener {
        void onShown();
    }

    public interface OnHiddenListener {
        void onHidden();
    }
}
