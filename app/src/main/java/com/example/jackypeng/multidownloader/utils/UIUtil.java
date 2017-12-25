package com.example.jackypeng.multidownloader.utils;

import com.example.jackypeng.multidownloader.MultiDownloadApplication;

/**
 * Created by jackypeng on 2017/12/21.
 */

public class UIUtil {
    public static void runOnUIThread(Runnable runnable) {
        MultiDownloadApplication.getAppHandler().post(runnable);
    }
}
