package com.example.multi_downloader.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.Button;

import com.example.multi_downloader.R;

/**
 * Created by jackypeng on 2018/3/1.
 */

public class DownloadButton extends Button {

    public static final int INIT = 1;
    public static final int DOWNLOADING = 2;
    public static final int PREPARING_DOWNLOAD = 3;
    public static final int WAITING = 4;
    public static final int PAUSED = 5;
    public static final int FINISHED = 6;
    private Context mContext;

    public DownloadButton(Context context) {
        this(context,null);
    }

    public DownloadButton(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DownloadButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext=context;
    }

    public void setStatus(int status) {
        switch (status) {
            case INIT:
                setText("未下载");
                setBackground(mContext.getResources().getDrawable(R.drawable.btn_download_bg));
                break;
            case PREPARING_DOWNLOAD:
                setText("准备下载中...");
                setBackground(mContext.getResources().getDrawable(R.drawable.btn_download_bg));
                break;
            case WAITING:
                setText("等待中...");
                setBackground(mContext.getResources().getDrawable(R.drawable.btn_download_bg));
                break;
            case DOWNLOADING:
                setText("暂停");
                setBackground(mContext.getResources().getDrawable(R.drawable.btn_download_downloading_bg));
                break;
            case PAUSED:
                setText("继续");
                setBackground(mContext.getResources().getDrawable(R.drawable.btn_download_paused_bg));
                break;
            case FINISHED:
                setText("安装");
                setBackground(mContext.getResources().getDrawable(R.drawable.btn_download_finished_bg));
                break;
        }
    }
}
