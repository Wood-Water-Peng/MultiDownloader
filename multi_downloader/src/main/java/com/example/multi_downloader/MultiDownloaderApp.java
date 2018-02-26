package com.example.multi_downloader;

import android.app.Application;
import android.content.Context;
import android.os.Handler;

/**
 * Created by jackypeng on 2017/12/27.
 */

public class MultiDownloaderApp extends Application {
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
        initDB();
    }

    private void initDB() {
    // 下面代码仅仅需要执行一次，一般会放在application

    }
}
