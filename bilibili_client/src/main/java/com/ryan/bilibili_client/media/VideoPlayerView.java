package com.ryan.bilibili_client.media;

import android.content.Context;
import android.view.SurfaceView;

import com.ryan.bilibili_client.media.callback.MediaPlayerListener;

/**
 * Created by MUFCRyan on 2017/7/6.
 * 自定义 VideoView
 */

public class VideoPlayerView extends SurfaceView implements MediaPlayerListener{
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
}
