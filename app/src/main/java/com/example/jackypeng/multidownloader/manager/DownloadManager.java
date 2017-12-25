package com.example.jackypeng.multidownloader.manager;

import android.util.Log;

import com.example.jackypeng.multidownloader.bean.DownloadInfo;
import com.example.jackypeng.multidownloader.db.DBManager;
import com.example.jackypeng.multidownloader.listeners.DataListener;
import com.example.jackypeng.multidownloader.task.TotalDownloadTask;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class DownloadManager implements TotalDownloadTask.DownloadSuccessListener {
    private static final String TAG = "DownloadManager";
    private static DownloadManager instance;
    private DownloadEngine engine;
    private DownloadConfig config;
    private final List<DownloadInfo> downloadingCaches;    //任务---正在下载
    private final List<DownloadInfo> waitingCaches;    //任务---等待中
    private final ConcurrentHashMap<Long, TotalDownloadTask> cacheDownloadTask;   //key--DownloadInfo的Id

    private DownloadManager(DownloadConfig config) {
        engine = new DownloadEngine();
        this.config = config;
        downloadingCaches = new ArrayList<>();
        waitingCaches = new ArrayList<>();
        cacheDownloadTask = new ConcurrentHashMap<>();
    }

    public static DownloadManager getInstance(DownloadConfig config) {
        synchronized (DownloadManager.class) {
            if (instance == null) {
                synchronized (DownloadManager.class) {
                    instance = new DownloadManager(config);
                }
            }
        }
        return instance;
    }

    //暂停下载
    public void pauseDownload(DownloadInfo downloadInfo) {
        TotalDownloadTask task = cacheDownloadTask.get(downloadInfo.getId());
        if (task != null) {
            task.pause();
            downloadingCaches.remove(downloadInfo);
            cacheDownloadTask.remove(downloadInfo.getId());
            //通知界面刷新
        }
        startWaitingTask();
    }

    //恢复下载
    public void resumeDownload(DownloadInfo downloadInfo) {
        startDownload(downloadInfo);
    }

    //删除下载
    public void deleteDownload(DownloadInfo downloadInfo) {

    }

    //开启新的下载任务
    public void startDownload(DownloadInfo downloadInfo) {
        if (downloadingCaches.size() >= config.getMaxThreads()) {
            Log.i(TAG, "---waiting---");
            downloadInfo.setStatus(DownloadInfo.INIT);
            waitingCaches.add(downloadInfo);
            DataListener listener = downloadInfo.getListener();
            if (listener != null) {
                listener.onRefresh();
            }
        } else {
            Log.i(TAG, "---startDownload---");
            TotalDownloadTask task = new TotalDownloadTask(downloadInfo, engine, this);
            downloadingCaches.add(downloadInfo);
            cacheDownloadTask.put(downloadInfo.getId(), task);
            task.start();
            DBManager.getInstance().updateInfo(downloadInfo);
        }
    }

    private void startWaitingTask() {
        if (waitingCaches.size() != 0) {
            DownloadInfo downloadInfo = waitingCaches.remove(0);
            startDownload(downloadInfo);
        }
    }

    public DownloadInfo getDownloadInfoById(int id) {
        for (DownloadInfo d : downloadingCaches) {
            if (d.getId() == id) {
                Log.i(TAG, "id:" + id);
                return d;
            }
        }
        return DBManager.getInstance().getInfoById(id);
    }

    /**
     * 暂停下载池中的所有任务
     */
    public void pauseAllDownloadTasks() {
        for (DownloadInfo downloadInfo : downloadingCaches) {
            pauseDownload(downloadInfo);
        }
    }

    @Override
    public void onSuccess(DownloadInfo downloadInfo) {
        Log.i(TAG, "---onSuccess---");
        downloadingCaches.remove(downloadInfo);
        cacheDownloadTask.remove(downloadInfo.getId());
        startWaitingTask();
    }
}
