package com.example.multi_downloader.manager;

import android.util.Log;
import android.util.SparseArray;

import com.example.multi_downloader.DB.DBManager;
import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.events.TaskFinishedEvent;
import com.example.multi_downloader.listeners.DataListener;
import com.example.multi_downloader.listeners.DownloadFileListener;
import com.example.multi_downloader.tasks.TotalDownloadTask;
import com.example.multi_downloader.utils.NotiUtil;
import com.example.multi_downloader.utils.UIUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jackypeng on 2017/12/20.
 * 该对象的生命周期由DownloadService控制
 * 如果DownloadService被杀死,确保该对象持有的引用被正确清理
 */

public class DownloadManager implements DownloadFileListener {
    private static final String TAG = "DownloadManager";
    private static DownloadManager instance;
    private DownloadEngine engine;
    private DownloadConfig config;
    private final List<DownloadInfo> downloadingCaches;    //任务---正在下载
    private final List<DownloadInfo> waitingCaches;    //任务---等待中
    private final SparseArray<TotalDownloadTask> cacheDownloadTask = new SparseArray<>();//key--DownloadInfo的Id

    private DownloadManager(DownloadConfig config) {
        engine = new DownloadEngine(config);
        this.config = config;
        downloadingCaches = new ArrayList<>();
        waitingCaches = new ArrayList<>();
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

    /**
     * 暂停下载
     * 1.缓存池中
     * 2.下载池中
     */

    public void pauseDownload(DownloadInfo downloadInfo) {

        if (!downloadingCaches.contains(downloadInfo)) {
            for (DownloadInfo item : waitingCaches) {
                if (downloadInfo.getId() == item.getId()) {
                    item.setStatus(DownloadInfo.NONE);
                    DataListener listener = downloadInfo.getListener();
                    if (listener != null) {
                        listener.onRefresh();
                    }
                    waitingCaches.remove(item);
                }
            }
        } else {
            TotalDownloadTask task = cacheDownloadTask.get(downloadInfo.getId().intValue());
            if (task != null) {
                task.pause();
                downloadingCaches.remove(downloadInfo);
                cacheDownloadTask.remove(downloadInfo.getId().intValue());
                //通知界面刷新
                downloadInfo.setStatus(DownloadInfo.PAUSED);
                DataListener listener = downloadInfo.getListener();
                if (listener != null) {
                    listener.onRefresh();
                }
            }
            startWaitingTask();
        }
        DBManager.getInstance().updateInfo(downloadInfo);
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
            waitingCaches.add(downloadInfo);
            downloadInfo.setStatus(DownloadInfo.WAITING);
            DataListener listener = downloadInfo.getListener();
            if (listener != null) {
                listener.onRefresh();
            }
        } else {
            Log.i(TAG, "---startDownload---");
            TotalDownloadTask task = new TotalDownloadTask(downloadInfo, engine, this);
            downloadingCaches.add(downloadInfo);
            cacheDownloadTask.put(downloadInfo.getId().intValue(), task);
            downloadInfo.setStatus(DownloadInfo.READY);
            DataListener listener = downloadInfo.getListener();
            if (listener != null) {
                listener.onRefresh();
            }
            task.start();
            DBManager.getInstance().updateInfo(downloadInfo);
        }
    }

    //启动等待中的任务
    private void startWaitingTask() {
        if (waitingCaches.size() > 0) {
            DownloadInfo downloadInfo = waitingCaches.remove(0);
            startDownload(downloadInfo);
        }
    }

    public DownloadInfo getDownloadInfoById(int id) {
        for (DownloadInfo d : downloadingCaches) {
            if (d.getId() == id) {
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

    //重置等待队列的状态
    private void resetWaitingTasks() {
        if (waitingCaches.size() > 0) {
            for (DownloadInfo item : waitingCaches) {
                item.setStatus(DownloadInfo.NONE);
                //更新界面
                DataListener listener = item.getListener();
                if (listener != null) {
                    listener.onRefresh();
                }
            }
        }
    }

    //清除等待池缓存
    private void clearWaitingCaches() {
        if (waitingCaches.size() > 0) {
            waitingCaches.clear();
        }
    }

    //清除下载池缓存
    private void clearDownloadingCaches() {
        if (downloadingCaches.size() > 0) {
            downloadingCaches.clear();
        }
    }

    /**
     * Service被销毁，暂停所有的下载任务
     * 并清理缓存，监听器等等
     */
    public void onDestroy() {
        /**
         * 关闭下载池中的所有正在下载的任务
         * 注意顺序
         * 1.关闭等待的任务
         * 2.关闭正在下载的任务
         */
        resetWaitingTasks();
        clearWaitingCaches();
        pauseAllDownloadTasks();
        clearDownloadingCaches();
    }

    @Override
    public void onLoading(final DownloadInfo info) {
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
                NotiUtil.showNotification(info.getId().intValue(),info.getIcon(),info.getName(),info.getPath());
                //通知栏显示
                EventBus.getDefault().post(new TaskFinishedEvent(info));
                //更新界面
                info.setStatus(DownloadInfo.FINISHED);
                //更新界面
                DataListener listener = info.getListener();
                if (listener != null) {
                    listener.onRefresh();
                }
                downloadingCaches.remove(info);
                cacheDownloadTask.remove(info.getId().intValue());
                startWaitingTask();
                DBManager.getInstance().updateInfo(info);
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

    /**
     * 下载失败
     * 1.网络错误
     * 2.进程被杀死
     */
    @Override
    public void onLoadFailed(DownloadInfo info) {
        //将缓存池中的任务设为初始状态
        resetWaitingTasks();
        //暂停正在下载的任务
        info.setStatus(DownloadInfo.PAUSED);
        //更新界面
        DataListener listener = info.getListener();
        if (listener != null) {
            listener.onRefresh();
        }
    }
}
