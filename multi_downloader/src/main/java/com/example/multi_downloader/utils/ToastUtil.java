package com.example.multi_downloader.utils;

import android.view.Gravity;
import android.widget.Toast;

import com.example.multi_downloader.MultiDownloaderApp;

/**
 * 自定义toast
 *
 * @author Administrator
 */
public class ToastUtil {

    public static void toast(String text) {
        Toast.makeText(MultiDownloaderApp.getContext(), text, Toast.LENGTH_SHORT).show();
    }

    public static void toastLong(String text) {
        Toast.makeText(MultiDownloaderApp.getContext(), text, Toast.LENGTH_LONG).show();
    }

}
