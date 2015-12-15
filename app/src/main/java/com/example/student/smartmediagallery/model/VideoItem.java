package com.example.student.smartmediagallery.model;

import android.os.Parcel;

public class VideoItem extends MediaItem {
    private String title;
    private String iconUrl;
    private String videoUrl;

    public VideoItem(String title, String iconUrl, String videoUrl) {
        this.title = title;
        this.iconUrl = iconUrl;
        this.videoUrl = videoUrl;
    }

    public VideoItem(Parcel parcel) {
        setTitle(parcel.readString());
        setIconUrl(parcel.readString());
        setVideoUrl(parcel.readString());
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public static final Creator<VideoItem> CREATOR = new Creator<VideoItem>() {
        @Override
        public VideoItem createFromParcel(Parcel source) {
            return new VideoItem(source);
        }

        @Override
        public VideoItem[] newArray(int size) {
            return new VideoItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(iconUrl);
        dest.writeString(videoUrl);
    }

    @Override
    public String toString() {
        return "VideoItem{" +
                "title='" + title + '\'' +
                ", iconUrl='" + iconUrl + '\'' +
                ", videoUrl='" + videoUrl + '\'' +
                '}';
    }
}
