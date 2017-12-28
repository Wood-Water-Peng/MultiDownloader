package com.example.multi_downloader.utils;


import com.example.multi_downloader.MultiDownloaderApp;

/**
 * Created by jackypeng on 2017/12/21.
 */

public class UIUtil {
    public static void runOnUIThread(Runnable runnable) {
        MultiDownloaderApp.getAppHandler().post(runnable);
    }
}
