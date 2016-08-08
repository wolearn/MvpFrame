package com.wolearn.mvpframe;

import android.app.Application;

import com.wolearn.mvpframelib.frame.Mvp;

/**
 * Created by wulei
 * Data: 2016/8/8.
 */
public class MyApplication extends Application{
    @Override
    public void onCreate() {
        super.onCreate();

        Mvp.getInstance().init(this);
    }
}
