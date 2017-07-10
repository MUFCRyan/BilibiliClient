package com.ryan.bilibili_client.media;

import android.content.Context;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.media.callback.DanmakuSwitchListener;
import com.ryan.bilibili_client.media.callback.MediaPlayerListener;
import com.ryan.bilibili_client.media.callback.VideoBackListener;
import com.ryan.bilibili_client.utils.LogUtil;

import java.util.Locale;

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
                    if (!mDragging && mShowing){
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (position % 1000));
                        updatePausePlay();
                    }
                    break;
                default :
                    break;
            }
        }
    };

    private OnClickListener mPauseListener = v -> {
        doPauseResume();
        show(sDefaultTimeout);
    };

    private Runnable mLastRunnable;

    private SeekBar.OnSeekBarChangeListener mSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (!fromUser)
                return;
            long newPosition = mDuration * progress / 1000;
            String time = generateTime(newPosition);
            if (mInstantSeeking){
                mHandler.removeCallbacks(mLastRunnable);
                mLastRunnable = () -> mPlayerListener.seekTo(newPosition);
                mHandler.postDelayed(mLastRunnable, 200);
            }
            if (mInfoView != null)
                mInfoView.setText(time);
            if (mCurrentTime != null)
                mCurrentTime.setText(time);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            mDragging = true;
            show(3600000);
            mHandler.removeMessages(SHOW_PROGRESS);
            if (mInstantSeeking)
                mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, true);
            if (mInfoView != null){
                mInfoView.setText("");
                mInfoView.setVisibility(VISIBLE);
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            if (!mInstantSeeking)
                mPlayerListener.seekTo(mDuration * seekBar.getProgress() / 1000);
            if (mInfoView != null){
                mInfoView.setText("");
                mInfoView.setVisibility(GONE);
            }
            show(sDefaultTimeout);
            mHandler.removeMessages(SHOW_PROGRESS);
            mAudioManager.setStreamMute(AudioManager.STREAM_MUSIC, false);
            mDragging = false;
            mHandler.sendEmptyMessageDelayed(SHOW_PROGRESS, 1000);
        }
    };

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

    public void hide() {
        if (mAnchor == null)
            return;
        if (mShowing){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH){
                mAnchor.setSystemUiVisibility(SYSTEM_UI_FLAG_HIDE_NAVIGATION);
            }
            try {
                mHandler.removeMessages(SHOW_PROGRESS);
                if (mFromXml){
                    setVisibility(GONE);
                } else
                    mWindow.dismiss();
            } catch (IllegalArgumentException e){
                LogUtil.all("MediaController already removed");
            }
            mShowing = false;
            if (mHiddenListener != null){
                mHiddenListener.onHidden();
            }
        }
    }

    private long setProgress() {
        if (mPlayerListener == null || mDragging)
            return 0;
        int position = mPlayerListener.getCurrentPosition();
        int duration = mPlayerListener.getDuration();
        if (mProgressBar != null){
            if (duration > 0){
                long pos = 1000L * position / duration;
                mProgressBar.setProgress((int) pos);
            }
            int percent = mPlayerListener.getBufferPercentage();
            mProgressBar.setSecondaryProgress(percent * 10);
        }
        mDuration = duration;
        if (mEndTime != null)
            mEndTime.setText(generateTime(mDuration));
        if (mCurrentTime != null)
            mCurrentTime.setText(generateTime(position));
        return position;
    }

    private String generateTime(long position) {
        int totalSeconds = (int) ((position / 1000.0) + 0.5);
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;
        if (hours > 0)
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        else
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
    }

    private void updatePausePlay() {
        if (mRoot == null || mIbPause == null || mTvPlay == null)
            return;
        if (mPlayerListener.isPlaying()){
            mIbPause.setImageResource(R.drawable.bili_player_play_can_pause);
            mTvPlay.setImageResource(R.drawable.ic_tv_stop);
        } else {
            mIbPause.setImageResource(R.drawable.bili_player_play_can_play);
            mTvPlay.setImageResource(R.drawable.ic_tv_play);
        }
    }

    private void doPauseResume() {
        if (mPlayerListener.isPlaying())
            mPlayerListener.pause();
        else
            mPlayerListener.start();
        updatePausePlay();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (mRoot != null)
            initControllerView(mRoot);
    }

    private void initControllerView(View view) {
        mIbPause = (ImageButton) view.findViewById(R.id.media_controller_play_pause);
        mTvPlay = (ImageView) view.findViewById(R.id.media_controller_tv_play);
        if (mIbPause != null && mTvPlay != null){
            mIbPause.requestFocus();
            mTvPlay.setOnClickListener(v -> {
                doPauseResume();
                show(sDefaultTimeout);
            });
        }

        mProgressBar = (SeekBar) view.findViewById(R.id.media_controller_seekbar);
        if (mProgressBar != null){
            if (mProgressBar instanceof SeekBar){
                SeekBar seekBar = (SeekBar) mProgressBar;
                seekBar.setOnSeekBarChangeListener(mSeekBarChangeListener);
                seekBar.setThumbOffset(1);
            }
            mProgressBar.setMax(1000);
        }

        mEndTime = (TextView) view.findViewById(R.id.media_controller_time_total);
        mCurrentTime = (TextView) view.findViewById(R.id.media_controller_time_current);
        mTitleView = (TextView) view.findViewById(R.id.media_controller_title);
        if (mTitleView != null)
            mTitleView.setText(mTitle);

        LinearLayout llDanmuku = (LinearLayout) view.findViewById(R.id.media_controller_danmaku_layout);
        ImageView ivDanmuku = (ImageView) view.findViewById(R.id.media_controller_danmaku_switch);
        TextView tvDanmuku = (TextView) view.findViewById(R.id.media_controller_danmaku_text);
        llDanmuku.setOnClickListener(v -> {
            if (mDanmakuShow){
                ivDanmuku.setImageResource(R.drawable.bili_player_danmaku_is_open);
                tvDanmuku.setText("弹幕开");
                mDanmakuSwitchListener.setDanmakuShow(true);
                mDanmakuShow = false;
            } else {
                ivDanmuku.setImageResource(R.drawable.bili_player_danmaku_is_closed);
                tvDanmuku.setText("弹幕关");
                mDanmakuSwitchListener.setDanmakuShow(false);
                mDanmakuShow = true;
            }
        });

        mIvBack = (ImageView) view.findViewById(R.id.media_controller_back);
        mIvBack.setOnClickListener(v -> mVideoBackListener.back());
    }

    public void setAnchor(View anchor) {
        mAnchor = anchor;
        if (!mFromXml){
            removeAllViews();
            mRoot = makeControllerView();
            mWindow.setContentView(mRoot);
            mWindow.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
            mWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        }
        initControllerView(mRoot);
    }

    private View makeControllerView() {
        return ((LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.layout_media_controller, this);
    }

    public void setMediaPlayer(MediaPlayerListener listener){
        mPlayerListener = listener;
        updatePausePlay();
    }

    public void setInstantSeeking(boolean seekWhenDragging){
        mInstantSeeking = seekWhenDragging;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public void setInfoView(OutlineTextView infoView) {
        mInfoView = infoView;
    }

    public void setAnimStyle(int animStyle) {
        mAnimStyle = animStyle;
    }

    public void setDanmakuSwitchListener(DanmakuSwitchListener listener){
        mDanmakuSwitchListener = listener;
    }

    public void setVideoBackListener(VideoBackListener listener){
        mVideoBackListener = listener;
    }

    public void show() {
        show(sDefaultTimeout);
    }

    public void show(int timeout) {
        if (!mShowing && mAnchor != null && mAnchor.getWindowToken() != null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH)
                mAnchor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            if (mIbPause != null && mTvPlay != null){
                mIbPause.requestFocus();
                mTvPlay.requestFocus();
            }
            disableUnsupportedButtons();

            if (mFromXml)
                setVisibility(VISIBLE);
            else {
                int[] location = new int[2];
                mAnchor.getLocationOnScreen(location);
                Rect anchorRect = new Rect(location[0], location[1], location[0] + mAnchor.getWidth(), location[1] + mAnchor.getHeight());
                mWindow.setAnimationStyle(mAnimStyle);
                mWindow.showAtLocation(mAnchor, Gravity.BOTTOM, anchorRect.left, 0);
            }
            mShowing = true;
            if (mShowListener != null)
                mShowListener.onShown();
        }

        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);

        if (timeout != 0){
            mHandler.removeMessages(FADE_OUT);
            mHandler.sendMessageDelayed(mHandler.obtainMessage(FADE_OUT), timeout);
        }
    }

    private void disableUnsupportedButtons() {
        if (mIbPause != null && mTvPlay != null && !mPlayerListener.canPause()){
            mIbPause.setEnabled(false);
            mTvPlay.setEnabled(false);
        }
    }

    public boolean isShowing(){
        return mShowing;
    }

    public void setOnShownListener(OnShownListener listener){
        mShowListener = listener;
    }

    public void setOnHiddenListener(OnHiddenListener listener){
        mHiddenListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        show(sDefaultTimeout);
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int keyCode = event.getKeyCode();
        if (event.getRepeatCount() == 0 && (keyCode == KeyEvent.KEYCODE_HEADSETHOOK || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE || keyCode == KeyEvent.KEYCODE_SPACE)){
            doPauseResume();
            show(sDefaultTimeout);
            if (mIbPause != null && mTvPlay != null){
                mIbPause.requestFocus();
                mTvPlay.requestFocus();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP){
            if (mPlayerListener.isPlaying()){
                mPlayerListener.pause();
                updatePausePlay();
            }
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK || keyCode == KeyEvent.KEYCODE_MENU){
            hide();
            return true;
        } else
            show(sDefaultTimeout);
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mIbPause != null)
            mIbPause.setEnabled(enabled);
        if (mTvPlay != null)
            mTvPlay.setEnabled(enabled);
        if (mProgressBar != null)
            mProgressBar.setEnabled(enabled);
        disableUnsupportedButtons();
        super.setEnabled(enabled);
    }

    public interface OnShownListener {
        void onShown();
    }

    public interface OnHiddenListener {
        void onHidden();
    }
}
