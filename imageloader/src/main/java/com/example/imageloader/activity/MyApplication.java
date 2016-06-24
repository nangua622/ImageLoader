package com.example.imageloader.activity;

import android.app.Application;

import com.example.imageloader.utils.AppUtils;

import org.xutils.BuildConfig;
import org.xutils.x;

/**
 * Created by nBB on 16/6/21.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {

        super.onCreate();
        //初始化xutils框架
        x.Ext.init(this);
        x.Ext.setDebug(BuildConfig.DEBUG);
        
        AppUtils.setContext(this);
    }
}
