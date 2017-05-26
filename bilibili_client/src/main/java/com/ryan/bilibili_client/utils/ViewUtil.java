package com.ryan.bilibili_client.utils;

import android.view.View;

/**
 * Created by MUFCRyan on 2017/5/26.
 */

public class ViewUtil {
    public static <T> T find(View view, int resId){
        return (T) view.findViewById(resId);
    }
}
