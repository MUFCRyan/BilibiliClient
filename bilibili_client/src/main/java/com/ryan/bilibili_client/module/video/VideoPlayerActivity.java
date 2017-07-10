package com.ryan.bilibili_client.module.video;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ryan.bilibili_client.R;
import com.ryan.bilibili_client.base.RxBaseActivity;
import com.ryan.bilibili_client.media.MediaController;
import com.ryan.bilibili_client.media.VideoPlayerView;
import com.ryan.bilibili_client.media.callback.DanmakuSwitchListener;
import com.ryan.bilibili_client.media.callback.VideoBackListener;
import com.ryan.bilibili_client.media.danmuku.BiliDanmukuDownloadUtil;
import com.ryan.bilibili_client.network.RetrofitHelper;
import com.ryan.bilibili_client.utils.ConstantUtil;

import java.util.HashMap;

import butterknife.BindView;
import master.flame.danmaku.controller.DrawHandler;
import master.flame.danmaku.controller.IDanmakuView;
import master.flame.danmaku.danmaku.model.BaseDanmaku;
import master.flame.danmaku.danmaku.model.DanmakuTimer;
import master.flame.danmaku.danmaku.model.IDisplayer;
import master.flame.danmaku.danmaku.model.android.DanmakuContext;
import master.flame.danmaku.danmaku.parser.BaseDanmakuParser;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import tv.danmaku.ijk.media.player.IMediaPlayer;

public class VideoPlayerActivity extends RxBaseActivity implements DanmakuSwitchListener, VideoBackListener{

    public static void launch(Activity activity, int cid, String title) {
        Intent mIntent = new Intent(activity, VideoPlayerActivity.class);
        mIntent.putExtra(ConstantUtil.EXTRA_CID, cid);
        mIntent.putExtra(ConstantUtil.EXTRA_TITLE, title);
        activity.startActivity(mIntent);
    }

    @BindView(R.id.sv_danmaku)
    IDanmakuView mDanmakuView;

    @BindView(R.id.playerView)
    VideoPlayerView mPlayerView;

    @BindView(R.id.buffering_indicator)
    View mBufferingIndicator;

    @BindView(R.id.video_start)
    RelativeLayout mVideoPrepareLayout;

    @BindView(R.id.bili_anim)
    ImageView mAnimImageView;

    @BindView(R.id.video_start_info)
    TextView mPrepareText;

    private AnimationDrawable mLoadingAnim;
    private DanmakuContext mDanmakuContext;
    private int mCid;
    private String mTitle;
    private int mLastPosition = 0;
    private String mStartText = "初始化播放器...";

    /** 视频缓冲事件回调 */
    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int extra) {
            if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_START){
                if (mDanmakuView != null && mDanmakuView.isPrepared()){
                    mDanmakuView.pause();
                    if (mBufferingIndicator != null)
                        mBufferingIndicator.setVisibility(View.VISIBLE);
                }
            } else if (what == IMediaPlayer.MEDIA_INFO_BUFFERING_END){
                if (mDanmakuView != null && mDanmakuView.isPrepared())
                    mDanmakuView.resume();
                if (mBufferingIndicator != null)
                    mBufferingIndicator.setVisibility(View.GONE);
            }
            return true;
        }
    };

    /** 视频跳转事件回调 */
    private IMediaPlayer.OnSeekCompleteListener mOnSeekCompleteListener = new IMediaPlayer.OnSeekCompleteListener() {
        @Override
        public void onSeekComplete(IMediaPlayer player) {
            if (mDanmakuView != null && mDanmakuView.isPrepared())
                mDanmakuView.seekTo(player.getCurrentPosition());
        }
    };

    /** 视频播放完成事件回调 */
    private IMediaPlayer.OnCompletionListener mOnCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer player) {
            if (mDanmakuView != null && mDanmakuView.isPrepared()){
                mDanmakuView.seekTo((long) 0);
                mDanmakuView.pause();
            }
            mPlayerView.pause();
        }
    };

    /** 控制条控制状态事件回调 */
    private VideoPlayerView.OnControlEventsListener mOnControlEventsListener = new VideoPlayerView.OnControlEventsListener() {
        @Override
        public void onVideoPause() {
            if (mDanmakuView != null && mDanmakuView.isPrepared())
                mDanmakuView.pause();
        }

        @Override
        public void onVideoResume() {
            if (mDanmakuView != null && mDanmakuView.isPrepared())
                mDanmakuView.resume();
        }
    };

    @Override
    public int getLayoutId() {
        return R.layout.activity_video_player;
    }

    @Override
    public void initViews(Bundle savedInstanceState) {
        Intent intent = getIntent();
        if (intent != null){
            mCid = intent.getIntExtra(ConstantUtil.EXTRA_CID, 0);
            mTitle = intent.getStringExtra(ConstantUtil.EXTRA_TITLE);
        }
        initAnimation();
        initMediaPlayer();
    }

    @Override
    public void initToolBar() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().setBackgroundDrawable(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mDanmakuView != null && mDanmakuView.isPrepared() && mDanmakuView.isPaused())
            mDanmakuView.seekTo((long) mLastPosition);
        if (mPlayerView != null && !mPlayerView.isPlaying())
            mPlayerView.seekTo(mLastPosition);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mPlayerView != null){
            mLastPosition = mPlayerView.getCurrentPosition();
            mPlayerView.pause();
        }
        if (mDanmakuView != null && mDanmakuView.isPrepared())
            mDanmakuView.pause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDanmakuView != null){
            mDanmakuView.release();
            mDanmakuView = null;
        }
        if (mLoadingAnim != null)
            mLoadingAnim.stop();
        mLoadingAnim = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPlayerView != null && mPlayerView.isDrawingCacheEnabled())
            mPlayerView.destroyDrawingCache();
        if (mDanmakuView != null && mDanmakuView.isPaused()){
            mDanmakuView.release();
            mDanmakuView = null;
        }
        if (mLoadingAnim != null){
            mLoadingAnim.stop();
            mLoadingAnim = null;
        }
    }

    public void setDanmakuShow(boolean isShow){
        if (mDanmakuView != null){
            if (isShow)
                mDanmakuView.show();
            else
                mDanmakuView.hide();
        }
    }

    public void back(){
        onBackPressed();
    }

    private void initAnimation() {
        mVideoPrepareLayout.setVisibility(View.VISIBLE);
        mStartText += "【完成】\n解析视频地址...【完成】\n全舰弹幕填装...";
        mPrepareText.setText(mStartText);
        mLoadingAnim = (AnimationDrawable) mAnimImageView.getBackground();
        mLoadingAnim.start();
    }

    private void initMediaPlayer() {
        MediaController controller = new MediaController(this);
        controller.setTitle(mTitle);
        mPlayerView.setMediaController(controller);
        mPlayerView.setMediaBufferingIndicator(mBufferingIndicator);
        mPlayerView.requestFocus();
        mPlayerView.setOnInfoListener(mOnInfoListener);
        mPlayerView.setOnSeekCompleteListener(mOnSeekCompleteListener);
        mPlayerView.setOnCompletionListener(mOnCompletionListener);
        mPlayerView.setOnControlEventsListener(mOnControlEventsListener);
        // 设置弹幕开关监听
        controller.setDanmakuSwitchListener(this);
        // 设置返回键监听
        controller.setVideoBackListener(this);
        // 配置弹幕库
        mDanmakuView.enableDanmakuDrawingCache(true);
        // 设置最大显示行数
        HashMap<Integer, Integer> maxLinesPair = new HashMap<>();
        // 滚动屏幕最大显示五行
        maxLinesPair.put(BaseDanmaku.TYPE_SCROLL_RL, 5);
        // 设置是否禁止重叠
        HashMap<Integer, Boolean> overlappingEnablePair = new HashMap<>();
        overlappingEnablePair.put(BaseDanmaku.TYPE_SCROLL_RL, true);
        overlappingEnablePair.put(BaseDanmaku.TYPE_FIX_TOP, true);
        // 设置弹幕样式
        mDanmakuContext = DanmakuContext.create();
        mDanmakuContext.setDanmakuStyle(IDisplayer.DANMAKU_STYLE_STROKEN, 3)
                .setDuplicateMergingEnabled(false)
                .setScrollSpeedFactor(1.2f)
                .setScaleTextSize(0.8f)
                .setMaximumLines(maxLinesPair)
                .preventOverlapping(overlappingEnablePair);
        loadData();
    }

    @Override
    public void loadData() {
        RetrofitHelper.getBiliGoAPI()
                .getHDVideoUrl(mCid, 4, ConstantUtil.VIDEO_TYPE_MP4)
                .compose(bindToLifecycle())
                .map(videoInfo -> Uri.parse(videoInfo.getDurl().get(0).getUrl()))
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<Uri, Observable<BaseDanmakuParser>>() {
                    @Override
                    public Observable<BaseDanmakuParser> call(Uri uri) {
                        mPlayerView.setVideoURI(uri);
                        mPlayerView.setOnClickListener(v -> {
                            mLoadingAnim.stop();
                            mStartText += "【完成】\n视频缓冲中...";
                            mPrepareText.setText(mStartText);
                            mVideoPrepareLayout.setVisibility(View.GONE);
                        });
                        String url = "http://comment.bilibili.com/" + mCid + ".xml";
                        return BiliDanmukuDownloadUtil.downloadXML(url);
                    }
                })
                .subscribeOn(Schedulers.io())
                .subscribe(baseDanmakuParser -> {
                    mDanmakuView.prepare(baseDanmakuParser, mDanmakuContext);
                    mDanmakuView.showFPS(false);
                    mDanmakuView.enableDanmakuDrawingCache(false);
                    mDanmakuView.setCallback(new DrawHandler.Callback() {
                        @Override
                        public void prepared() {
                            mDanmakuView.start();
                        }

                        @Override
                        public void updateTimer(DanmakuTimer timer) {

                        }

                        @Override
                        public void danmakuShown(BaseDanmaku danmaku) {

                        }

                        @Override
                        public void drawingFinished() {

                        }
                    });
                    mPlayerView.start();
                }, throwable -> {
                    mStartText += "【失败】\n视频缓冲中...";
                    mPrepareText.setText(mStartText);
                    mStartText += "【失败】\n" + throwable.getMessage();
                    mPrepareText.setText(mStartText);
                });
    }
}
