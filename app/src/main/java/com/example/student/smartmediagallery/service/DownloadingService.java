package com.example.student.smartmediagallery.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.model.Downloadable;
import com.example.student.smartmediagallery.model.VideoItem;
import com.example.student.smartmediagallery.net.Downloader;
import com.example.student.smartmediagallery.net.ProgressFileLoader;
import com.example.student.smartmediagallery.net.ProgressFileLoaderBasedOnUrlConnection;
import com.example.student.smartmediagallery.provider.ResourceManager;
import com.example.student.smartmediagallery.ui.handler.DownloadingHandler;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadingService extends Service {
    public final static int STATUS_OK = 0;
    public final static int STATUS_PAUSED = 1;
    public final static int STATUS_FAILED = 2;

    private Downloadable downloadable;
    private DownloadingHandler videoDownloadingHandler;
    private ExecutorService executorService;
    private Downloader downloader;

    NotificationCompat.Builder notificationBuilder;

    @Override
    public void onCreate() {
        super.onCreate();
        notificationBuilder = new NotificationCompat.Builder(this);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isStarting = intent.getBooleanExtra("is starting", true);
        if(isStarting) {
            Log.d("mylog", "isStarting");
            VideoItem videoItem = intent.getParcelableExtra(TransferConstant.CURRENT_MEDIA.toString());
            final int position = intent.getIntExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), -1);
            ResourceManager resourceManager = new ResourceManager(getApplicationContext());
            String title = videoItem.getTitle();
            String url = videoItem.getVideoUrl();
            File targetPath = new File(resourceManager.getVideoItemPath(videoItem));
            downloadable = new Downloadable(title, url, targetPath);
            ServiceDownloader downloader = new ServiceDownloader(this, downloadable, position, notificationBuilder);
            executorService = Executors.newFixedThreadPool(1);
            executorService.execute(downloader);
            Log.d("mylog", "video downloading started!");
        } else {
            int callerId = intent.getIntExtra("callerId", -1);
            Log.d("mylog", "service must stop self (id: " + callerId + ")");
            stopSelf(callerId);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("mylog", "SERVICE DESTROYED!");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public class ServiceDownloader implements Runnable, ProgressFileLoader.LoaderListener{
        public final static int MAX_PROGRESS = 100;

        private NotificationManager notificationManager;
        NotificationCompat.Builder notificationBuilder;
        int id;

        Downloadable downloadable;
        ProgressFileLoaderBasedOnUrlConnection loader;
        boolean isCanceled;
        boolean isPaused;

        public ServiceDownloader(Context context, Downloadable downloadable, int id, NotificationCompat.Builder notificationBuilder) {
            this.downloadable = downloadable;
            this.id = id;
            this.notificationBuilder = notificationBuilder;
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        @Override
        public void run() {
            isCanceled = false;
            String url = downloadable.getUrl();
            String targetPathStr = downloadable.getTargetPath().getAbsolutePath();
            long bytesRead = downloadable.getBytesRead();
            if(!isPaused) {
                downloadable.getTargetPath().delete();
            }
            isPaused = false;
            loader = new ProgressFileLoaderBasedOnUrlConnection(url, targetPathStr, bytesRead);
            loader.setProgressListener(this);
            try {
                loader.requestContentLenght();
                loader.download();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onTotalSizeCalculated(long totalSize) {
            notificationBuilder.setContentTitle(downloadable.getTitle())
                    .setContentText("Downloading: " + downloadable.getBytesRead() + "/" + downloadable.getTotalSize())
                    .setSmallIcon(R.drawable.ic_action_download);
            Intent stopServiceIntent = new Intent(getApplicationContext(), DownloadingService.class);
            stopServiceIntent.putExtra("is starting", false);
            stopServiceIntent.putExtra("callerId", id);
            PendingIntent stopServicePendingIntent = PendingIntent.getService(getApplicationContext(), id, stopServiceIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationBuilder.setContentIntent(stopServicePendingIntent);
        }

        @Override
        public void onTotalSizeFetched(long totalSize) {
            downloadable.setTotalSize(totalSize);
        }

        @Override
        public void onProgressUpdated(long totalSize, long readSize) {
            if(!isCanceled || readSize >= downloadable.getTotalSize()) {
                downloadable.setBytesRead(readSize);
            }
            int percentDownloaded = (int)((downloadable.getBytesRead() * MAX_PROGRESS) / (float)downloadable.getTotalSize());
            notificationBuilder.setProgress(MAX_PROGRESS, percentDownloaded, false);
            notificationBuilder.setContentText("Downloading: " + downloadable.getBytesRead() + "/" + downloadable.getTotalSize());
            notificationManager.notify(id, notificationBuilder.build());
        }

        @Override
        public void onDownloadComplete() {
            downloadable.setBytesRead(0);
            notificationBuilder.setContentText("Downloaded").setProgress(0,0,false);
            notificationManager.notify(id, notificationBuilder.build());
        }

        public void pause() {
            if(loader != null) {
                loader.cancel();
            }
            isPaused = true;
        }

        public void stop() {
            if(loader != null) {
                loader.cancel();
            }
            isCanceled = true;
            downloadable.setBytesRead(0);
            downloadable.getTargetPath().delete();
            stopSelf();
        }
    }
}
