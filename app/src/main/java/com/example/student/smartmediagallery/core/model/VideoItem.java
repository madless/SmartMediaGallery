package com.example.student.smartmediagallery.core.model;

import android.os.Parcel;

import java.io.File;

public class VideoItem extends MediaItem {

    public VideoItem(String title, String iconUrl, String url) {
        this.title = title;
        this.iconUrl = iconUrl;
        this.url = url;
    }

    private VideoItem(Parcel parcel) {
        setTitle(parcel.readString());
        setIconUrl(parcel.readString());
        setUrl(parcel.readString());
        setTargetPath(new File(parcel.readString()));
        setBytesRead(parcel.readLong());
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
        dest.writeString(url);
        dest.writeString(targetPath);
        dest.writeLong(getBytesRead());
    }

}
