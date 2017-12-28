package com.example.multi_downloader.manager;

/**
 * Created by jackypeng on 2017/12/22.
 */

public class DownloadConfig {
    private static final byte DEFAULT_MAX_THREADS_COUNT = 2;
    private static final int DEFAULT_CONNECT_TIMEOUT = 10000;
    private int connectTimeout;
    private int maxThreads;  //最大同时下载线程数量

    public DownloadConfig() {
        this.connectTimeout = DEFAULT_CONNECT_TIMEOUT;
        this.maxThreads = DEFAULT_MAX_THREADS_COUNT;
    }

    public DownloadConfig(int connectTimeout, int maxThreads) {
        this.connectTimeout = connectTimeout;
        this.maxThreads = maxThreads;
    }

    public int getConnectTimeout() {
        return connectTimeout;
    }

    public void setConnectTimeout(int connectTimeout) {
        this.connectTimeout = connectTimeout;
    }

    public int getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(int maxThreads) {
        this.maxThreads = maxThreads;
    }
}
