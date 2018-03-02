package com.example.jackypeng.multidownloader.simple;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jackypeng.multidownloader.R;
import com.example.jackypeng.multidownloader.bean.MyBusinessInfo;
import com.example.multi_downloader.DB.DBManager;
import com.example.multi_downloader.MultiDownloaderApp;
import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.constants.DownloadStatusConstants;
import com.example.multi_downloader.events.TaskFinishedEvent;
import com.example.multi_downloader.listeners.DataListener;
import com.example.multi_downloader.services.DownloadService;
import com.example.multi_downloader.utils.FileUtil;
import com.example.multi_downloader.utils.GeneralPositiveAndNegativeDialog;
import com.example.multi_downloader.utils.NetUtil;
import com.example.multi_downloader.utils.NotiUtil;
import com.example.multi_downloader.utils.ToastUtil;
import com.example.multi_downloader.views.DownloadButton;

import org.greenrobot.eventbus.EventBus;

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
        Log.i(TAG, "---onCreate---");
        this.myBusinessInfos = myBusinessInfos;
        this.mContext = mContext;
        MultiDownloaderApp.getContext().bindService(new Intent(mContext, DownloadService.class), new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                DownloadService.LocalBinder binder = (DownloadService.LocalBinder) service;
                downloadService = binder.getService();
                notifyDataSetChanged();
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
        //根据downloadInfo,确定各个控件的状态
        if (downloadService != null) {
            itemHolder.initData(businessInfo);
        }
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
        private final DownloadButton bt_action;
        private DownloadInfo downloadInfo;

        ItemHolder(View view) {
            super(view);
            iv_icon = (ImageView) view.findViewById(R.id.iv_icon);
            tv_size = (TextView) view.findViewById(R.id.tv_size);
            tv_status = (TextView) view.findViewById(R.id.tv_status);
            pb = (ProgressBar) view.findViewById(R.id.pb);
            tv_name = (TextView) view.findViewById(R.id.tv_name);
            bt_action = (DownloadButton) view.findViewById(R.id.bt_action);
        }

        void setVisibility(boolean isVisible) {
            RecyclerView.LayoutParams param = (RecyclerView.LayoutParams) itemView.getLayoutParams();
            if (isVisible) {
                param.height = RelativeLayout.LayoutParams.WRAP_CONTENT;// 这里注意使用自己布局的根布局类型
                param.width = RelativeLayout.LayoutParams.MATCH_PARENT;// 这里注意使用自己布局的根布局类型
                itemView.setVisibility(View.VISIBLE);
            } else {
                itemView.setVisibility(View.GONE);
                param.height = 0;
                param.width = 0;
            }
            itemView.setLayoutParams(param);
        }

        /**
         * 界面需要通过数据来更新
         * 1.数据的来源   ---由DownloadManager统一管理
         * <p>
         * sql中不保存状态，只保存数据(已下载/总大小)
         * <p>
         * 在初始化界面时候，根据这两个数值更新状态
         */
        void initData(final MyBusinessInfo businessInfo) {
            this.downloadInfo = downloadService.getDownloadManager().getDownloadInfoById(businessInfo.getUrl().hashCode());
            /**
             * 此时，立刻监听下载的记录
             */
            if (downloadInfo == null) {  //如果没有下载记录，则创建一条新的记录
                Log.i(TAG, "---创建新的下载记录---");
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
                downloadInfo.setStatus(DownloadStatusConstants.INIT);
                downloadInfo.setId((long) businessInfo.getUrl().hashCode());
            } else {
                Log.i(TAG, "---从下载池中获取下载记录---");
            }
            //初始化界面数据
            initUI(downloadInfo, downloadInfo.getLoadedSize(), downloadInfo.getTotalSize());
            //设置监听器
            downloadInfo.setListener(new DataListener() {

                @Override
                public void onInit() {
                    downloadInfo.setStatus(DownloadStatusConstants.INIT);
                    bt_action.setStatus(DownloadStatusConstants.INIT);
                    tv_status.setText(mContext.getResources().getString(R.string.tv_init));
                }

                @Override
                public void onPrepare() {
                    downloadInfo.setStatus(DownloadStatusConstants.PREPARING);
                    bt_action.setStatus(DownloadStatusConstants.PREPARING);
                    tv_status.setText(mContext.getResources().getString(R.string.tv_preparing));
                }

                @Override
                public void onWaiting() {
                    downloadInfo.setStatus(DownloadStatusConstants.WAITING);
                    bt_action.setStatus(DownloadStatusConstants.WAITING);
                    tv_status.setText(mContext.getResources().getString(R.string.tv_waiting));
                }

                @Override
                public void onLoading() {
                    Log.i(TAG, "---onLoading---" + downloadInfo.getName());
                    downloadInfo.setStatus(DownloadStatusConstants.DOWNLOADING);
                    bt_action.setStatus(DownloadStatusConstants.DOWNLOADING);
                    tv_status.setText(mContext.getResources().getString(R.string.tv_downloading));
                    pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                    tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getTotalSize()));
                }

                @Override
                public void onPaused() {
                    downloadInfo.setStatus(DownloadStatusConstants.PAUSED);
                    bt_action.setStatus(DownloadStatusConstants.PAUSED);
                    tv_status.setText(mContext.getResources().getString(R.string.tv_paused));
                    pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                    tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getTotalSize()));
                }

                @Override
                public void onSuccess() {
                    NotiUtil.showNotification(downloadInfo.getId().intValue(), downloadInfo.getIcon(), downloadInfo.getName(), downloadInfo.getPath());
                    //通知栏显示
                    EventBus.getDefault().post(new TaskFinishedEvent(downloadInfo));
                    downloadInfo.setStatus(DownloadStatusConstants.FINISHED);
                    bt_action.setStatus(DownloadStatusConstants.FINISHED);
                    tv_status.setText(mContext.getResources().getString(R.string.tv_finished));
                    pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                    tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getTotalSize()));
                    DBManager.getInstance().updateInfo(downloadInfo);
                    setVisibility(false);
                }

                @Override
                public void onFetchFileInfoError() {
                    downloadInfo.setStatus(DownloadStatusConstants.INIT);
                    bt_action.setStatus(DownloadStatusConstants.INIT);
                    tv_status.setText(mContext.getResources().getString(R.string.tv_init));
                    ToastUtil.toast("获取文件信息失败");
                }

                @Override
                public void onFailed() {
                    Log.i(TAG, downloadInfo.getName() + "  failed");
                    downloadInfo.setStatus(DownloadStatusConstants.PAUSED);
                    bt_action.setStatus(DownloadStatusConstants.PAUSED);
                    tv_status.setText(mContext.getResources().getString(R.string.tv_paused));
                    pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                    tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                            .formatFileSize(downloadInfo.getTotalSize()));
                }
            });
            //设置点击事件
            bt_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /**
                     * 检测用户的网络状态
                     * 1.wifi      直接下载
                     * 2.2/3/4G    提示用户
                     */
                    if (!NetUtil.isNetworkOnline()) {
                        ToastUtil.toast("网络未连接");
//                        return;
                    }

                    if (ConnectivityManager.TYPE_WIFI != NetUtil.getConnectedType()) {
                        //连接非wifi信号，弹出dialog提示
                        final GeneralPositiveAndNegativeDialog dialog = new GeneralPositiveAndNegativeDialog(mContext);
                        dialog.setTitle("网络未连接");
                        dialog.show();
                        dialog.setPositiveAndNegativeListener(new GeneralPositiveAndNegativeDialog.OnPositiveAndNegativeListener() {

                            @Override
                            public void onPositiveButtonClicked() {
                                dialog.dismiss();
                            }

                            @Override
                            public void onNegativeButtonClicked() {
                                dialog.dismiss();
                            }
                        });
                        return;
                    }
                    /**
                     * 防止用户多次点击
                     */
                    //根据downloadInfo确定要执行的任务
                    switch (downloadInfo.getStatus()) {
                        case DownloadStatusConstants.INIT:
                            downloadService.download(downloadInfo);
                            break;
                        case DownloadStatusConstants.WAITING:
//                            将任务从等待队列中移除，并还原到以前的状态
//                            downloadService.pause(downloadInfo);
                            downloadService.removeTaskFromWaitingPool(downloadInfo);
                            break;
                        case DownloadStatusConstants.PREPARING:
                            //正在准备下载，不响应用户点击
                            ToastUtil.toast("正在准备下载，请稍后点击");
                            break;
                        case DownloadStatusConstants.PAUSED:
                        case DownloadStatusConstants.ERROR:
                            downloadService.resume(downloadInfo);
                            break;
                        case DownloadStatusConstants.DOWNLOADING:
                            downloadService.pause(downloadInfo);
                            break;
                        case DownloadStatusConstants.FINISHED:
                            break;
                    }
                }
            });
        }

        /**
         * 根据数据库中的数据，初始化，并给每一个downloadInfo赋予一个状态
         */
        private void initUI(DownloadInfo downloadInfo, long loadedSize, long totalSize) {

            Glide.with(mContext).load(downloadInfo.getIcon()).into(iv_icon);
            tv_name.setText(downloadInfo.getName());
            Log.i(TAG, "initUI---:" + downloadInfo.getStatus());
            if (loadedSize == 0) {//未下载
                downloadInfo.setStatus(DownloadStatusConstants.INIT);
                bt_action.setStatus(DownloadStatusConstants.INIT);
                pb.setProgress(0);
                tv_status.setText(mContext.getResources().getString(R.string.tv_init));
            } else if (loadedSize > 0 && loadedSize < totalSize && (downloadInfo.getStatus() != DownloadStatusConstants.DOWNLOADING)) {   //暂停中
                downloadInfo.setStatus(DownloadStatusConstants.PAUSED);
                bt_action.setStatus(DownloadStatusConstants.PAUSED);
                tv_status.setText(mContext.getResources().getString(R.string.tv_paused));
                pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                        .formatFileSize(downloadInfo.getTotalSize()));
            } else if (loadedSize == totalSize) {   //下载完成
                setVisibility(false);
                downloadInfo.setStatus(DownloadStatusConstants.FINISHED);
                bt_action.setStatus(DownloadStatusConstants.FINISHED);
                tv_status.setText(mContext.getResources().getString(R.string.tv_finished));
                pb.setProgress((int) (downloadInfo.getLoadedSize() * 100.0 / downloadInfo.getTotalSize()));
                tv_size.setText(FileUtil.formatFileSize(downloadInfo.getLoadedSize()) + "/" + FileUtil
                        .formatFileSize(downloadInfo.getTotalSize()));
            }

        }
    }


}
