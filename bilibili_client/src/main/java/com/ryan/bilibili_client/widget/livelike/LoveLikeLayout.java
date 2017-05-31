package com.ryan.bilibili_client.widget.livelike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.ryan.bilibili_client.R;

import java.util.Random;

/**
 * Created by MUFCRyan on 2017/5/31.
 * 直播送礼物特效自定义控件
 */

public class LoveLikeLayout extends RelativeLayout {

    private Interpolator line = new LinearInterpolator();
    private Interpolator accelerate = new AccelerateInterpolator();
    private Interpolator decelerate = new DecelerateInterpolator();
    private Interpolator accDecelerate = new AccelerateDecelerateInterpolator();
    private Interpolator[] mInterpolators;
    private Random mRandom = new Random();
    private Drawable[] mDrawables; // 爱心图片数组
    private int mDrawableWidth, mDrawableHeight; // 图片宽、高
    private int mMeasuredWidth, mMeasuredHeight; // 布局宽、高
    private LayoutParams mLayoutParams;

    public LoveLikeLayout(Context context) {
        this(context, null);
    }

    public LoveLikeLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoveLikeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mDrawables = new Drawable[7];
        Drawable one = getResources().getDrawable(R.drawable.ic_live_like_01);
        Drawable two = getResources().getDrawable(R.drawable.ic_live_like_02);
        Drawable three = getResources().getDrawable(R.drawable.ic_live_like_03);
        Drawable four = getResources().getDrawable(R.drawable.ic_live_like_04);
        Drawable five = getResources().getDrawable(R.drawable.ic_live_like_05);
        mDrawables[0] = one;
        mDrawables[1] = two;
        mDrawables[2] = three;
        mDrawables[3] = four;
        mDrawables[4] = five;
        mDrawableWidth = one.getIntrinsicWidth();
        mDrawableHeight = one.getIntrinsicHeight();
        mLayoutParams = new LayoutParams(mDrawableWidth, mDrawableHeight);
        mLayoutParams.addRule(CENTER_HORIZONTAL, TRUE);
        mLayoutParams.addRule(ALIGN_BOTTOM, TRUE);
        mInterpolators = new Interpolator[4];
        mInterpolators[0] = line;
        mInterpolators[1] = accelerate;
        mInterpolators[2] = decelerate;
        mInterpolators[3] = accDecelerate;
    }

    /** 测量 layout 的高宽，用于计算爱心的显示位置 */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasuredWidth = getMeasuredWidth();
        mMeasuredHeight = getMeasuredHeight();
    }

    public void addLove(){
        ImageView imageView = new ImageView(getContext());
        imageView.setImageDrawable(mDrawables[mRandom.nextInt(3)]);
        imageView.setLayoutParams(mLayoutParams);
        addView(imageView);
        Animator animator = getAnimator(imageView);
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                removeView(imageView);
                super.onAnimationEnd(animation);
            }
        });
        animator.start();
    }

    /** 爱心的显示和运行轨迹动画组合实现 */
    private Animator getAnimator(View view) {
        AnimatorSet enterAnimatorSet = getEnterAnimatorSet(view);
        ValueAnimator valueAnimator = getBezierAnimator(view);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(enterAnimatorSet);
        animatorSet.playSequentially(enterAnimatorSet, valueAnimator);
        animatorSet.setInterpolator(mInterpolators[mRandom.nextInt(4)]);
        animatorSet.setTarget(view);
        return animatorSet;
    }

    /** 爱心显示动画实现 */
    private AnimatorSet getEnterAnimatorSet(View view) {
        ObjectAnimator alpha = ObjectAnimator.ofFloat(view, ALPHA, 0.2f, 1.0f);
        ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, SCALE_X, 0.2f, 1.0f);
        ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, SCALE_Y, 0.2f, 1.0f);

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.setDuration(500);
        animatorSet.playTogether(alpha, scaleX, scaleY);
        animatorSet.setTarget(view);
        return animatorSet;
    }

    /** 爱心轨迹动画实现 */
    private ValueAnimator getBezierAnimator(View view) {
        BezierEvaluator bezierEvaluator = new BezierEvaluator(getPoint(2), getPoint(1));
        ValueAnimator animator = ValueAnimator.ofObject(bezierEvaluator,
                new PointF((mMeasuredWidth - mDrawableWidth) / 2, mMeasuredHeight - mDrawableHeight),
                new PointF(mRandom.nextInt(getWidth()), 0));
        animator.setDuration(3000);
        animator.setTarget(view);
        animator.addUpdateListener(valueAnimator -> {
            // 获取贝塞尔曲线运动轨迹
            PointF animatedValue = (PointF) valueAnimator.getAnimatedValue();
            view.setScaleX(animatedValue.x);
            view.setScaleY(animatedValue.y);
            view.setAlpha(1 - valueAnimator.getAnimatedFraction());
        });
        return animator;
    }

    private PointF getPoint(int scale){
        PointF pointF = new PointF();
        pointF.x = mRandom.nextInt((mMeasuredWidth - 100));
        pointF.y = mRandom.nextInt((mMeasuredHeight - 100)) / scale;
        return pointF;
    }
}
