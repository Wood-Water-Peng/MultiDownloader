package com.example.jackypeng.multidownloader.listeners;

/**
 * Created by jackypeng on 2017/12/20.
 */

public interface GetFileSizeListener {
    void onSuccess(long size);
    void onFailed(String msg);
}
