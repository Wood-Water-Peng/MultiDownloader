package com.example.multi_downloader.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.util.Log;

import com.example.multi_downloader.BuildConfig;
import com.example.multi_downloader.MultiDownloaderApp;

import java.io.File;
import java.io.IOException;

/**
 * 包管理器
 *
 * @author evan.liu
 */
public class PackageUtil {
    /**
     * 检测有没此应用
     *
     * @param context 上下文
     * @return
     */
    private static final String TAG = "PackageUtil";

    public static boolean checkApkExist(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_UNINSTALLED_PACKAGES);
            if (pi != null) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取当前版本号
     *
     * @param context 上下文
     * @return
     */
    public static int getVersionCode(Context context) {
        int versionCode = -1;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (pi != null) {
                versionCode = pi.versionCode;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取当前版本名
     *
     * @param context 上下文
     * @return
     */
    public static String getVersionName(Context context) {
        String versionName = null;
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
            if (pi != null) {
                versionName = pi.versionName;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 安装apk
     *
     * @param context   上下文
     * @param cachePath apk缓存路径
     */
    public static void installApk(Context context, String cachePath) {
        chmod("777", cachePath);
        Intent intent = new Intent("android.intent.action.VIEW");
        Log.i(TAG, "cachePath:" + cachePath);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(MultiDownloaderApp.getContext(),
                    BuildConfig.APPLICATION_ID + ".fileProvider", new File(cachePath));
            // 给目标应用一个临时授权
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.parse("file://" + cachePath);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    private static void chmod(String permission, String path) {
        try {
            String command = "chmod " + permission + " " + path;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
