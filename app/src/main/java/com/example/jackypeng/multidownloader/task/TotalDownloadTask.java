package com.example.jackypeng.multidownloader.task;

import android.util.Log;

import com.example.jackypeng.multidownloader.bean.DownloadInfo;
import com.example.jackypeng.multidownloader.listeners.DataListener;
import com.example.jackypeng.multidownloader.listeners.DownloadFileListener;
import com.example.jackypeng.multidownloader.listeners.GetFileSizeListener;
import com.example.jackypeng.multidownloader.manager.DownloadEngine;
import com.example.jackypeng.multidownloader.utils.UIUtil;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class TotalDownloadTask implements DownloadFileListener, GetFileSizeListener {
    private static final String TAG = "TotalDownloadTask";
    private DownloadInfo downloadInfo;
    private DownloadEngine engine;
    private DownloadFileTask downloadFileTask;
    private GetFileSizeTask getFileSizeTask;
    private DownloadSuccessListener resultListener;

    public interface DownloadSuccessListener {
        void onSuccess(DownloadInfo downloadInfo);
    }

    public TotalDownloadTask(DownloadInfo downloadInfo, DownloadEngine engine, DownloadSuccessListener listener) {
        this.downloadInfo = downloadInfo;
        this.engine = engine;
        this.resultListener = listener;
    }

    public void start() {
        getFileInfo(downloadInfo);
    }

    public void pause() {
        if (downloadFileTask != null) {
            downloadFileTask.pauseTask();
        }
    }

    private void downloadFile(DownloadInfo downloadInfo) {
        downloadFileTask = new DownloadFileTask(downloadInfo, this);
        engine.submit(downloadFileTask);
    }

    private void getFileInfo(DownloadInfo downloadInfo) {
        getFileSizeTask = new GetFileSizeTask(downloadInfo, this);
        engine.submit(getFileSizeTask);
    }

    @Override
    public void onSuccess(final long size) {
        Log.i(TAG, "onSuccess_downloadInfo:" + downloadInfo.toString());
        //该方法在子线程中执行
        UIUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                downloadInfo.setTotalSize(size);
                downloadFile(downloadInfo);
            }
        });
    }

    @Override
    public void onFailed(String msg) {
        //更新界面
    }

    @Override
    public void onLoading(final DownloadInfo info) {
//        Log.i(TAG, "downloadInfo:" + downloadInfo.getUrl());
        UIUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                info.setStatus(DownloadInfo.LOADING);
                //更新界面
                DataListener listener = info.getListener();
                if (listener != null) {
                    listener.onRefresh();
                }
            }
        });
    }

    @Override
    public void onLoadFinished(final DownloadInfo info) {
        //更新界面
        UIUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //更新界面
                info.setStatus(DownloadInfo.FINISHED);
                //更新界面
                DataListener listener = info.getListener();
                if (listener != null) {
                    listener.onRefresh();
                }
                if (resultListener != null) {
                    resultListener.onSuccess(info);
                }
            }
        });
    }

    @Override
    public void onLoadPaused(final DownloadInfo info) {
        UIUtil.runOnUIThread(new Runnable() {
            @Override
            public void run() {
                //更新界面
                info.setStatus(DownloadInfo.PAUSED);
                //更新界面
                DataListener listener = info.getListener();
                if (listener != null) {
                    listener.onRefresh();
                }
            }
        });
    }
}
