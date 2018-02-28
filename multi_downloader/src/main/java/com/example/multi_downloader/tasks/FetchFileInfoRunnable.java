package com.example.multi_downloader.tasks;

import com.example.multi_downloader.bean.DownloadInfo;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by jackypeng on 2018/2/27.
 */

public class FetchFileInfoRunnable implements Runnable {

    private DownloadInfo downloadInfo;

    public FetchFileInfoRunnable(DownloadInfo downloadInfo) {
        this.downloadInfo = downloadInfo;
    }

    @Override
    public void run() {
        try {
           tryFetchFileInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private DownloadInfo tryFetchFileInfo() throws Exception {
        URL url;
        HttpURLConnection httpConnection = null;
        url = new URL(downloadInfo.getUrl());
        httpConnection = (HttpURLConnection) url.openConnection();
        httpConnection.setConnectTimeout(10000);
        httpConnection.setReadTimeout(10000);
        httpConnection.setRequestMethod("GET");
        int responseCode = httpConnection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            downloadInfo.setTotalSize(httpConnection.getContentLength());
            return downloadInfo;
        } else {
            return null;
        }
    }
}
