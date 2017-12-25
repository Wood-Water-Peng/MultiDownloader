package com.example.jackypeng.multidownloader.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class DownloadEngine {
    private ExecutorService service;

    public DownloadEngine() {
        service = Executors.newFixedThreadPool(5);
    }

    public void submit(Runnable task) {
        service.submit(task);
    }
}
