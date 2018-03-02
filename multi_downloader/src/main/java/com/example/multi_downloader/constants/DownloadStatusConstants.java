package com.example.multi_downloader.constants;

/**
 * Created by jackypeng on 2018/3/1.
 * 下载过程中的状态常量
 */

public class DownloadStatusConstants {
    public static final int INIT = 1;
    public static final int PREPARING = 2;   //准备中
    public static final int WAITING = 3;    //等待中
    public static final int ERROR = 4;    //下载出错
    public static final int DOWNLOADING = 5;
    public static final int PAUSED = 6;
    public static final int FINISHED = 7;
}
