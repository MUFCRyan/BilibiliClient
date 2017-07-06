package com.ryan.bilibili_client.media.callback;

/**
 * Created by MUFCRyan on 2017/7/6.
 * 视频控制回调接口
 */

public interface MediaPlayerListener {
    void start();
    void pause();
    int getDuration();
    int getCurrentPosition();
    void seekTo(long position);
    boolean isPlaying();
    int getBufferPercentage();
    boolean canPause();
}
