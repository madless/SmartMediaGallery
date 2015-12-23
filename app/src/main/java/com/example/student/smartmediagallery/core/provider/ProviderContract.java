package com.example.student.smartmediagallery.core.provider;

import android.net.Uri;

import com.example.student.smartmediagallery.core.model.Downloadable;
import com.example.student.smartmediagallery.core.manager.ResourceManager;

import java.io.File;

/**
 * Created by student on 14.12.2015.
 */
public final class ProviderContract {
    public static final String CONTENT = "content://";
    public static final String AUTHORITY = "com.example.student.smartmediagallery.internal_file_provider";
    public static final String SOUND_DIR = "audio";
    public static final String VIDEO_DIR = "video";
    public static final String ALL_IN_DIR = "/*";

    public static Uri getDownloadableUri(Downloadable item) {
        String fullTitle = item.getTitle() + ResourceManager.getType(item);
        return Uri.parse(CONTENT + AUTHORITY + File.separator + SOUND_DIR + File.separator + fullTitle);
    }
}
