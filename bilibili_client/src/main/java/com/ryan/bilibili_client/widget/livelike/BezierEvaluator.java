package com.ryan.bilibili_client.widget.livelike;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * Created by MUFCRyan on 2017/5/31.
 * 贝塞尔曲线运动轨迹估值器
 */

public class BezierEvaluator implements TypeEvaluator<PointF> {
    PointF mPoint1, mPoint2;
    BezierEvaluator(PointF point1, PointF point2){
        mPoint1 = point1;
        mPoint2 = point2;
    }

    @Override
    public PointF evaluate(float time, PointF startValue, PointF endValue) {
        float timeLeft = 1.0f - time;
        PointF pointF = new PointF();
        pointF.x = timeLeft * timeLeft * timeLeft * (startValue.x)
                + 3 * timeLeft * timeLeft * time * (mPoint1.x)
                + 3 * timeLeft * time * time * (mPoint2.x)
                + time * time * time * (endValue.x);
        pointF.y = timeLeft * timeLeft * timeLeft * (startValue.y)
                + 3 * timeLeft * timeLeft * time * (mPoint1.y)
                + 3 * timeLeft * time * time * (mPoint2.y)
                + time * time * time * (endValue.y);
        return pointF;
    }
}
