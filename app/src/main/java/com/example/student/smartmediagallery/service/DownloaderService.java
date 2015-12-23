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
import com.example.student.smartmediagallery.core.constants.MessageEvent;
import com.example.student.smartmediagallery.core.constants.TransferConstant;
import com.example.student.smartmediagallery.core.model.Downloadable;
import com.example.student.smartmediagallery.net.ProgressFileLoader;
import com.example.student.smartmediagallery.net.ProgressFileLoaderBasedOnUrlConnection;

import java.io.IOException;

public class DownloaderService extends Service implements ProgressFileLoader.LoaderListener{

    ProgressFileLoaderBasedOnUrlConnection loader;
    private NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    boolean active;
    long totalSize;
    long readSize;
    boolean isPaused;
    DownloaderServiceBinder downloaderServiceBinder;
    Downloadable downloadable;

    @Override
    public void onCreate() {
        super.onCreate();
        downloaderServiceBinder = new DownloaderServiceBinder();
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(!active && intent != null) {
            downloadable = intent.getParcelableExtra(TransferConstant.CURRENT_MEDIA.toString());
            loader = new ProgressFileLoaderBasedOnUrlConnection(downloadable.getUrl(), downloadable.getTargetPath().getAbsolutePath(), readSize);
            loader.setProgressListener(this);
        } else {
            initDialog();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void initDialog() {
        Intent totalSizeFetchedIntent = new Intent(getString(R.string.action_receiver_download_manager));
        totalSizeFetchedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_INIT.toString());
        totalSizeFetchedIntent.putExtra(TransferConstant.TOTAL_SIZE.toString(), totalSize);
        sendBroadcast(totalSizeFetchedIntent);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return downloaderServiceBinder;
    }

    @Override
    public void onTotalSizeCalculated(String url, long totalSize) {}
    @Override
    public void onTotalSizeFetched(String url, long totalSize) {
        Intent totalSizeFetchedIntent = new Intent(getString(R.string.action_receiver_download_manager));
        totalSizeFetchedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_INIT.toString());
        totalSizeFetchedIntent.putExtra(TransferConstant.TOTAL_SIZE.toString(), totalSize);
        sendBroadcast(totalSizeFetchedIntent);
        this.totalSize = totalSize;
        notificationBuilder.setContentTitle(downloadable.getTitle()).setSmallIcon(R.drawable.ic_action_download);
    }
    @Override
    public void onProgressUpdated(String url, long totalSize, long readSize) {
        this.readSize = readSize;
        int percentDownloaded = (int)((readSize * 100) / this.totalSize);
        notificationBuilder.setProgress(100, percentDownloaded, false);
        notificationBuilder.setContentText("Downloading: " + readSize + "/" + this.totalSize);
        notificationManager.notify(downloadable.getUrl().hashCode(), notificationBuilder.build());
        Intent progressUpdatedIntent = new Intent(getString(R.string.action_receiver_download_manager));
        progressUpdatedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_IN_PROGRESS.toString());
        progressUpdatedIntent.putExtra(TransferConstant.READ_SIZE.toString(), readSize);
        sendBroadcast(progressUpdatedIntent);
    }
    @Override
    public void onDownloadComplete(String url) {
        readSize = 0;
        notificationBuilder.setContentText("Downloaded").setProgress(0, 0, false);
        notificationManager.notify(downloadable.getUrl().hashCode(), notificationBuilder.build());
        Intent downloadCompleteIntent = new Intent(getString(R.string.action_receiver_download_manager));
        downloadCompleteIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_DOWNLOADED.toString());
        sendBroadcast(downloadCompleteIntent);
        stopSelf();
    }

    public class DownloaderServiceBinder extends Binder {
        public DownloaderService getService() {
            return DownloaderService.this;
        }
    }

    public void startLoading() {
        if(!active) {
            active = true;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!isPaused) {
                            downloadable.getTargetPath().delete();
                        }
                        isPaused = false;
                        loader.requestContentLenght();
                        loader.download();
                    } catch (IOException | InterruptedException e) {
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
        onProgressUpdated(downloadable.getUrl(), totalSize, readSize);
        notificationManager.cancel(downloadable.getUrl().hashCode());
        stopSelf();
    }

    public boolean isActive() {
        return active;
    }
}
