package com.example.student.smartmediagallery.model;

/**
 * Created by student on 17.12.2015.
 */
public class Notificator {
    int id;
    Downloadable downloadable;
    boolean isPaused;

    public Notificator(int id, Downloadable downloadable) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Notificator)) return false;

        Notificator that = (Notificator) o;

        if (getId() != that.getId()) return false;
        return !(getDownloadable() != null ? !getDownloadable().equals(that.getDownloadable()) : that.getDownloadable() != null);

    }

    @Override
    public int hashCode() {
        int result = getId();
        result = 31 * result + (getDownloadable() != null ? getDownloadable().hashCode() : 0);
        return result;
    }
}
