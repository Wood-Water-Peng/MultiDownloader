package com.example.multi_downloader.manager;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by jackypeng on 2017/12/20.
 */

public class DownloadEngine {
    private ExecutorService service;

    public DownloadEngine(DownloadConfig config) {

        service = Executors.newFixedThreadPool(config.getMaxThreads());
    }

    public void submit(Runnable task) {
        service.submit(task);
    }
}
