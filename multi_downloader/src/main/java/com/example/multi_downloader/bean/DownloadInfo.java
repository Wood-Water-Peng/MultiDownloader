package com.example.multi_downloader.bean;

import com.example.multi_downloader.listeners.DataListener;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;
import org.greenrobot.greendao.annotation.Unique;

/**
 * Created by jackypeng on 2017/12/20.
 */
@Entity
public class DownloadInfo {

    @Id(autoincrement = true)
    private Long key;
    @Unique
    private Long id;
    private int status;       //当前状态
    private String url;
    private String icon;
    private String name;
    private String path;
    private long loadedSize;
    private long totalSize;

    @Transient
    private DataListener listener;
    @Transient
    private int preStatus;    //上一个状态

    public DownloadInfo() {
    }

    @Generated(hash = 595438495)
    public DownloadInfo(Long key, Long id, int status, String url, String icon, String name, String path, long loadedSize, long totalSize) {
        this.key = key;
        this.id = id;
        this.status = status;
        this.url = url;
        this.icon = icon;
        this.name = name;
        this.path = path;
        this.loadedSize = loadedSize;
        this.totalSize = totalSize;
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

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPreStatus() {
        return preStatus;
    }

    public void setPreStatus(int preStatus) {
        this.preStatus = preStatus;
    }
}
