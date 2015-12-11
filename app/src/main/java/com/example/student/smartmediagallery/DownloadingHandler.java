package com.example.student.smartmediagallery;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;

import java.lang.ref.WeakReference;

/**
 * Created by student on 11.12.2015.
 */
public class DownloadingHandler extends Handler {
    public static final int MESSAGE_IN_PROGRESS = 0;
    public static final int MESSAGE_PAUSED = 1;
    public static final int MESSAGE_STOPPED = 2;
    public static final int MESSAGE_DOWNLOADED = 3;
    public static final int MESSAGE_INIT = 4;

    Activity activity;
    AlertDialog.Builder dialogBuilder;
    Dialog dialog;
    NumberProgressBar loadingProgressBar;
    TextView textViewLoadingProgress, textViewLoadingFileName;

    public DownloadingHandler(Activity activity, AlertDialog.Builder dialogBuilder) {
        this.activity = activity;
        this.dialogBuilder = dialogBuilder;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Downloadable downloadable = (Downloadable) msg.obj;
        switch (msg.what) {
            case MESSAGE_INIT: {
                View dialogContentView = View.inflate(activity.getApplicationContext(), R.layout.dialog_loading_content, null);
                dialogBuilder.setView(dialogContentView);
                dialogBuilder.create();
                dialog = dialogBuilder.show();

                loadingProgressBar = (NumberProgressBar) dialogContentView.findViewById(R.id.pb_loading);
                textViewLoadingProgress = (TextView) dialogContentView.findViewById(R.id.tv_loading_progress);
                textViewLoadingFileName = (TextView) dialogContentView.findViewById(R.id.tv_loading_filename);

                loadingProgressBar.setMax(100);
                textViewLoadingFileName.setText(downloadable.getTitle());
                break;
            }
            case MESSAGE_IN_PROGRESS: {
                int percentDownloaded = (int)((downloadable.getBytesRead() * 100f) / downloadable.getTotalSize());
                loadingProgressBar.setProgress(percentDownloaded);
                textViewLoadingProgress.setText(downloadable.getBytesRead() + "/" + downloadable.getTotalSize() + " bytes");
                break;
            }
            case MESSAGE_PAUSED: {
                Toast.makeText(activity.getApplicationContext(), R.string.toast_file_paused, Toast.LENGTH_SHORT).show();
                break;
            }
            case MESSAGE_DOWNLOADED: {
                dialog.dismiss();
                Toast.makeText(activity.getApplicationContext(), R.string.toast_file_downloaded, Toast.LENGTH_SHORT).show();
                break;
            }
            case MESSAGE_STOPPED: {
                Toast.makeText(activity.getApplicationContext(), R.string.toast_file_stopped, Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
