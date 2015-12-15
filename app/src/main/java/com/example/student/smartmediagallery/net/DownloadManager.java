package com.example.student.smartmediagallery.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
import android.util.Log;

import com.example.student.smartmediagallery.constants.MessageEvent;
import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.model.Downloadable;
import com.example.student.smartmediagallery.service.NewDownloaderService;
import com.example.student.smartmediagallery.ui.handler.DownloadingHandler;

public class DownloadManager implements Runnable {

    public final static String BROADCAST_ACTION_DOWNLOAD = "gfl.madless.smart_media_galery.downloading_receive";

    Context context;
    Downloadable downloadable;
    DownloadingHandler downloadingHandler;
    boolean isCanceled;
    boolean isPaused;
    DownloadingReceiver downloadingReceiver;

    public DownloadManager(Context context, Downloadable downloadable, DownloadingHandler downloadingHandler) {
        this.context = context;
        this.downloadable = downloadable;
        this.downloadingHandler = downloadingHandler;
    }

    @Override
    public void run() {
        isCanceled = false;
        if(!isPaused) {
            downloadable.getTargetPath().delete();
        }
        isPaused = false;

        String url = downloadable.getUrl();
        String targetPathStr = downloadable.getTargetPath().getAbsolutePath();
        long bytesRead = downloadable.getBytesRead();


        downloadingReceiver = new DownloadingReceiver();
        IntentFilter intentFilter = new IntentFilter(BROADCAST_ACTION_DOWNLOAD);
        context.registerReceiver(downloadingReceiver, intentFilter);

        Log.d("mylog", "context: " + context);

        Intent downloaderServiceIntent = new Intent(context, NewDownloaderService.class);

        downloaderServiceIntent.putExtra(TransferConstant.MEDIA_URL.toString(), url);
        downloaderServiceIntent.putExtra(TransferConstant.TARGET_PATH.toString(), targetPathStr);
        downloaderServiceIntent.putExtra(TransferConstant.READ_SIZE.toString(), bytesRead);

        context.startService(downloaderServiceIntent);

    }

    public void pause() {
        Intent cancelDownloadingIntent = new Intent(NewDownloaderService.BROADCAST_ACTION_CANCEL_DOWNLOADING);
        context.sendBroadcast(cancelDownloadingIntent);
        isPaused = true;
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_PAUSED, downloadable);
        downloadingHandler.sendMessage(message);
        context.unregisterReceiver(downloadingReceiver);
    }

    public void stop() {
        Intent cancelDownloadingIntent = new Intent(NewDownloaderService.BROADCAST_ACTION_CANCEL_DOWNLOADING);
        context.sendBroadcast(cancelDownloadingIntent);
        isCanceled = true;
        downloadable.setBytesRead(0);
        downloadable.getTargetPath().delete();
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_STOPPED, downloadable);
        downloadingHandler.sendMessage(message);
        context.unregisterReceiver(downloadingReceiver);
    }

    public class DownloadingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MessageEvent event = MessageEvent.valueOf(intent.getStringExtra(TransferConstant.MESSAGE_EVENT.toString()));
            switch (event) {
                case MESSAGE_INIT: {
                    long totalSize = intent.getLongExtra(TransferConstant.TOTAL_SIZE.toString(), -1);
                    downloadable.setTotalSize(totalSize);
                    Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_INIT, downloadable);
                    downloadingHandler.sendMessage(message);
                    break;
                }
                case MESSAGE_IN_PROGRESS: {
                    long readSize = intent.getLongExtra(TransferConstant.READ_SIZE.toString(), -1);
                    if(!isCanceled || readSize >= downloadable.getTotalSize()) {
                        downloadable.setBytesRead(readSize);
                        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_IN_PROGRESS, downloadable);
                        downloadingHandler.sendMessage(message);
                    }
                    break;
                }
                case MESSAGE_DOWNLOADED: {
                    Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_DOWNLOADED, downloadable);
                    downloadingHandler.sendMessage(message);
                    downloadable.setBytesRead(0);
                    break;
                }
                default: {
                    break;
                }
            }
        }
    }
}
