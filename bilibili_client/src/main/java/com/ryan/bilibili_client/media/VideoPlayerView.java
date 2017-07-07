package com.ryan.bilibili_client.media;

import android.content.Context;
import android.net.Uri;
import android.util.Pair;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.ryan.bilibili_client.media.callback.MediaPlayerListener;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnCompletionListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnErrorListener;
import tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener;
import tv.danmaku.ijk.media.player.pragma.DebugLog;

import static tv.danmaku.ijk.media.player.IMediaPlayer.*;

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
    }

    @Override
    public void start() {

    }

    @Override
    public void pause() {

    }

    @Override
    public int getDuration() {
        return 0;
    }

    @Override
    public int getCurrentPosition() {
        return 0;
    }

    @Override
    public void seekTo(long position) {

    }

    @Override
    public boolean isPlaying() {
        return false;
    }

    @Override
    public int getBufferPercentage() {
        return 0;
    }

    @Override
    public boolean canPause() {
        return false;
    }

    public interface OnControlEventsListener {
        void onVideoPause();
        void onVideoResume();
    }
}
