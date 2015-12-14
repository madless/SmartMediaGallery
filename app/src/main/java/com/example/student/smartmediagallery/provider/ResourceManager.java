package com.example.student.smartmediagallery.provider;

import android.content.Context;
import android.net.Uri;

import com.example.student.smartmediagallery.model.SoundItem;
import com.example.student.smartmediagallery.model.VideoItem;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by student on 14.12.2015.
 */
public class ResourceManager {
    Context context;

    public ResourceManager(Context context) {
        this.context = context;
    }

    public String getSoundItemPath(SoundItem soundItem) {
        return context.getFilesDir() + File.separator + soundItem.getTitle() + getSoundType(soundItem);
    }

    public String getVideoItemPath(VideoItem videoItem) {
        return context.getFilesDir() + File.separator + videoItem.getTitle() + getVideoType(videoItem);
    }

    public String getSoundItemPathByUri(Uri soundUri) {
        return context.getFilesDir() + File.separator + soundUri.getLastPathSegment();
    }

    public boolean isSdCardAvailable() {
        return false;
    }

    private String getSoundType(SoundItem soundItem) {
        Pattern pattern = Pattern.compile("\\.[a-zA-z0-9]+$");
        Matcher matcher = pattern.matcher(soundItem.getSoundUrl());
        if(matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    private String getVideoType(VideoItem soundItem) {
        Pattern pattern = Pattern.compile("\\.[a-zA-z0-9]+$");
        Matcher matcher = pattern.matcher(soundItem.getVideoUrl());
        if(matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
