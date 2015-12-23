package com.example.student.smartmediagallery.core.model;

import com.example.student.smartmediagallery.net.ProgressFileLoader;
import com.example.student.smartmediagallery.net.ProgressFileLoaderBasedOnUrlConnection;

import java.io.IOException;

/**
 * Created by student on 17.12.2015.
 */
public class LoadCommand {
    private int id;
    private Downloadable downloadable;
    private boolean isPaused;
    private boolean active;
    private long totalSize;
    ProgressFileLoaderBasedOnUrlConnection loader;


    public LoadCommand(int id, Downloadable downloadable) {
        this.id = id;
        this.downloadable = downloadable;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Downloadable getDownloadable() {
        return downloadable;
    }

    public void setDownloadable(Downloadable downloadable) {
        this.downloadable = downloadable;
    }

    public boolean isPaused() {
        return isPaused;
    }

    public void setIsPaused(boolean isPaused) {
        this.isPaused = isPaused;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public long getTotalSize() {
        return totalSize;
    }

    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof LoadCommand)) return false;

        LoadCommand that = (LoadCommand) o;

        if (getId() != that.getId()) return false;
        if (isPaused() != that.isPaused()) return false;
        if (isActive() != that.isActive()) return false;
        if (getTotalSize() != that.getTotalSize()) return false;
        return getDownloadable().equals(that.getDownloadable());

    }

    public void prepareLoading(){
        loader = new ProgressFileLoaderBasedOnUrlConnection(downloadable.getUrl(), downloadable.getTargetPath().getAbsolutePath(), downloadable.getBytesRead());
    }

    public void setListener(ProgressFileLoader.LoaderListener listener) {
        loader.setProgressListener(listener);
    }

    public void load() {
        try {
            loader.requestContentLenght();
            loader.download();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        loader.cancel();
    }

    public void stop() {
        loader.cancel();
        getDownloadable().setBytesRead(0);
    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + getDownloadable().hashCode();
        result = 31 * result + (isPaused() ? 1 : 0);
        result = 31 * result + (isActive() ? 1 : 0);
        result = 31 * result + (int) (getTotalSize() ^ (getTotalSize() >>> 32));
        return result;
    }
}
