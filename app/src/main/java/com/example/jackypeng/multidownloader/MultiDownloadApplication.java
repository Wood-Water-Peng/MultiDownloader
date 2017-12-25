package com.example.jackypeng.multidownloader;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;

import com.example.jackypeng.multidownloader.bean.DaoMaster;
import com.example.jackypeng.multidownloader.bean.DaoSession;
import com.example.jackypeng.multidownloader.db.DBManager;

import java.io.DataOutput;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class MultiDownloadApplication extends Application {
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
