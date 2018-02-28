package com.example.multi_downloader.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.multi_downloader.DB.DBManager;
import com.example.multi_downloader.bean.DownloadInfo;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jackypeng on 2018/2/27.
 */

public class FetchFileInfoTask extends AsyncTask<DownloadInfo, Integer, DownloadInfo> {
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
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
        return downloadInfo;
    }
}
