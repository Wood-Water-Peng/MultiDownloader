package com.example.multi_downloader.events;


import com.example.multi_downloader.bean.DownloadInfo;

/**
 * Created by jackypeng on 2017/12/26.
 */

public class TaskFinishedEvent {
    private DownloadInfo downloadInfo;

    public TaskFinishedEvent(DownloadInfo info) {
        this.downloadInfo = info;
    }

    public DownloadInfo getDownloadInfo() {
        return downloadInfo;
    }

    public void setDownloadInfo(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }
}
