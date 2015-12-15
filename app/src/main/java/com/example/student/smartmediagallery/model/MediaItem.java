package com.example.student.smartmediagallery.model;

import android.os.Parcelable;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.MediaState;

public abstract class MediaItem implements Parcelable {
    private MediaState state = MediaState.STATE_DEFAULT;

    public MediaState getState() {
        return state;
    }

    public void setState(MediaState state) {
        this.state = state;
    }

    public abstract String getTitle();
    public abstract String getIconUrl();

    public String getStateIcon() {
        switch (state) {
            case STATE_DEFAULT: {
                return "drawable://" + R.drawable.ic_save;
            }
            case STATE_DOWNLOADING: {
                return "drawable://" + R.drawable.ic_wait;
            }
            case STATE_DOWNLOADED: {
                return "drawable://" + R.drawable.ic_play_saved;
            }
        }
        return null;
    }
}
