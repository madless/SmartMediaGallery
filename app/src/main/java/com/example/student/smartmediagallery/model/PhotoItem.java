package com.example.student.smartmediagallery.model;

import android.os.Parcel;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhotoItem implements MediaItem {
    private String iconUrl;
    private String photoUrl;

    public PhotoItem(String iconUrl, String photoUrl) {
        this.iconUrl = iconUrl;
        this.photoUrl = photoUrl;
    }

    public PhotoItem(Parcel parcel) {
        this.setIconUrl(parcel.readString());
        this.setPhotoUrl(parcel.readString());
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
        dest.writeString(getPhotoUrl());
    }

    @Override
    public String getTitle() {
        Pattern pattern = Pattern.compile("\\w*\\.[a-zA-z]+$");
        Matcher matcher = pattern.matcher(photoUrl);
        if(matcher.find()) {
            return matcher.group();
        }
        return null;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    @Override
    public String toString() {
        return "PhotoItem{" +
                "iconUrl='" + iconUrl + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                '}';
    }
}
