package com.example.student.smartmediagallery.ui.handler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.model.Downloadable;

public class MediaDownloadingHandler extends DownloadingHandler {
    AlertDialog.Builder dialogBuilder;
    Dialog dialog;
    NumberProgressBar loadingProgressBar;
    TextView textViewLoadingProgress, textViewLoadingFileName;

    public MediaDownloadingHandler(Context context, AlertDialog.Builder dialogBuilder) {
        super(context);
        this.dialogBuilder = dialogBuilder;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Downloadable downloadable = (Downloadable) msg.obj;
        switch (msg.what) {
            case MESSAGE_INIT: {
                View dialogContentView = View.inflate(context.getApplicationContext(), R.layout.dialog_loading_content, null);
                dialogBuilder.setView(dialogContentView);
                dialogBuilder.create();
                dialog = dialogBuilder.show();
                Log.d("mylog", "dialog show!");

                loadingProgressBar = (NumberProgressBar) dialogContentView.findViewById(R.id.pb_loading);
                textViewLoadingProgress = (TextView) dialogContentView.findViewById(R.id.tv_loading_progress);
                textViewLoadingFileName = (TextView) dialogContentView.findViewById(R.id.tv_loading_filename);

                loadingProgressBar.setMax(100);
                textViewLoadingFileName.setText(downloadable.getTitle());
                break;
            }
            case MESSAGE_IN_PROGRESS: {
                Log.d("myupd", "handleMessage - MESSAGE_IN_PROGRESS");
                int percentDownloaded = (int)((downloadable.getBytesRead() * MAX_PROGRESS) / (float)downloadable.getTotalSize());
                loadingProgressBar.setProgress(percentDownloaded);
                textViewLoadingProgress.setText(downloadable.getBytesRead() + "/" + downloadable.getTotalSize() + " bytes");
                break;
            }
            case MESSAGE_PAUSED: {
                Toast.makeText(context.getApplicationContext(), R.string.toast_file_paused, Toast.LENGTH_SHORT).show();
                break;
            }
            case MESSAGE_DOWNLOADED: {
                dialog.dismiss();
                Toast.makeText(context.getApplicationContext(), R.string.toast_file_downloaded, Toast.LENGTH_SHORT).show();
                break;
            }
            case MESSAGE_STOPPED: {
                Toast.makeText(context.getApplicationContext(), R.string.toast_file_stopped, Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
