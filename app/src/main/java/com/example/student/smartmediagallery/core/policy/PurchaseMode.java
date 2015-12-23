package com.example.student.smartmediagallery.core.policy;

import com.example.student.smartmediagallery.core.manager.PurchaseManager;
import com.example.student.smartmediagallery.core.model.PhotoItem;
import com.example.student.smartmediagallery.core.model.SoundItem;
import com.example.student.smartmediagallery.core.model.VideoItem;

/**
 * Created by student on 23.12.2015.
 */
public interface PurchaseMode {
    boolean isAvailablePhoto(int id, PhotoItem photoItem);
    boolean isAvailableSound(int id, SoundItem soundItem);
    boolean isAvailableVideo(int id, VideoItem videoItem);
    boolean isHeaderVisible();
}
