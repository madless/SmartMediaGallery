package com.example.student.smartmediagallery.core.manager;

import android.content.Context;
import android.net.Uri;

import com.example.student.smartmediagallery.core.model.Downloadable;

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

    public String getDownloadablePath(Downloadable downloadable) {
        return context.getFilesDir() + File.separator + downloadable.getTitle() + getType(downloadable);
    }

    public String getItemPathByUri(Uri downloadableUri) {
        return context.getFilesDir() + File.separator + downloadableUri.getLastPathSegment();
    }

    public boolean isSdCardAvailable() {
        return false;
    }

    public static String getType(Downloadable downloadable) {
        Pattern pattern = Pattern.compile("\\.[a-zA-z0-9]+$");
        Matcher matcher = pattern.matcher(downloadable.getUrl());
        if(matcher.find()) {
            return matcher.group();
        }
        return "";
    }
}
