package com.example.student.smartmediagallery;

import android.os.Message;
import android.util.Log;

import com.example.student.smartmediagallery.net.ProgressFileLoader;
import com.example.student.smartmediagallery.net.ProgressFileLoaderBasedOnUrlConnection;

import java.io.File;
import java.io.IOException;

/**
 * Created by student on 11.12.2015.
 */
public class Downloader implements Runnable, ProgressFileLoader.LoaderListener{
    Downloadable downloadable;
    DownloadingHandler downloadingHandler;
    ProgressFileLoaderBasedOnUrlConnection loader;
    boolean isCanceled;

    public Downloader(Downloadable downloadable, DownloadingHandler downloadingHandler) {
        this.downloadable = downloadable;
        this.downloadingHandler = downloadingHandler;
    }

    @Override
    public void run() {
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_INIT, downloadable);
        downloadingHandler.sendMessage(message);
        String url = downloadable.getUrl();
        String targetPathStr = downloadable.getTargetPath().getAbsolutePath();
        long bytesRead = downloadable.getBytesRead();
        Log.d("mylog", "bytes read: " + bytesRead);
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

    }

    @Override
    public void onTotalSizeFetched(long totalSize) {
        downloadable.setTotalSize(totalSize);
    }

    @Override
    public void onProgressUpdated(long totalSize, long readSize) {
        if(!isCanceled || readSize >= downloadable.getTotalSize()) {
            downloadable.setBytesRead(readSize);
            Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_IN_PROGRESS, downloadable);
            downloadingHandler.sendMessage(message);
        }
    }

    @Override
    public void onDownloadComplete() {
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_DOWNLOADED, downloadable);
        downloadingHandler.sendMessage(message);
        downloadable.setBytesRead(0);
    }

    public void pause() {
        Log.d("mylog", "PAUSE");
        if(loader != null) {
            loader.cancel();
        }
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_PAUSED, downloadable);
        downloadingHandler.sendMessage(message);
    }

    public void stop() {
        Log.d("mylog", "STOP");
        if(loader != null) {
            loader.cancel();
        }
        isCanceled = true;
        downloadable.setBytesRead(0);
        downloadable.getTargetPath().delete();
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_STOPPED, downloadable);
        downloadingHandler.sendMessage(message);
    }
}
