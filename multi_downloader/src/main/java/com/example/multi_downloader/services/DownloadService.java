package com.example.multi_downloader.services;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.manager.DownloadConfig;
import com.example.multi_downloader.manager.DownloadManager;

/**
 * Created by jackypeng on 2017/12/22.
 */

public class DownloadService extends Service {
    private static final String TAG = "DownloadService";
    private LocalBinder binder = new LocalBinder();
    private static DownloadManager downloadManager;

    public class LocalBinder extends Binder {
        // 声明一个方法，getService。（提供给客户端调用）
        public DownloadService getService() {
            // 返回当前对象LocalService,这样我们就可在客户端端调用Service的公共方法了
            return DownloadService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        downloadManager = DownloadManager.getInstance(new DownloadConfig());
    }

    public static DownloadManager getDownloadManager() {
        return DownloadManager.getInstance(new DownloadConfig());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public void download(DownloadInfo downloadInfo) {
        downloadManager.startDownload(downloadInfo);
    }

    public void pause(DownloadInfo downloadInfo) {
        downloadManager.pauseDownload(downloadInfo);
    }

    public void resume(DownloadInfo downloadInfo) {
        downloadManager.resumeDownload(downloadInfo);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "---onDestroy---");
        downloadManager.onDestroy();
    }
}
