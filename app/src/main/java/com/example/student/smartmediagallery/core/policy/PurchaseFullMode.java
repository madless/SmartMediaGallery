package com.example.student.smartmediagallery.core.policy;

import com.example.student.smartmediagallery.core.model.PhotoItem;
import com.example.student.smartmediagallery.core.model.SoundItem;
import com.example.student.smartmediagallery.core.model.VideoItem;

/**
 * Created by student on 23.12.2015.
 */
public class PurchaseFullMode implements PurchaseMode {
    @Override
    public boolean isAvailablePhoto(int id, PhotoItem photoItem) {
        return true;
    }

    @Override
    public boolean isAvailableSound(int id, SoundItem soundItem) {
        return true;
    }

    @Override
    public boolean isAvailableVideo(int id, VideoItem videoItem) {
        return true;
    }

    @Override
    public boolean isHeaderVisible() {
        return false;
    }
}

