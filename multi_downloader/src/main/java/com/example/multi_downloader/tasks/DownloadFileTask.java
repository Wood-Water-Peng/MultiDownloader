package com.example.multi_downloader.tasks;

import android.util.Log;

import com.example.multi_downloader.DB.DBManager;
import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.listeners.DownloadFileListener;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class DownloadFileTask implements Runnable {
    private static final String TAG = "DownloadFileTask";
    private DownloadInfo downloadInfo;
    private DownloadFileListener listener;
    private boolean isPause;

    public DownloadFileTask(DownloadInfo downloadInfo, DownloadFileListener listener) {
        this.downloadInfo = downloadInfo;
        this.listener = listener;
    }

    @Override
    public void run() {
        long lastLoadedSize = downloadInfo.getLoadedSize();
        HttpURLConnection httpConnection = null;
        try {
            URL url = new URL(downloadInfo.getUrl());
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(5000);
            httpConnection.setRequestProperty("Range",
                    "bytes=" + lastLoadedSize + "-" + downloadInfo.getTotalSize());
            int responseCode = httpConnection.getResponseCode();
            InputStream is;
            if (responseCode == HttpURLConnection.HTTP_PARTIAL
                    || responseCode == HttpURLConnection.HTTP_OK) {
                is = httpConnection.getInputStream();
                String apk_path = downloadInfo.getPath();
                File file = new File(apk_path);
                if (!file.exists()) {
                    file.createNewFile();
                }
                RandomAccessFile raf = new RandomAccessFile(file, "rwd");
                Log.i(TAG, "download_url:" + downloadInfo.getUrl());
                raf.seek(lastLoadedSize);
                final byte[] buf = new byte[1024 * 1024 * 10];
                int len;
                long offset = lastLoadedSize;
                long lastUpdateSize = 0L;
                downloadInfo.setStatus(DownloadInfo.LOADING);
                DBManager.getInstance().updateInfo(downloadInfo);
                while ((len = (is.read(buf, 0, buf.length))) != -1 && (downloadInfo.getStatus() != DownloadInfo.PAUSED)) {
                    raf.write(buf, 0, len);
                    offset += len;
                    //更新下载进度
                    downloadInfo.setLoadedSize(offset);
                    if (offset - lastUpdateSize > 1000 * 1000) {
                        lastUpdateSize = offset;
                        listener.onLoading(downloadInfo);
                    }
                    DBManager.getInstance().updateInfo(downloadInfo);
                }
                //通知监听者下载状态
                //更新下载记录到数据库中
                if (downloadInfo.getStatus() == DownloadInfo.PAUSED) {  //暂停
                    listener.onLoadPaused(downloadInfo);
                } else {
                    listener.onLoadFinished(downloadInfo);
                }
                DBManager.getInstance().updateInfo(downloadInfo);
            }
        } catch (Exception e) {
            Log.i(TAG, "下载文件异常:" + e.getMessage());
            listener.onLoadFailed(downloadInfo);
            DBManager.getInstance().updateInfo(downloadInfo);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }
}
