package com.ryan.bilibili_client.media;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Pair;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.media.callback.MediaPlayerListener;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;
import tv.danmaku.ijk.media.player.pragma.DebugLog;

import static tv.danmaku.ijk.media.player.IMediaPlayer.OnBufferingUpdateListener;
import static tv.danmaku.ijk.media.player.IMediaPlayer.OnInfoListener;
import static tv.danmaku.ijk.media.player.IMediaPlayer.OnSeekCompleteListener;
import static tv.danmaku.ijk.media.player.IMediaPlayer.OnVideoSizeChangedListener;

/**
 * Created by MUFCRyan on 2017/7/6.
 * 自定义 VideoView
 */

public class VideoPlayerView extends SurfaceView implements MediaPlayerListener {
    private static final String TAG = VideoPlayerView.class.getSimpleName();
    public static final int VIDEO_LAYOUT_ORIGIN = 0;
    public static final int VIDEO_LAYOUT_SCALE = 1;
    public static final int VIDEO_LAYOUT_STRETCH = 2;
    public static final int VIDEO_LAYOUT_ZOOM = 3;

    public static final int STATE_ERROR = -1;
    public static final int STATE_IDLE = 0;
    public static final int STATE_PREPARING = 1;
    public static final int STATE_PREPARED = 2;
    public static final int STATE_PLAYING = 3;
    public static final int STATE_PAUSED = 4;
    public static final int STATE_PLAYBACK_COMPLETED = 5;
    public static final int STATE_SUSPEND = 6;
    public static final int STATE_RESUME = 7;
    public static final int STATE_SUSPEND_UNSUPPORTED = 8;

    private Uri mUri;
    private long mDuration;
    private String mUserAgent;
    private int mCurrentState = STATE_IDLE;
    private int mTargetState = STATE_IDLE;
    private int mVideoLayout = VIDEO_LAYOUT_SCALE;
    private SurfaceHolder mSurfaceHolder;
    private IMediaPlayer mMediaPlayer;
    private int mVideoWidth, mVideoHeight;
    private int mVideoSarNum, mVideoSarDen;
    private int mSurfaceWidth, mSurfaceHeight;
    private MediaController mMediaController;
    private View mMediaBufferingIndicator;

    private OnCompletionListener mOnCompletionListener;
    private OnPreparedListener mOnPreparedListener;
    private OnErrorListener mOnErrorListener;
    private OnSeekCompleteListener mOnSeekCompleteListener;
    private OnInfoListener mOnInfoListener;
    private OnBufferingUpdateListener mOnBufferingUpdateListener;
    private OnControlEventsListener mOnControlEventsListener;

    private int mCurrentBufferPercentage;
    private long mSeekWhenPrepared;
    private boolean mCanPause = true;
    private boolean mCanSeekBack = true;
    private boolean mCanSeekForward = true;

    private Context mContext;

    OnVideoSizeChangedListener mSizeChangedListener = new OnVideoSizeChangedListener() {
        @Override
        public void onVideoSizeChanged(IMediaPlayer player, int width, int height, int sarNum, int sarDen) {
            DebugLog.dfmt(TAG, "onVideoSizeChanged: (%dx%d)", width, height);
            mVideoWidth = player.getVideoWidth();
            mVideoHeight = player.getVideoHeight();
            mVideoSarNum = sarNum;
            mVideoSarDen = sarDen;
            if (mVideoWidth != 0 && mVideoHeight != 0){
                setVideoLayout(mVideoLayout);
            }
        }
    };

    OnPreparedListener mPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer player) {
            DebugLog.d(TAG, "OnPrepare");
            mCurrentState = STATE_PREPARED;
            mTargetState = STATE_PLAYING;
            if (mOnPreparedListener != null){
                mOnPreparedListener.onPrepared(player);
            }
            if (mMediaController != null){
                mMediaController.setEnabled(true);
            }
            mVideoWidth = player.getVideoWidth();
            mVideoHeight = player.getVideoHeight();
            long seekToPosition = mSeekWhenPrepared;
            if (seekToPosition != 0){
                seekTo(seekToPosition);
            }
            if (mVideoWidth != 0 && mVideoHeight != 0){
                setVideoLayout(mVideoLayout);
                if (mSurfaceWidth == mVideoWidth && mSurfaceHeight == mVideoHeight){
                    if (mTargetState == STATE_PLAYING){
                        start();
                        if (mMediaController != null)
                            mMediaController.show();
                    } else if (!isPlaying() && (seekToPosition != 0 || getCurrentPosition() > 0)){
                        if (mMediaController != null)
                            mMediaController.show(0);
                    }
                }
            } else if (mTargetState == STATE_PLAYING){
                start();
            }
        }
    };

    private OnCompletionListener mCompletionListener = new OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer iMediaPlayer) {
            DebugLog.d(TAG, "onCompletion");
            mCurrentState = STATE_PLAYBACK_COMPLETED;
            mTargetState = STATE_PLAYBACK_COMPLETED;
            if (mMediaController != null)
                mMediaController.hide();
            if (mOnCompletionListener != null)
                mOnCompletionListener.onCompletion(mMediaPlayer);
        }
    };

    private OnErrorListener mErrorListener = new OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer player, int frameworkErr, int implErr) {
            DebugLog.dfmt(TAG, "Error: %d, %d", frameworkErr, implErr);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            if (mMediaController != null)
                mMediaController.hide();
            if (mOnErrorListener != null)
                if (mOnErrorListener.onError(mMediaPlayer, frameworkErr, implErr))
                    return true;
            if (getWindowToken() != null){
                int message = frameworkErr == IMediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK
                        ? R.string.video_error_text_invalid_progressive_playback : R.string.video_error_text_unknown;
                new AlertDialog.Builder(mContext)
                        .setTitle(R.string.video_error_title)
                        .setMessage(message)
                        .setPositiveButton(R.string.video_error_button, (error, whichButton) -> {
                            if (mOnCompletionListener != null)
                                mOnCompletionListener.onCompletion(mMediaPlayer);
                        })
                        .setCancelable(false)
                        .show();
            }
            return true;
        }
    };

    private OnBufferingUpdateListener mBufferingUpdateListener = new OnBufferingUpdateListener() {
        @Override
        public void onBufferingUpdate(IMediaPlayer player, int percent) {
            mCurrentBufferPercentage = percent;
            if (mOnBufferingUpdateListener != null)
                mOnBufferingUpdateListener.onBufferingUpdate(player, percent);
        }
    };

    private OnInfoListener mInfoListener = new OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer player, int what, int extra) {
            DebugLog.dfmt(TAG, "onInfo: (%d, %d)", what, extra);
            if (mOnInfoListener != null)
                mOnInfoListener.onInfo(player, what, extra);
            else if (mMediaPlayer != null){
                if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START){
                    DebugLog.dfmt(TAG, "onInfo: (MEDIA_INFO_BUFFERING_START)");
                    if (mMediaBufferingIndicator != null)
                        mMediaBufferingIndicator.setVisibility(VISIBLE);
                } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END){
                    DebugLog.dfmt(TAG, "onInfo: (MEDIA_INFO_BUFFERING_END)");
                    if (mMediaBufferingIndicator != null)
                        mMediaBufferingIndicator.setVisibility(GONE);
                }
            }
            return true;
        }
    };

    private OnSeekCompleteListener mSeekCompleteListener = new OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer player) {
            DebugLog.d(TAG, "onSeekComplete");
            if (mOnSeekCompleteListener != null)
                mOnSeekCompleteListener.onSeekComplete(player);
        }
    };

    SurfaceHolder.Callback mSHCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null && mCurrentState == STATE_SUSPEND && mTargetState == STATE_RESUME){
                mMediaPlayer.setDisplay(mSurfaceHolder);
                resume();
            } else
                openVideo();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            mSurfaceHolder = holder;
            if (mMediaPlayer != null)
                mMediaPlayer.setDisplay(mSurfaceHolder);
            mSurfaceWidth = width;
            mSurfaceHeight = height;
            boolean isValidState = mTargetState == STATE_PLAYING;
            boolean hasValidSize = mVideoWidth == width && mVideoHeight == height;
            if (mMediaPlayer != null && isValidState && hasValidSize){
                if (mSeekWhenPrepared != 0)
                    seekTo(mSeekWhenPrepared);
                start();
                if (mMediaController != null){
                    if (mMediaController.isShowing())
                        mMediaController.hide();
                    mMediaController.show();
                }
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            if (mMediaController != null)
                mMediaController.hide();
            if (mCurrentState != STATE_SUSPEND)
                release(true);
        }
    };

    private void resume() {
        if (mSurfaceHolder == null && mCurrentState == STATE_SUSPEND)
            mTargetState = STATE_RESUME;
        else if (mCurrentState == STATE_SUSPEND_UNSUPPORTED)
            openVideo();
    }

    private void openVideo() {
        if (mUri == null || mSurfaceHolder == null)
            return;
        Intent intent = new Intent("com.android.music.musicservicecommand");
        intent.putExtra("command", "pause");
        mContext.sendBroadcast(intent);
        release(false);

        try {
            mDuration = -1;
            mCurrentBufferPercentage = 0;
            IjkMediaPlayer mediaPlayer = null;
            if (mUri != null){
                mediaPlayer = new IjkMediaPlayer();
                mediaPlayer.setLogEnabled(false);
                mediaPlayer.setOption(IjkMediaPlayer.OPT_CATEGORY_CODEC, "skip_loop_filter", "48");
            }
            mMediaPlayer = mediaPlayer;
            assert mMediaPlayer != null;
            mMediaPlayer.setOnPreparedListener(mPreparedListener);
            mMediaPlayer.setOnVideoSizeChangedListener(mSizeChangedListener);
            mMediaPlayer.setOnCompletionListener(mCompletionListener);
            mMediaPlayer.setOnErrorListener(mErrorListener);
            mMediaPlayer.setOnBufferingUpdateListener(mBufferingUpdateListener);
            mMediaPlayer.setOnInfoListener(mInfoListener);
            mMediaPlayer.setOnSeekCompleteListener(mSeekCompleteListener);
            mMediaPlayer.setDataSource(mUri.toString());
            mMediaPlayer.setDisplay(mSurfaceHolder);
            mMediaPlayer.setScreenOnWhilePlaying(true);
            mMediaPlayer.prepareAsync();
            mCurrentState = STATE_PREPARING;
            attachMediaController();
        } catch (IOException e){
            DebugLog.e(TAG, "Unable to open content: " + mUri, e);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        } catch (IllegalArgumentException e){
            DebugLog.e(TAG, "Unable to open content: " + mUri, e);
            mCurrentState = STATE_ERROR;
            mTargetState = STATE_ERROR;
            mErrorListener.onError(mMediaPlayer, IMediaPlayer.MEDIA_ERROR_UNKNOWN, 0);
            return;
        }
    }

    private void attachMediaController() {
        if (mMediaPlayer != null && mMediaController != null){
            mMediaController.setMediaPlayer(this);
            View anchor = this.getParent() instanceof View ? (View) this.getParent() : this;
            mMediaController.setAnchor(anchor);
            mMediaController.setEnabled(isInPlaybackState());
        }
    }

    private boolean isInPlaybackState() {
        return (mMediaPlayer != null && mCurrentState != STATE_ERROR && mCurrentState != STATE_IDLE && mCurrentState != STATE_PREPARING);
    }

    private void release(boolean clearTargetState) {
        if (mMediaPlayer != null){
            mMediaPlayer.reset();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            if (clearTargetState)
                mTargetState = STATE_IDLE;
        }
    }

    private void setVideoLayout(int layout) {
        ViewGroup.LayoutParams params = getLayoutParams();
        Pair<Integer, Integer> resolution = ScreenResolution.getResolution(mContext);
        int windowWidth = resolution.first, windowHeight = resolution.second;
        float windowRatio = windowWidth / (float)windowHeight;
        int sarNum = mVideoSarNum;
        int sarDen = mVideoSarDen;
        if (mVideoWidth > 0 && mVideoHeight > 0){
            float videoRatio = mVideoWidth / (float)mVideoHeight;
            if (sarNum > 0 && sarDen > 0){
                videoRatio = videoRatio * sarNum / sarDen;
            }
            mSurfaceWidth = mVideoWidth;
            mSurfaceHeight = mVideoHeight;

            if (VIDEO_LAYOUT_ORIGIN == layout && mSurfaceWidth < windowWidth && mSurfaceHeight < windowHeight){
                params.width = (int) (mSurfaceHeight * videoRatio);
                params.height = mSurfaceHeight;
            } else if (layout == VIDEO_LAYOUT_ZOOM){
                params.width = windowRatio > videoRatio ? windowWidth : (int) (videoRatio * windowHeight);
                params.height = windowRatio < videoRatio ? windowHeight : (int) (windowWidth / videoRatio);
            } else {
                boolean full = layout == VIDEO_LAYOUT_STRETCH;
                params.width = (full || windowRatio < videoRatio) ? windowWidth : (int) (videoRatio * windowHeight);
                params.height = (full || windowRatio > videoRatio) ? windowHeight : (int) (windowWidth / videoRatio);
            }
            setLayoutParams(params);
            getHolder().setFixedSize(mSurfaceWidth, mSurfaceHeight);
            DebugLog.dfmt(TAG,
                    "VIDEO: %dx%dx%f[SAR:%d:%d], Surface: %dx%d, LP: %dx%d, Window: %dx%dx%f",
                    mVideoWidth, mVideoHeight, videoRatio, mVideoSarNum,
                    mVideoSarDen, mSurfaceWidth, mSurfaceHeight, params.width,
                    params.height, windowWidth, windowHeight, windowRatio);
        }
        mVideoLayout = layout;
    }

    public VideoPlayerView(Context context) {
        super(context);
        initVideoView(context);
    }

    public VideoPlayerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public VideoPlayerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initVideoView(context);
    }

    private void initVideoView(Context context) {
        mContext = context;
        mVideoWidth = 0;
        mVideoHeight = 0;
        mVideoSarNum = 0;
        mVideoSarDen = 0;
        getHolder().addCallback(mSHCallback);
        setFocusable(true);
        setFocusableInTouchMode(true);
        requestFocus();
        mCurrentState = STATE_IDLE;
        mTargetState = STATE_IDLE;
        if (context instanceof Activity)
            ((Activity)context).setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = getDefaultSize(mVideoWidth, widthMeasureSpec);
        int height = getDefaultSize(mVideoHeight, heightMeasureSpec);
        setMeasuredDimension(width, height);
    }

    public boolean isValid(){
        return mSurfaceHolder != null && mSurfaceHolder.getSurface().isValid();
    }

    public void setVideoPath(String path){
        setVideoURI(Uri.parse(path));
    }

    public void setVideoURI(Uri uri) {
        mUri = uri;
        mSeekWhenPrepared = 0;
        openVideo();
        requestLayout();
        invalidate();
    }

    public void setUserAgent(String agent){
        mUserAgent = agent;
    }

    public void stopPlayback(){
        if (mMediaPlayer != null){
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mCurrentState = STATE_IDLE;
            mTargetState = STATE_IDLE;
        }
    }

    public void setMediaController(MediaController mediaController) {
        mMediaController = mediaController;
    }

    public void setMediaBufferingIndicator(View mediaBufferingIndicator) {
        mMediaBufferingIndicator = mediaBufferingIndicator;
    }

    public void setOnPreparedListener(OnPreparedListener onPreparedListener) {
        mOnPreparedListener = onPreparedListener;
    }

    public void setOnCompletionListener(OnCompletionListener onCompletionListener) {
        mOnCompletionListener = onCompletionListener;
    }

    public void setOnErrorListener(OnErrorListener onErrorListener) {
        mOnErrorListener = onErrorListener;
    }

    public void setOnBufferingUpdateListener(OnBufferingUpdateListener onBufferingUpdateListener) {
        mOnBufferingUpdateListener = onBufferingUpdateListener;
    }

    public void setOnSeekCompleteListener(OnSeekCompleteListener onSeekCompleteListener) {
        mOnSeekCompleteListener = onSeekCompleteListener;
    }

    public void setOnInfoListener(OnInfoListener onInfoListener) {
        mOnInfoListener = onInfoListener;
    }

    public void setOnControlEventsListener(OnControlEventsListener onControlEventsListener) {
        mOnControlEventsListener = onControlEventsListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisibility();
        return false;
    }

    @Override
    public boolean onTrackballEvent(MotionEvent event) {
        if (isInPlaybackState() && mMediaController != null)
            toggleMediaControlsVisibility();
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean isKeyCodeSupported = keyCode != KeyEvent.KEYCODE_BACK
                && keyCode != KeyEvent.KEYCODE_VOLUME_UP
                && keyCode != KeyEvent.KEYCODE_VOLUME_DOWN
                && keyCode != KeyEvent.KEYCODE_MENU
                && keyCode != KeyEvent.KEYCODE_CALL
                && keyCode != KeyEvent.KEYCODE_ENDCALL;
        if (isInPlaybackState() && isKeyCodeSupported && mMediaController != null){
            if (keyCode == KeyEvent.KEYCODE_HEADSETHOOK
                    || keyCode == KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE
                    || keyCode == KeyEvent.KEYCODE_SPACE){
                if (mMediaPlayer.isPlaying()){
                    pause();
                    mMediaController.show();
                } else {
                    start();
                    mMediaController.hide();
                }
                return true;
            } else if (keyCode == KeyEvent.KEYCODE_MEDIA_STOP && mMediaPlayer.isPlaying()){
                pause();
                mMediaController.show();
            } else
                toggleMediaControlsVisibility();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggleMediaControlsVisibility() {
        if (mMediaController.isShowing())
            mMediaController.hide();
        else
            mMediaController.show();
    }

    @Override
    public void start() {
        if (isInPlaybackState()){
            mMediaPlayer.start();
            mCurrentState = STATE_PLAYING;
        }
        mTargetState = STATE_PLAYING;
        mOnControlEventsListener.onVideoResume();
    }

    @Override
    public void pause() {
        if (isInPlaybackState()){
            if (mMediaPlayer.isPlaying()){
                mMediaPlayer.pause();
                mCurrentState = STATE_PAUSED;
            }
            mTargetState = STATE_PAUSED;
            mOnControlEventsListener.onVideoPause();
        }
    }

    @Override
    public int getDuration() {
        if (isInPlaybackState()){
            if (mDuration > 0)
                return (int) mDuration;
            mDuration = mMediaPlayer.getDuration();
            return (int) mDuration;
        }
        mDuration = -1;
        return (int) mDuration;
    }

    @Override
    public int getCurrentPosition() {
        if (isInPlaybackState()){
            long currentPosition = mMediaPlayer.getCurrentPosition();
            return (int) currentPosition;
        }
        return 0;
    }

    @Override
    public void seekTo(long position) {
        if (isInPlaybackState()){
            mMediaPlayer.seekTo(position);
            mSeekWhenPrepared = 0;
        } else
            mSeekWhenPrepared = position;
    }

    @Override
    public boolean isPlaying() {
        return isInPlaybackState() && mMediaPlayer.isPlaying();
    }

    @Override
    public int getBufferPercentage() {
        if (mMediaPlayer != null)
            return mCurrentBufferPercentage;
        return 0;
    }

    @Override
    public boolean canPause() {
        return mCanPause;
    }

    public boolean canSeekForward() {
        return mCanSeekForward;
    }

    public boolean canSeekBack() {
        return mCanSeekBack;
    }

    public int getVideoWidth() {
        return mVideoWidth;
    }

    public int getVideoHeight() {
        return mVideoHeight;
    }

    public interface OnControlEventsListener {
        void onVideoPause();
        void onVideoResume();
    }
}
