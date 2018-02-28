package com.example.multi_downloader.utils;

import android.content.Context;
import android.view.WindowManager;

import com.example.multi_downloader.MultiDownloaderApp;

/**
 * Created by pj on 2016/12/2.
 * 保存手机相关信息
 * 单例模式
 */
public class DeviceInfo {

    private DeviceInfo() {
    }

    private static DeviceInfo mInstance;

    public static DeviceInfo getInstance() {
        synchronized (DeviceInfo.class) {
            if (mInstance == null) {
                synchronized (DeviceInfo.class) {
                    if (mInstance == null) {
                        mInstance = new DeviceInfo();
                    }
                }
            }
        }
        return mInstance;
    }

    public int getDevice_width() {
        WindowManager wm = (WindowManager) MultiDownloaderApp.getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getWidth();
    }


    public int getDevice_height() {
        WindowManager wm = (WindowManager) MultiDownloaderApp.getContext().getSystemService(Context.WINDOW_SERVICE);
        return wm.getDefaultDisplay().getHeight();
    }

}
