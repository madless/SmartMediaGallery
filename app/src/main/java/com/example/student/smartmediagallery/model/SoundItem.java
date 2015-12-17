package com.example.student.smartmediagallery.model;

import android.os.Parcel;

import com.example.student.smartmediagallery.R;

import java.io.File;

public class SoundItem extends MediaItem {

    public SoundItem(String title, String url) {
        this.title = title;
        this.url = url;
    }

    private SoundItem(Parcel parcel) {
        setTitle(parcel.readString());
        setUrl(parcel.readString());
        setTargetPath(new File(parcel.readString()));
        setBytesRead(parcel.readLong());
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getIconUrl() {
        return "drawable://" + R.drawable.ic_sound_m;
    }

    public static final Creator<SoundItem> CREATOR = new Creator<SoundItem>() {
        @Override
        public SoundItem createFromParcel(Parcel source) {
            return new SoundItem(source);
        }

        @Override
        public SoundItem[] newArray(int size) {
            return new SoundItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(url);
        dest.writeString(targetPath);
        dest.writeLong(getBytesRead());
    }

}
