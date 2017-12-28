package com.example.multi_downloader.utils;

import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.NotificationCompat;
import android.widget.RemoteViews;

import com.example.multi_downloader.MultiDownloaderApp;
import com.example.multi_downloader.R;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by jackypeng on 2017/12/28.
 */

public class NotiUtil {
    public static void showNotification(final int id, final String url, final String title, String text) {
        final Context context = MultiDownloaderApp.getContext();
        final NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        builder.setAutoCancel(true);
        builder.setOnlyAlertOnce(true);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.custom_notif);
        remoteViews.setTextViewText(R.id.custom_notification_name, title);
        builder.setContent(remoteViews);

        new AsyncTask<String, Integer, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6000);//设置超时
                    conn.setDoInput(true);
                    conn.setUseCaches(false);//不缓存
                    conn.connect();
                    int code = conn.getResponseCode();
                    Bitmap bitmap = null;
                    if (code == 200) {
                        InputStream is = conn.getInputStream();//获得图片的数据流
                        bitmap = BitmapFactory.decodeStream(is);

                    }
                    return bitmap;
                } catch (Exception e) {
                    remoteViews.setImageViewResource(R.id.custom_notification_icon, R.mipmap.ic_launcher);
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                remoteViews.setImageViewBitmap(R.id.custom_notification_icon, bitmap);
                NotificationManager notificationManager = (NotificationManager) context
                        .getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(id, builder.build());
            }
        }.execute(url);


    }


}
