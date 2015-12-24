package com.example.student.smartmediagallery.core.policy;

import android.util.Log;

import com.example.student.smartmediagallery.core.manager.PurchaseManager;
import com.example.student.smartmediagallery.core.model.PhotoItem;
import com.example.student.smartmediagallery.core.model.SoundItem;
import com.example.student.smartmediagallery.core.model.VideoItem;

/**
 * Created by student on 23.12.2015.
 */
public class PurchaseModeProxy {
    PurchaseMode currentPurchaseMode;
    PurchaseMode purchaseFreeMode;
    PurchaseMode purchaseFullMode;
    PurchaseManager purchaseManager;

    public PurchaseModeProxy(PurchaseManager purchaseManager) {
        purchaseFreeMode = new PurchaseFreeMode();
        purchaseFullMode = new PurchaseFullMode();
        currentPurchaseMode = purchaseFreeMode;
        this.purchaseManager = purchaseManager;
    }

    public void checkIfPurchased() {
        if(purchaseManager.isPurchased()) {
            Log.d("mylog", "purchased photo");
            currentPurchaseMode = purchaseFullMode;
        } else {
            Log.d("mylog", "not purchased photo");
        }
    }

    public boolean isAvailablePhoto(int id, PhotoItem photoItem) {
        checkIfPurchased();
        return currentPurchaseMode.isAvailablePhoto(id, photoItem);
    }

    public boolean isAvailableSound(int id, SoundItem soundItem) {
        checkIfPurchased();
        return currentPurchaseMode.isAvailableSound(id, soundItem);
    }

    public boolean isAvailableVideo(int id, VideoItem videoItem) {
        checkIfPurchased();
        return currentPurchaseMode.isAvailableVideo(id, videoItem);
    }

    public boolean isHeaderVisible() {
        if(purchaseManager.isPurchased()) {
            Log.d("mylog", "purchased buttonPurchase");
            currentPurchaseMode = purchaseFullMode;
        } else {
            Log.d("mylog", "not purchased buttonPurchase");
        }
        return currentPurchaseMode.isHeaderVisible();
    }
}
