package com.example.student.smartmediagallery.core.policy;

import com.example.student.smartmediagallery.core.model.PhotoItem;
import com.example.student.smartmediagallery.core.model.SoundItem;
import com.example.student.smartmediagallery.core.model.VideoItem;

/**
 * Created by student on 23.12.2015.
 */
public class PurchaseFreeMode implements PurchaseMode {
    @Override
    public boolean isAvailablePhoto(int id, PhotoItem photoItem) {
        if(id < 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAvailableSound(int id, SoundItem soundItem) {
        if(id < 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isAvailableVideo(int id, VideoItem videoItem) {
        if(id < 3) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isHeaderVisible() {
        return false;
    }
}
