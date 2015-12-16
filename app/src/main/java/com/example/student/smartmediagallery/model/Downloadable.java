package com.example.student.smartmediagallery.model;

import java.io.File;

/**
 * Created by student on 11.12.2015.
 */
public class Downloadable {
    private String title;
    private String url;
    private File targetPath;
    private long totalSize;
    private long bytesRead;

    public Downloadable(String title, String url, File targetPath) {
        this.title = title;
        this.url = url;
        this.targetPath = targetPath;
    }

    public void setBytesRead(long bytesRead) {
        this.bytesRead = bytesRead;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    public String getUrl() {
        return url;
    }

    public File getTargetPath() {
        return targetPath;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public long getBytesRead() {
        return bytesRead;
    }

    public String getTitle() {
        return title;
    }
}
