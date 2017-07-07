package com.ryan.bilibili_client.media;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by MUFCRyan on 2017/7/7.
 *
 */
@SuppressLint("DrawAllocation")
public class OutlineTextView extends TextView{
    public OutlineTextView(Context context) {
        super(context);
    }

    public OutlineTextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public OutlineTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
