package com.example.student.smartmediagallery.model;

import android.os.Parcel;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoItem extends MediaItem {

    public PhotoItem(String iconUrl, String url) {
        this.iconUrl = iconUrl;
        this.url = url;
    }

    public PhotoItem(Parcel parcel) {
        this.setIconUrl(parcel.readString());
        this.setUrl(parcel.readString());
        this.setTargetPath(new File(parcel.readString()));
        this.setBytesRead(parcel.readLong());
    }

    public static final Creator<PhotoItem> CREATOR = new Creator<PhotoItem>() {
        @Override
        public PhotoItem createFromParcel(Parcel source) {
            return new PhotoItem(source);
        }

        @Override
        public PhotoItem[] newArray(int size) {
            return new PhotoItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(getIconUrl());
        dest.writeString(getUrl());
        dest.writeString(targetPath);
        dest.writeLong(getBytesRead());
    }

    @Override
    public String getTitle() {
        Pattern pattern = Pattern.compile("\\w*\\.[a-zA-z]+$");
        Matcher matcher = pattern.matcher(url);
        if(matcher.find()) {
            return matcher.group();
        }
        return null;
    }
}
