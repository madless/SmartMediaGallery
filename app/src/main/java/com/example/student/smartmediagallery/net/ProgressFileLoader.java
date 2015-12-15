package com.example.student.smartmediagallery.net;

import java.io.IOException;

/**
 * Created by student on 10.12.2015.
 */
public abstract class ProgressFileLoader {
    public static final int BUFFER_SIZE = 8192;
    //public static final int BUFFER_SIZE = 50;
    public static final int DEFAULT_TIMEOUT = 15000;

    protected String url;
    protected String targetPath;

    protected LoaderListener progressListener;

    protected long totalSize = 0;
    protected long readBytes = 0;

    protected boolean cancelled = false;

    public interface LoaderListener {
        void onTotalSizeCalculated(long totalSize);
        void onTotalSizeFetched(long totalSize);
        void onProgressUpdated(long totalSize, long readSize);
        void onDownloadComplete();
    }

    public ProgressFileLoader(String url, String targetPath, long readBytes) {
        this.url = url;
        this.targetPath = targetPath;
        this.readBytes = readBytes;
    }

    public ProgressFileLoader(String url) {
        this(url, null, 0);
    }

    public void cancel() {
        cancelled = true;
    }

    public LoaderListener getProgressListener() {
        return progressListener;
    }

    public void setProgressListener(LoaderListener progressListener) {
        this.progressListener = progressListener;
    }

    public abstract void download() throws IOException, InterruptedException;
    public abstract void requestContentLenght() throws IOException;
}
