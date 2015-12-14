package com.example.student.smartmediagallery.provider;

import android.net.Uri;

import com.example.student.smartmediagallery.model.Downloadable;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by student on 14.12.2015.
 */
public final class ProviderContract {
    public static final String CONTENT = "content://";
    public static final String AUTHORITY = "com.example.student.smartmediagallery.internal_file_provider";
    public static final String SOUND_DIR = "audio";
    public static final String VIDEO_DIR = "video";
    public static final String ALL_IN_DIR = "/*";

    private static String getType(Downloadable item) {
        Pattern pattern = Pattern.compile("\\.[a-zA-z0-9]+$");
        Matcher matcher = pattern.matcher(item.getUrl());
        if(matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static Uri getDownloadableUri(Downloadable item) {
        String fullTitle = item.getTitle() + getType(item);
        return Uri.parse(CONTENT + AUTHORITY + File.separator + SOUND_DIR + File.separator + fullTitle);
    }
}
