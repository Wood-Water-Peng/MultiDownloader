package com.example.jackypeng.multidownloader.simple;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.jackypeng.multidownloader.R;
import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.constants.DownloadStatusConstants;
import com.example.multi_downloader.utils.FileUtil;
import com.example.multi_downloader.utils.PackageUtil;
import com.example.multi_downloader.views.DownloadButton;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jackypeng on 2017/12/26.
 */

public class FinishedTaskRecycleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    //传进来的数据已经是FINISH状态
    private List<DownloadInfo> downloadInfos = new ArrayList<>();

    public FinishedTaskRecycleAdapter(Context context) {
        this.mContext = context;
    }

    public void setData(List<DownloadInfo> data) {
        this.downloadInfos = data;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FinishedTaskRecycleAdapter.ItemHolder(LayoutInflater.from(parent.getContext()).inflate(
                R.layout.finished_item, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof FinishedTaskRecycleAdapter.ItemHolder)) {
            return;
        }
        final DownloadInfo downloadInfo = downloadInfos.get(position);
        ItemHolder itemHolder = (ItemHolder) holder;
        itemHolder.initUI(downloadInfo);
    }

    @Override
    public int getItemCount() {
        return downloadInfos.size();
    }

    private class ItemHolder extends RecyclerView.ViewHolder {

        private final ImageView iv_icon;
        private final TextView tv_size;
        private final TextView tv_name;
        private final DownloadButton bt_action;

        ItemHolder(View view) {
            super(view);
            iv_icon = (ImageView) view.findViewById(R.id.finished_item_iv_icon);
            tv_size = (TextView) view.findViewById(R.id.finished_item_tv_size);
            tv_name = (TextView) view.findViewById(R.id.finished_item_tv_name);
            bt_action = (DownloadButton) view.findViewById(R.id.finished_item_bt_action);
        }


        void initUI(final DownloadInfo downloadInfo) {
            tv_name.setText(downloadInfo.getName());
            tv_size.setText(FileUtil.formatFileSize(downloadInfo.getTotalSize()));
            bt_action.setStatus(DownloadStatusConstants.FINISHED);
            Glide.with(mContext).load(downloadInfo.getIcon()).into(iv_icon);
            bt_action.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //安装apk
                    PackageUtil.installApk(mContext, downloadInfo.getPath());
                }
            });
        }
    }
}
