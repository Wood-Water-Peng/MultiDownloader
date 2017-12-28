package com.example.jackypeng.multidownloader.simple;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.jackypeng.multidownloader.R;
import com.example.multi_downloader.DB.DBManager;
import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.events.TaskFinishedEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by jackypeng on 2017/12/26.
 */

public class FinishedFragment extends Fragment {

    private static final String TAG = "FinishedFragment";
    private List<DownloadInfo> downloadInfos = new ArrayList<>();
    private Unbinder binder;
    @BindView(R.id.fragment_finished_recycle_view)
    RecyclerView recyclerView;
    private FinishedTaskRecycleAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_finished_task, container, false);
        binder = ButterKnife.bind(this, rootView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FinishedTaskRecycleAdapter(getContext());
        recyclerView.setAdapter(adapter);
        initData();
        return rootView;
    }

    private void initData() {
        downloadInfos = DBManager.getInstance().getFinishedLoadingInfos();
        adapter.setData(downloadInfos);
        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onTaskFinishedEvent(TaskFinishedEvent event) {
        Log.i(TAG, "DownloadInfo:" + event.getDownloadInfo().getName());
        //刷新界面
        downloadInfos.add(event.getDownloadInfo());
        adapter.setData(downloadInfos);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        binder.unbind();
    }


}
