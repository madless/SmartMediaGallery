package com.example.student.smartmediagallery.service;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.MessageEvent;
import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.net.ProgressFileLoader;
import com.example.student.smartmediagallery.net.ProgressFileLoaderBasedOnUrlConnection;

import java.io.File;
import java.io.IOException;

public class RefactoredDownloaderService extends Service implements ProgressFileLoader.LoaderListener{

    ProgressFileLoaderBasedOnUrlConnection loader;
    private NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    String url;
    String targetPathStr;
    boolean active;
    long totalSize;
    long readSize;
    boolean isPaused;
    DownloaderServiceBinder downloaderServiceBinder;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("mylog", "WOW CREATED!");
        downloaderServiceBinder = new DownloaderServiceBinder();
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        url = intent.getStringExtra(TransferConstant.MEDIA_URL.toString());
        targetPathStr = intent.getStringExtra(TransferConstant.TARGET_PATH.toString());

        if(!active) {
            Log.d("mylog", "new Loader created");
            loader = new ProgressFileLoaderBasedOnUrlConnection(url, targetPathStr, readSize);
            loader.setProgressListener(this);
        } else {
            initDialog();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void initDialog() {
        Log.d("mylog", "initDialog()");
        Intent totalSizeFetchedIntent = new Intent(getString(R.string.action_receiver_download_manager));
        totalSizeFetchedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_INIT.toString());
        totalSizeFetchedIntent.putExtra(TransferConstant.TOTAL_SIZE.toString(), totalSize);
        sendBroadcast(totalSizeFetchedIntent);

//        Intent progressUpdatedIntent = new Intent(getString(R.string.action_receiver_download_manager));
//        progressUpdatedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_IN_PROGRESS.toString());
//        progressUpdatedIntent.putExtra(TransferConstant.READ_SIZE.toString(), readSize);
//        sendBroadcast(progressUpdatedIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return downloaderServiceBinder;
    }

    @Override
    public void onTotalSizeCalculated(long totalSize) {}
    @Override
    public void onTotalSizeFetched(long totalSize) {
        Intent totalSizeFetchedIntent = new Intent(getString(R.string.action_receiver_download_manager));
        totalSizeFetchedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_INIT.toString());
        totalSizeFetchedIntent.putExtra(TransferConstant.TOTAL_SIZE.toString(), totalSize);
        sendBroadcast(totalSizeFetchedIntent);
        Log.d("mylog", "sent broadcast init: " + getString(R.string.action_receiver_download_manager));
        this.totalSize = totalSize;
        notificationBuilder.setContentTitle(url).setSmallIcon(R.drawable.ic_action_download);
    }
    @Override
    public void onProgressUpdated(long totalSize, long readSize) {
        this.readSize = readSize;
        int percentDownloaded = (int)((readSize * 100) / this.totalSize);
        notificationBuilder.setProgress(100, percentDownloaded, false);
        notificationBuilder.setContentText("Downloading: " + readSize + "/" + this.totalSize);
        notificationManager.notify(url.hashCode(), notificationBuilder.build());
        Intent progressUpdatedIntent = new Intent(getString(R.string.action_receiver_download_manager));
        progressUpdatedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_IN_PROGRESS.toString());
        progressUpdatedIntent.putExtra(TransferConstant.READ_SIZE.toString(), readSize);
        sendBroadcast(progressUpdatedIntent);
        Log.d("myupd", "onProgressUpdated");
    }
    @Override
    public void onDownloadComplete() {
        readSize = 0;
        notificationBuilder.setContentText("Downloaded").setProgress(0, 0, false);
        notificationManager.notify(url.hashCode(), notificationBuilder.build());
        Intent downloadCompleteIntent = new Intent(getString(R.string.action_receiver_download_manager));
        downloadCompleteIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_DOWNLOADED.toString());
        sendBroadcast(downloadCompleteIntent);
        stopSelf();
    }

    public class DownloaderServiceBinder extends Binder {
        public RefactoredDownloaderService getService() {
            return RefactoredDownloaderService.this;
        }
    }

    public void startLoading() {
        Log.d("mylog", "startLoading()");
        if(!active) {
            active = true;
            Log.d("mylog", "downloading had started");
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!isPaused) {
                            new File(targetPathStr).delete();
                        }
                        isPaused = false;
                        loader.requestContentLenght();
                        loader.download();
                    } catch (IOException | InterruptedException e) {
                        Log.d("mylog", "ERROR!");
                        e.printStackTrace();
                    } finally {
                        active = false;
                    }
                }
            }).start();
        } else{
            Log.d("mylog", "downloading hadn't started");
        }
    }

    public void pauseLoading() {
        active = false;
        isPaused = true;
        loader.cancel();
    }

    public void stopLoading() {
        loader.cancel();
        readSize = 0;
        active = false;
        stopSelf();
        onProgressUpdated(totalSize, readSize);
        notificationManager.cancel(url.hashCode());
    }

    public boolean isActive() {
        return active;
    }
}
