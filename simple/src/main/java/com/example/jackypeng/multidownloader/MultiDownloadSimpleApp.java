package com.example.jackypeng.multidownloader;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by jackypeng on 2018/2/26.
 */

public class MultiDownloadSimpleApp extends Application {
    private static Context mContext;
    private static Handler sHandler;

    public static Context getContext() {
        return mContext;
    }

    public static Handler getAppHandler() {
        return sHandler;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        sHandler = new Handler();
    }
}
