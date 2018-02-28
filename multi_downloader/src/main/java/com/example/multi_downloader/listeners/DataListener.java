package com.example.multi_downloader.listeners;

/**
 * Created by jackypeng on 2017/12/20.
 */

public interface DataListener {

    void onInit();

    void onPrepare();

    void onWaiting();

    void onLoading();

    void onPaused();

    void onSuccess();

    void onFetchFileInfoError();  //获取要下载的文件失败

    void onFailed();
}
