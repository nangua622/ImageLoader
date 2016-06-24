package com.example.imageloader.utils;

import android.os.Environment;

/**
 * Created by nBB on 16/6/22.
 */
public class Constants {
    public static final int S_WEB = 0 ;//看网络图片
    public static final int S_LOCAL = 1 ;//看下好的本地图片
    public static  int state = S_WEB;//当前状态
    public static final String downloadPath = Environment.getExternalStorageDirectory()+"/imageload";
}
