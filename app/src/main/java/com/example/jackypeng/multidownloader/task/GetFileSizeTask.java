package com.example.jackypeng.multidownloader.task;

import com.example.jackypeng.multidownloader.bean.DownloadInfo;
import com.example.jackypeng.multidownloader.listeners.GetFileSizeListener;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class GetFileSizeTask implements Runnable {
    private DownloadInfo downloadInfo;
    private GetFileSizeListener listener;

    public GetFileSizeTask(DownloadInfo downloadInfo, GetFileSizeListener listener) {
        this.downloadInfo = downloadInfo;
        this.listener = listener;
    }

    @Override
    public void run() {
        final URL url;
        HttpURLConnection httpConnection = null;
        try {
            url = new URL(downloadInfo.getUrl());
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(10000);
            httpConnection.setRequestMethod("GET");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                if (listener != null) {
                    listener.onSuccess(httpConnection.getContentLength());
                }
            } else {
                if (listener != null) {
                    listener.onFailed("返回码错误");
                }
            }
        } catch (Exception e) {
            if (listener != null) {
                listener.onFailed(e.getMessage());
            }
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }
}
