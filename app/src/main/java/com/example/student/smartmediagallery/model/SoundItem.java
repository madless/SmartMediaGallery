package com.example.student.smartmediagallery.model;

import android.os.Parcel;

import com.example.student.smartmediagallery.R;

public class SoundItem extends MediaItem {


    private String title;
    private String soundUrl;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getIconUrl() {
        return "drawable://" + R.drawable.ic_sound_m;
    }

    public String getSoundUrl() {
        return soundUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setSoundUrl(String soundUrl) {
        this.soundUrl = soundUrl;
    }

    public SoundItem(String title, String soundUrl) {
        this.title = title;
        this.soundUrl = soundUrl;
    }

    private SoundItem(Parcel parcel) {
        setTitle(parcel.readString());
        setSoundUrl(parcel.readString());
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
        dest.writeString(soundUrl);
    }

    @Override
    public String toString() {
        return "SoundItem{" +
                "title='" + title + '\'' +
                ", soundUrl='" + soundUrl + '\'' +
                '}';
    }
}
