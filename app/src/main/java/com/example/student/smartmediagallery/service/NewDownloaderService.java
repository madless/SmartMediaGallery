package com.example.student.smartmediagallery.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.MessageEvent;
import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.net.DownloadManager;
import com.example.student.smartmediagallery.net.ProgressFileLoader;
import com.example.student.smartmediagallery.net.ProgressFileLoaderBasedOnUrlConnection;

import java.io.IOException;

public class NewDownloaderService extends Service implements ProgressFileLoader.LoaderListener {

    public final static String BROADCAST_ACTION_CANCEL_DOWNLOADING = "gfl.madless.smart_media_galery.cancel_downloading_receive";

    ProgressFileLoaderBasedOnUrlConnection loader;
    boolean isCanceled;
    boolean isPaused;
    private NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    int startId;
    long totalSize;
    DownloaderServiceReceiver downloaderServiceReceiver;
    String url;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("mylog", "onStartCommand");
        this.startId = startId;

        url = intent.getStringExtra(TransferConstant.MEDIA_URL.toString());
        String targetPathStr = intent.getStringExtra(TransferConstant.TARGET_PATH.toString());
        long bytesRead = intent.getLongExtra(TransferConstant.READ_SIZE.toString(), -1);

        loader = new ProgressFileLoaderBasedOnUrlConnection(url, targetPathStr, bytesRead);
        loader.setProgressListener(this);

        notificationBuilder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        IntentFilter cancelDownloadingIntentFilter = new IntentFilter(BROADCAST_ACTION_CANCEL_DOWNLOADING);
        downloaderServiceReceiver = new DownloaderServiceReceiver();
        registerReceiver(downloaderServiceReceiver, cancelDownloadingIntentFilter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    loader.requestContentLenght();
                    loader.download();
                } catch (IOException | InterruptedException e) {
                    Log.d("mylog", "ERROR!");
                    e.printStackTrace();
                }
            }
        }).start();

        Log.d("mylog", "stop");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTotalSizeCalculated(long totalSize) {}

    @Override
    public void onTotalSizeFetched(long totalSize) {
        Intent totalSizeFetchedIntent = new Intent(DownloadManager.BROADCAST_ACTION_DOWNLOAD);
        totalSizeFetchedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_INIT.toString());
        totalSizeFetchedIntent.putExtra(TransferConstant.TOTAL_SIZE.toString(), totalSize);
        sendBroadcast(totalSizeFetchedIntent);
        this.totalSize = totalSize;
        notificationBuilder.setContentTitle(url)
                .setSmallIcon(R.drawable.ic_action_download);
    }

    @Override
    public void onProgressUpdated(long totalSize, long readSize) {
        int percentDownloaded = (int)((readSize * 100) / totalSize);
        notificationBuilder.setProgress(100, percentDownloaded, false);
        notificationBuilder.setContentText("Downloading: " + readSize + "/" + totalSize);
        notificationManager.notify(startId, notificationBuilder.build());

        Intent progressUpdatedIntent = new Intent(DownloadManager.BROADCAST_ACTION_DOWNLOAD);
        progressUpdatedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_IN_PROGRESS.toString());
        progressUpdatedIntent.putExtra(TransferConstant.READ_SIZE.toString(), readSize);
        sendBroadcast(progressUpdatedIntent);
    }

    @Override
    public void onDownloadComplete() {
        notificationBuilder.setContentText("Downloaded").setProgress(0, 0, false);
        notificationManager.notify(startId, notificationBuilder.build());
        Intent downloadCompleteIntent = new Intent(DownloadManager.BROADCAST_ACTION_DOWNLOAD);
        downloadCompleteIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_DOWNLOADED.toString());
        sendBroadcast(downloadCompleteIntent);
        stopSelf();
    }

    public class DownloaderServiceReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("mylog", "Received!");
            loader.cancel();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(downloaderServiceReceiver);
    }
}
