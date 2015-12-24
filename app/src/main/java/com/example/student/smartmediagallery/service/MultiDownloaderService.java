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
import com.example.student.smartmediagallery.core.model.LoadCommand;
import com.example.student.smartmediagallery.core.model.RequestsModel;
import com.example.student.smartmediagallery.net.ProgressFileLoader;

public class MultiDownloaderService extends Service implements ProgressFileLoader.LoaderListener{

    private NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;

    MultiDownloaderServiceBinder downloaderServiceBinder;
    int counter;
    RequestsModel requestsModel;

    @Override
    public void onCreate() {
        super.onCreate();
        downloaderServiceBinder = new MultiDownloaderServiceBinder();
        notificationBuilder = new NotificationCompat.Builder(this);
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        requestsModel = new RequestsModel();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent != null) {
            Downloadable downloadable = intent.getParcelableExtra(TransferConstant.CURRENT_MEDIA.toString());
            if(requestsModel.getLoadCommandIdByUrl(downloadable.getUrl()) == -1) {
                Log.d("mylog", "ON START COMMAND: requestsModel.getLoadCommandIdByUrl(downloadable.getUrl()) == -1");
                LoadCommand loadCommand = new LoadCommand(counter++, downloadable);
                requestsModel.addRequest(downloadable.getUrl(), loadCommand);
                requestsModel.prepareCommandByUrl(downloadable.getUrl());
                requestsModel.setCommandListenerByUrl(downloadable.getUrl(), this);
            } else {
                Log.d("mylog", "ON START COMMAND: requestsModel.getLoadCommandIdByUrl(downloadable.getUrl()) != -1");
                if(!requestsModel.getIsActiveByUrl(downloadable.getUrl())) {
                    Log.d("mylog", "ON START COMMAND: !requestsModel.getIsActiveByUrl(downloadable.getUrl())");
                    requestsModel.prepareCommandByUrl(downloadable.getUrl());
                    requestsModel.setCommandListenerByUrl(downloadable.getUrl(), this);
                } else {
                    Log.d("mylog", "ON START COMMAND: requestsModel.getIsActiveByUrl(downloadable.getUrl())");
                    initDialog(downloadable.getUrl());
                }
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    public void initDialog(String url) {
        Log.d("mylog", "initDialog: " + url);
        Intent totalSizeFetchedIntent = new Intent(getString(R.string.action_receiver_download_manager));
        totalSizeFetchedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_INIT.toString());
        totalSizeFetchedIntent.putExtra(TransferConstant.TOTAL_SIZE.toString(), requestsModel.getTotalSizeByUrl(url));
        totalSizeFetchedIntent.putExtra(TransferConstant.MEDIA_URL.toString(), url);
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
        Log.d("mylog", "url onTotalSize: " + url);
        Intent totalSizeFetchedIntent = new Intent(getString(R.string.action_receiver_download_manager));
        totalSizeFetchedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_INIT.toString());
        totalSizeFetchedIntent.putExtra(TransferConstant.TOTAL_SIZE.toString(), totalSize);
        totalSizeFetchedIntent.putExtra(TransferConstant.MEDIA_URL.toString(), url);
        sendBroadcast(totalSizeFetchedIntent);
        requestsModel.setTotalSizeByUrl(url, totalSize);
        Log.d("mylog", "TITLE onTotalSize: " + requestsModel.getTitleByUrl(url));
        Log.d("mylog", "ID onTotalSize: " + requestsModel.getLoadCommandIdByUrl(url));
        notificationBuilder.setSmallIcon(R.drawable.ic_action_download);
    }
    @Override
    public void onProgressUpdated(String url, long totalSize, long readSize) {
        if(getApplicationContext() != null) {
            requestsModel.setReadSizeByUrl(url, readSize);
            int percentDownloaded = (int)((readSize * 100) / requestsModel.getTotalSizeByUrl(url));
            notificationBuilder.setProgress(100, percentDownloaded, false);
            notificationBuilder.setContentText(getString(R.string.notification_downloading) + readSize + "/" + requestsModel.getTotalSizeByUrl(url));
            notificationBuilder.setContentTitle(requestsModel.getTitleByUrl(url));
            notificationManager.notify(requestsModel.getLoadCommandIdByUrl(url), notificationBuilder.build());
            Intent progressUpdatedIntent = new Intent(getString(R.string.action_receiver_download_manager));
            progressUpdatedIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_IN_PROGRESS.toString());
            progressUpdatedIntent.putExtra(TransferConstant.READ_SIZE.toString(), readSize);
            progressUpdatedIntent.putExtra(TransferConstant.MEDIA_URL.toString(), url);
            sendBroadcast(progressUpdatedIntent);
        } else {
            Log.d("mylog", "NULL CONTEXT!!!");
            notificationManager.cancel(requestsModel.getLoadCommandIdByUrl(url));
        }

    }
    @Override
    public void onDownloadComplete(String url) {
        requestsModel.setReadSizeByUrl(url, 0);
        notificationBuilder.setContentText(getString(R.string.notification_downloaded)).setProgress(0, 0, false);
        notificationBuilder.setContentTitle(requestsModel.getTitleByUrl(url));
        notificationManager.notify(requestsModel.getLoadCommandIdByUrl(url), notificationBuilder.build());
        Intent downloadCompleteIntent = new Intent(getString(R.string.action_receiver_download_manager));
        downloadCompleteIntent.putExtra(TransferConstant.MESSAGE_EVENT.toString(), MessageEvent.MESSAGE_DOWNLOADED.toString());
        downloadCompleteIntent.putExtra(TransferConstant.MEDIA_URL.toString(), url);
        sendBroadcast(downloadCompleteIntent);
        stopSelf();
    }

    public class MultiDownloaderServiceBinder extends Binder {
        public MultiDownloaderService getService() {
            return MultiDownloaderService.this;
        }
    }

    public void startLoading(String mutableUrl) {
        final String url = mutableUrl;
        if(!requestsModel.getIsActiveByUrl(url)) {
            requestsModel.setIsActiveByUrl(url, true);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if(!requestsModel.getIsPausedByUrl(url)) {
                            requestsModel.deleteFileFromTargetPath(url);
                        }
                        requestsModel.setIsPausedByUrl(url, false);
                        requestsModel.executeCommandByUrl(url);
                    } finally {
                        requestsModel.setIsActiveByUrl(url, false);
                    }
                }
            }).start();
        } else{
            Log.d("mylog", "downloading hadn't started");
        }
    }

    public void pauseLoading(String url) {
        requestsModel.setIsActiveByUrl(url, false);
        requestsModel.setIsPausedByUrl(url, true);
        requestsModel.pauseCommandByUrl(url);
    }

    public void stopLoading(String url) {
        requestsModel.stopCommandByUrl(url);
        onProgressUpdated(url, requestsModel.getTotalSizeByUrl(url), requestsModel.getReadBytesByUrl(url));
        notificationManager.cancel(requestsModel.getLoadCommandIdByUrl(url));
        requestsModel.removeByUrl(url);
        stopSelf();
    }

    public boolean isActive(String url) {
        return requestsModel.getIsActiveByUrl(url);
    }
}
