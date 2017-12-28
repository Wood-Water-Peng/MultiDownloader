package com.example.multi_downloader.listeners;


import com.example.multi_downloader.bean.DownloadInfo;

/**
 * Created by jackypeng on 2017/12/20.
 */

public interface DownloadFileListener {
    public static final int READY = 1;
    public static final int LOADING = 2;
    public static final int PAUSED = 3;
    public static final int FINISHED = 4;

    void onLoading(DownloadInfo info);
    void onLoadFinished(DownloadInfo info);
    void onLoadPaused(DownloadInfo info);
    void onLoadFailed(DownloadInfo info);
}
