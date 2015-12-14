package com.example.student.smartmediagallery.ui.handler;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Message;
import android.support.v7.app.NotificationCompat;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.model.Downloadable;

/**
 * Created by student on 14.12.2015.
 */
public class VideoDownloadingHandler extends DownloadingHandler {

    private NotificationManager notificationManager;
    NotificationCompat.Builder notificationBuilder;
    int id;

    public VideoDownloadingHandler(Context context, int id, NotificationCompat.Builder notificationBuilder) {
        super(context);
        this.notificationBuilder = notificationBuilder;
        this.id = id;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Downloadable downloadable = (Downloadable) msg.obj;
        switch (msg.what) {
            case MESSAGE_INIT: {
                break;
            }
            case MESSAGE_IN_PROGRESS: {
                int percentDownloaded = (int)((downloadable.getBytesRead() * MAX_PROGRESS) / (float)downloadable.getTotalSize());
                notificationBuilder.setProgress(MAX_PROGRESS, percentDownloaded, false);
                notificationManager.notify(id, notificationBuilder.build());
                break;
            }
            case MESSAGE_PAUSED: {
                break;
            }
            case MESSAGE_DOWNLOADED: {
                notificationBuilder.setContentText("Download complete").setProgress(0,0,false);
                notificationManager.notify(id, notificationBuilder.build());
                Toast.makeText(context, "Video was downloaded!", Toast.LENGTH_SHORT).show();
                break;
            }
            case MESSAGE_STOPPED: {
                break;
            }
        }
    }
}
