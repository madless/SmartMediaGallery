package com.example.student.smartmediagallery.core.model;

import android.os.Parcelable;

import java.io.File;

/**
 * Created by student on 17.12.2015.
 */
public interface Downloadable extends Parcelable {
    abstract String getTitle();
    public File getTargetPath();
    public long getTotalSize();
    public long getBytesRead();
    public String getUrl();
    public void setBytesRead(long bytesRead);
    public void setTotalSize(long totalSize);
    public void setTargetPath(File targetPath);
}
