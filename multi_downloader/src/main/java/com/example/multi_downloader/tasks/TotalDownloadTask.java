package com.example.multi_downloader.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.multi_downloader.DB.DBManager;
import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.listeners.DownloadFileListener;
import com.example.multi_downloader.manager.DownloadEngine;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class TotalDownloadTask extends AsyncTask<DownloadInfo, Integer, DownloadInfo> {
    private static final String TAG = "TotalDownloadTask";
    private DownloadInfo downloadInfo;
    private DownloadEngine engine;
    private DownloadFileTask downloadFileTask;
    private DownloadFileListener downloadFileListener;

    @Override
    protected DownloadInfo doInBackground(DownloadInfo... params) {
        URL url;
        DownloadInfo downloadInfo = params[0];
        HttpURLConnection httpConnection = null;
        try {
            url = new URL(downloadInfo.getUrl());
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(10000);
            httpConnection.setReadTimeout(10000);
            httpConnection.setRequestMethod("GET");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                downloadInfo.setTotalSize(httpConnection.getContentLength());
                DBManager.getInstance().updateInfo(downloadInfo);
                return downloadInfo;
            } else {
                downloadFileListener.onFetchFileInfoFailed(downloadInfo);
                DBManager.getInstance().updateInfo(downloadInfo);
                return null;
            }

        } catch (Exception e) {
            Log.i(TAG, "准备下载异常:" + e.getMessage());
            downloadFileListener.onFetchFileInfoFailed(downloadInfo);
            DBManager.getInstance().updateInfo(downloadInfo);
            return null;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

    @Override
    protected void onPostExecute(DownloadInfo downloadInfo) {
        if (downloadInfo != null) {
            downloadFile(downloadInfo);
        }
    }


    public TotalDownloadTask(DownloadInfo downloadInfo, DownloadEngine engine, DownloadFileListener listener) {
        this.downloadInfo = downloadInfo;
        this.engine = engine;
        this.downloadFileListener = listener;
    }

    public void start() {
        execute(downloadInfo);
    }

    private void downloadFile(DownloadInfo downloadInfo) {
        downloadFileTask = new DownloadFileTask(downloadInfo, downloadFileListener);
        engine.submit(downloadFileTask);
    }

}
