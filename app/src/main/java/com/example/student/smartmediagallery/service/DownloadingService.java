package com.example.student.smartmediagallery.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.Constants;
import com.example.student.smartmediagallery.model.Downloadable;
import com.example.student.smartmediagallery.model.VideoItem;
import com.example.student.smartmediagallery.net.Downloader;
import com.example.student.smartmediagallery.provider.ResourceManager;
import com.example.student.smartmediagallery.ui.handler.DownloadingHandler;
import com.example.student.smartmediagallery.ui.handler.VideoDownloadingHandler;

import java.io.File;
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
        notificationBuilder.setContentTitle("Picture Download")
                .setContentText("Download in progress")
                .setSmallIcon(R.drawable.ic_action_download);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("mylog", "onStartCommand!");
        VideoItem videoItem = intent.getParcelableExtra(Constants.CURRENT_MEDIA.toString());
        int pos = intent.getIntExtra(Constants.CURRENT_MEDIA_POS.toString(), -1);

        ResourceManager resourceManager = new ResourceManager(getApplicationContext());
        String title = videoItem.getTitle();
        String url = videoItem.getVideoUrl();
        File targetPath = new File(resourceManager.getVideoItemPath(videoItem));
        downloadable = new Downloadable(title, url, targetPath);
        videoDownloadingHandler = new VideoDownloadingHandler(getApplicationContext(), pos, notificationBuilder);
        executorService = Executors.newFixedThreadPool(1);
        downloader = new Downloader(downloadable, videoDownloadingHandler);
        executorService.execute(downloader);
        Log.d("mylog", "video downloading started!");
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
