package com.example.student.smartmediagallery.core.model;

import android.util.Log;

import java.io.File;

public abstract class MediaItem implements ListItem, Downloadable {
    protected String targetPath = "";
    protected long totalSize;
    protected long bytesRead;

    protected String title;
    protected String iconUrl;
    protected String url;

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getIconUrl() {
        return iconUrl;
    }

    @Override
    public File getTargetPath() {
        Log.d("mylog", "getTargetPath(): " + targetPath);
        return new File(targetPath);
    }

    @Override
    public long getTotalSize() {
        return totalSize;
    }

    @Override
    public long getBytesRead() {
        return bytesRead;
    }

    @Override
    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }

    @Override
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public void setTargetPath(File targetPath) {
        this.targetPath = targetPath.getAbsolutePath();
    }

    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }
}
