package com.example.student.smartmediagallery.model;

import android.os.Parcelable;

public interface MediaItem extends Parcelable {
    String getTitle();
    String getIconUrl();
}
