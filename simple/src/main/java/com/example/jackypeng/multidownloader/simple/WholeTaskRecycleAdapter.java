package com.example.jackypeng.multidownloader.simple;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jackypeng.multidownloader.R;
import com.example.jackypeng.multidownloader.bean.MyBusinessInfo;
import com.example.multi_downloader.MultiDownloaderApp;
import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.listeners.DataListener;
import com.example.multi_downloader.services.DownloadService;
import com.example.multi_downloader.utils.FileUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class WholeTaskRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "WholeTaskRecycleAdapter";
    /**
     * 适配器中返回的界面是根据每一项对应的DownloadInfo改变而变化的
     * <p>
     * 适配器中的数据由Service提供，而Service操作DownloadManager下载数据
     * 这样即使旧的Adapter被回收掉，Service可以确保下载继续执行，并提供数据
     */
    private List<MyBusinessInfo> myBusinessInfos = new ArrayList<>();
    private Context mContext;
    private DownloadService downloadService;

    WholeTaskRecycleAdapter(List<MyBusinessInfo> myBusinessInfos, Context mContext) {
        this.myBusinessInfos = myBusinessInfos;
        this.mContext = mContext;
        mContext.startService(new Intent(mContext, DownloadService.class));
        mContext.bindService(new Intent(mContext, DownloadService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
                downloadService = binder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                downloadService.unbindService(this);
            }
        }, Context.BIND_AUTO_CREATE);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ItemHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.download_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof ItemHolder)) {
            return;
        }
        MyBusinessInfo businessInfo = myBusinessInfos.get(position);
        ItemHolder itemHolder = (ItemHolder) holder;
        ((ItemHolder) holder).tv_name.setText(businessInfo.getName());
        Glide.with(mContext).load(businessInfo.getIcon()).into(itemHolder.iv_icon);
        //根据downloadInfo,确定各个控件的状态
        itemHolder.initData(businessInfo);
        itemHolder.refresh();
    }

    @Override
    public int getItemCount() {
        return myBusinessInfos.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_icon;
        private final TextView tv_size;
        private final TextView tv_status;
        private final ProgressBar pb;
        private final TextView tv_name;
        private final Button bt_action;
        private DownloadInfo downloadInfo;

        ItemHolder(View view) {
            super(view);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            pb = (ProgressBar) view.findViewById(R.id.pb);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            bt_action = (Button) view.findViewById(R.id.bt_action);
        }

        /**
         * 界面需要通过数据来更新
         * 1.数据的来源   ---由DownloadManager统一管理
         */
        void initData(final MyBusinessInfo businessInfo) {
            this.downloadInfo = DownloadService.getDownloadManager().getDownloadInfoById(businessInfo.getUrl().hashCode());

            /**
             * 此时，立刻监听下载的记录
             */
            if (downloadInfo != null) {
                downloadInfo.setListener(new DataListener() {
                    @Override
                    public void onRefresh() {
                        refresh();
                    }

                    @Override
                    public void onFailed() {

                    }
                });
            }
            bt_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 检测用户的网络状态
                     * 1.wifi      直接下载
                     * 2.2/3/4G    提示用户
                     */
                    //如果没有下载记录，则创建一条新的记录
                    if (downloadInfo == null) {
                        File d = new File(MultiDownloaderApp.getContext().getExternalCacheDir().getAbsolutePath(), "download");
                        if (!d.exists()) {
                            d.mkdirs();
                        }
                        String path = d.getAbsolutePath().concat("/").concat(businessInfo.getName());
                        downloadInfo = new DownloadInfo();
                        downloadInfo.setPath(path);
                        downloadInfo.setUrl(businessInfo.getUrl());
                        downloadInfo.setName(businessInfo.getName());
                        downloadInfo.setIcon(businessInfo.getIcon());
                        downloadInfo.setStatus(DownloadInfo.NONE);
                        downloadInfo.setId((long) businessInfo.getUrl().hashCode());
                    }
                    /**
                     * 防止用户多次点击
                     */
                    downloadInfo.setListener(new DataListener() {
                        @Override
                        public void onRefresh() {
                            refresh();
                        }

                        @Override
                        public void onFailed() {

                        }
                    });
                    //根据downloadInfo确定要执行的任务
                    switch (downloadInfo.getStatus()) {
                        case DownloadInfo.NONE:
                            downloadService.download(downloadInfo);
                            break;
                        case DownloadInfo.WAITING:
                            downloadService.pause(downloadInfo);
                            break;
                        case DownloadInfo.READY:
                            downloadService.pause(downloadInfo);
                            break;
                        case DownloadInfo.PAUSED:
                        case DownloadInfo.ERROR:
                            downloadService.resume(downloadInfo);
                            break;
                        case DownloadInfo.LOADING:
                            downloadService.pause(downloadInfo);
                            break;
                        case DownloadInfo.FINISHED:
                            break;
                    }
                }
            });
        }

        void refresh() {
            if (downloadInfo == null) {
                tv_size.setText("");
                pb.setProgress(0);
                bt_action.setText("下载");
                tv_status.setText("未下载");
                bt_action.setBackgroundColor(Color.LTGRAY);
                tv_status.setTextColor(Color.LTGRAY);
            } else {
                switch (downloadInfo.getStatus()) {
                    case DownloadInfo.NONE:
                        bt_action.setText("下载");
                        tv_status.setText("未下载");
                        bt_action.setBackgroundColor(Color.LTGRAY);
                        tv_status.setTextColor(Color.LTGRAY);
                        break;
                    case DownloadInfo.WAITING:
                        bt_action.setText("等待中...");
                        tv_status.setText("等待下载中...");
                        bt_action.setBackgroundColor(Color.LTGRAY);
                        tv_status.setTextColor(Color.LTGRAY);
                        break;
                    case DownloadInfo.READY:
                        bt_action.setText("准备中...");
                        tv_status.setText("准备下载中...");
                        bt_action.setBackgroundColor(Color.LTGRAY);
                        tv_status.setTextColor(Color.LTGRAY);
                        break;
                    case DownloadInfo.PAUSED:
                    case DownloadInfo.ERROR:
                        bt_action.setText("继续");
                        tv_status.setText("暂停中");
                        bt_action.setBackgroundColor(Color.LTGRAY);
                        tv_status.setTextColor(Color.LTGRAY);
                        pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                        tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                                .formatFileSize(downloadInfo.getTotalSize()));
                        break;
                    case DownloadInfo.LOADING:
                        bt_action.setText("暂停");
                        tv_status.setText("下载中");
                        bt_action.setBackgroundColor(Color.BLUE);
                        tv_status.setTextColor(Color.BLUE);
                        pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                        tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                                .formatFileSize(downloadInfo.getTotalSize()));
                        break;
                    case DownloadInfo.FINISHED:
                        bt_action.setText("删除");
                        tv_status.setText("下载成功");
                        bt_action.setBackgroundColor(Color.RED);
                        tv_status.setTextColor(Color.RED);
                        pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                        tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                                .formatFileSize(downloadInfo.getTotalSize()));
                        break;
                }
            }
        }
    }
}
