package com.example.student.smartmediagallery.model;

import android.os.Parcelable;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.MediaState;

public interface MediaItem extends Parcelable {
    public abstract String getTitle();
    public abstract String getIconUrl();

}
