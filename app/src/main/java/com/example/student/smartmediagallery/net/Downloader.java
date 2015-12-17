package com.example.student.smartmediagallery.net;

import android.os.Message;

import com.example.student.smartmediagallery.model.Downloadable;
import com.example.student.smartmediagallery.ui.handler.DownloadingHandler;

import java.io.IOException;

public class Downloader implements Runnable, ProgressFileLoader.LoaderListener{
    Downloadable downloadable;
    DownloadingHandler downloadingHandler;
    ProgressFileLoaderBasedOnUrlConnection loader;
    boolean isCanceled;
    boolean isPaused;

    public Downloader(Downloadable downloadable, DownloadingHandler downloadingHandler) {
        this.downloadable = downloadable;
        this.downloadingHandler = downloadingHandler;
    }

    @Override
    public void run() {
        isCanceled = false;
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_INIT, downloadable);
        downloadingHandler.sendMessage(message);
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
    public void onTotalSizeCalculated(String url, long totalSize) {

    }

    @Override
    public void onTotalSizeFetched(String url, long totalSize) {
        downloadable.setTotalSize(totalSize);
    }

    @Override
    public void onProgressUpdated(String url, long totalSize, long readSize) {
        if(!isCanceled || readSize >= downloadable.getTotalSize()) {
            downloadable.setBytesRead(readSize);
            Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_IN_PROGRESS, downloadable);
            downloadingHandler.sendMessage(message);
        }
    }

    @Override
    public void onDownloadComplete(String url) {
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_DOWNLOADED, downloadable);
        downloadingHandler.sendMessage(message);
        downloadable.setBytesRead(0);
    }

    public void pause() {
        if(loader != null) {
            loader.cancel();
        }
        isPaused = true;
        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_PAUSED, downloadable);
        downloadingHandler.sendMessage(message);
    }

    public void stop() {
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
