package com.example.jackypeng.multidownloader.bean;

import com.example.jackypeng.multidownloader.listeners.DataListener;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by jackypeng on 2017/12/20.
 */
@Entity
public class DownloadInfo {
    public static final int LOADING = 1;
    public static final int PAUSED = 2;
    public static final int ERROR = 3;
    public static final int FINISHED = 4;
    public static final int INIT = 5;

    @Id(autoincrement = true)
    private Long key;
    @Unique
    private Long id;
    private int status;
    private String url;
    private String path;
    private long loadedSize;
    private long totalSize;
    @Transient
    private DataListener listener;

    @Generated(hash = 173698838)
    public DownloadInfo(Long key, Long id, int status, String url, String path,
            long loadedSize, long totalSize) {
        this.key = key;
        this.id = id;
        this.status = status;
        this.url = url;
        this.path = path;
        this.loadedSize = loadedSize;
        this.totalSize = totalSize;
    }

    @Generated(hash = 327086747)
    public DownloadInfo() {
    }

    public DataListener getListener() {
        return listener;
    }

    public void setListener(DataListener listener) {
        this.listener = listener;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getLoadedSize() {
        return this.loadedSize;
    }

    public void setLoadedSize(long loadedSize) {
        this.loadedSize = loadedSize;
    }

    public long getTotalSize() {
        return this.totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public Long getKey() {
        return this.key;
    }

    public void setKey(Long key) {
        this.key = key;
    }
}
