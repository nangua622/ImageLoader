package com.example.imageloader.listener;

import org.xutils.common.Callback;

/**
 * Created by nBB on 16/6/22.
 */
public class MyCacheCallback<T> implements Callback.CacheCallback<T>{

    @Override
    public void onSuccess(T result) {

    }

    @Override
    public void onError(Throwable ex, boolean isOnCallback) {

    }

    @Override
    public void onCancelled(CancelledException cex) {

    }

    @Override
    public void onFinished() {

    }

    @Override
    public boolean onCache(T result) {
        return false;
    }
}
