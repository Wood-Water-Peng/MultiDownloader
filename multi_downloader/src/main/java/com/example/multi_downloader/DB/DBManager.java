package com.example.multi_downloader.DB;

import android.database.sqlite.SQLiteDatabase;

import com.example.multi_downloader.MultiDownloaderApp;
import com.example.multi_downloader.bean.DaoMaster;
import com.example.multi_downloader.bean.DaoSession;
import com.example.multi_downloader.bean.DownloadInfo;
import com.example.multi_downloader.bean.DownloadInfoDao;

import java.util.List;

/**
 * Created by jackypeng on 2017/12/20.
 *
 * 数据库中只保留三种状态
 * 1.未下载
 * 2.暂停下载
 * 3.下载完成
 */

public class DBManager {
    private static DBManager manager;
    private DaoMaster.DevOpenHelper helper;
    private SQLiteDatabase db;
    private DaoMaster daoMaster;
    private DaoSession daoSession;

    private DBManager() {
        helper = new DaoMaster.DevOpenHelper(MultiDownloaderApp.getContext(),MultiDownloaderApp.getContext().getExternalCacheDir()+"/db/download-db.db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        this.daoSession = daoSession;
    }

    public static DBManager getInstance() {
        synchronized (DBManager.class) {
            if (manager == null) {
                synchronized (DBManager.class) {
                    manager = new DBManager();
                }
            }
        }
        return manager;
    }


    public void insertInfo(DownloadInfo downloadInfo) {
        daoSession.getDownloadInfoDao().insertOrReplace(downloadInfo);
    }

    public void updateInfo(DownloadInfo downloadInfo) {
        daoSession.getDownloadInfoDao().insertOrReplace(downloadInfo);
    }

    public void deleteInfo(DownloadInfo downloadInfo) {
        daoSession.getDownloadInfoDao().delete(downloadInfo);
    }

    public DownloadInfo getInfoById(int id) {
        List<DownloadInfo> list = daoSession.getDownloadInfoDao().queryBuilder().where(DownloadInfoDao.Properties.Id.eq(id)).list();
        if (list == null || list.size() == 0) {
            return null;
        }
        return list.get(0);
    }

    public List<DownloadInfo> getLoadingInfos() {
        return daoSession.getDownloadInfoDao().queryBuilder().where(DownloadInfoDao.Properties.Status.eq(DownloadInfo.LOADING)).list();
    }

    public List<DownloadInfo> getFinishedLoadingInfos() {
        return daoSession.getDownloadInfoDao().queryBuilder().where(DownloadInfoDao.Properties.Status.eq(DownloadInfo.FINISHED)).list();
    }

    public List<DownloadInfo> getAllInfos() {
        return daoSession.getDownloadInfoDao().loadAll();
    }

}
