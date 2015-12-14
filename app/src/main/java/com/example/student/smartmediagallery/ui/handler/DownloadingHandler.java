package com.example.student.smartmediagallery.ui.handler;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;

/**
 * Created by student on 11.12.2015.
 */
public class DownloadingHandler extends Handler {

    public final static int MAX_PROGRESS = 100;

    public static final int MESSAGE_IN_PROGRESS = 0;
    public static final int MESSAGE_PAUSED = 1;
    public static final int MESSAGE_STOPPED = 2;
    public static final int MESSAGE_DOWNLOADED = 3;
    public static final int MESSAGE_INIT = 4;

    Context context;

    public DownloadingHandler(Context context) {
        this.context = context;
    }

}
