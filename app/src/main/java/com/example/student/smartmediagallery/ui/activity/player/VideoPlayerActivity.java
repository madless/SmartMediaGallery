package com.example.student.smartmediagallery.ui.activity.player;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.core.constants.MessageEvent;
import com.example.student.smartmediagallery.core.constants.TransferConstant;
import com.example.student.smartmediagallery.core.model.Downloadable;
import com.example.student.smartmediagallery.core.model.VideoItem;
import com.example.student.smartmediagallery.core.manager.ResourceManager;
import com.example.student.smartmediagallery.service.MultiDownloaderService;
import com.example.student.smartmediagallery.ui.handler.DownloadingHandler;
import com.example.student.smartmediagallery.ui.handler.MediaDownloadingHandler;

import java.io.File;
import java.util.ArrayList;

//import com.example.student.smartmediagallery.service.DownloaderService;

public class VideoPlayerActivity extends AppCompatActivity {

    private AlertDialog.Builder alertDialogBuilder;
    private Downloadable downloadable;
    private DownloadingHandler downloadingHandler;
    boolean isServiceBounded;
    MultiDownloaderService multiDownloaderService;
    DownloaderServiceConnection downloaderServiceConnection;

    DownloadManagerReceiver downloadManagerReceiver;
    IntentFilter intentFilter;

    ProgressDialog pDialog;
    VideoView videoview;
    ArrayList<VideoItem> videos;
    int position;
    ResourceManager resourceManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoview = (VideoView) findViewById(R.id.VideoView);

        position = getIntent().getIntExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), 0);
        videos = getIntent().getParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString());

        pDialog = new ProgressDialog(this);
        pDialog.setTitle(videos.get(position).getTitle());
        pDialog.setMessage(getString(R.string.progress_dialog_video_text));
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(videoview);
            Uri video = Uri.parse(videos.get(position).getUrl());
            videoview.setMediaController(mediacontroller);
            videoview.setVideoURI(video);

        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }

        videoview.requestFocus();
        videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            public void onPrepared(MediaPlayer mp) {
                pDialog.dismiss();
                videoview.start();
            }
        });

        resourceManager = new ResourceManager(getApplicationContext());
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("mylog", "onStartVideoPlayerActivity");
        alertDialogBuilder = new AlertDialog.Builder(VideoPlayerActivity.this);
        alertDialogBuilder.setMessage(R.string.dialog_loading_title);
        alertDialogBuilder.setPositiveButton(R.string.dialog_loading_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alertDialogBuilder.setNeutralButton(R.string.dialog_loading_pause_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pause();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                stop();
            }
        });

        downloadable =  videos.get(position);
        downloadingHandler = new MediaDownloadingHandler(this, alertDialogBuilder);
        downloaderServiceConnection = new DownloaderServiceConnection();

        intentFilter = new IntentFilter(getString(R.string.action_receiver_download_manager));
        downloadManagerReceiver = new DownloadManagerReceiver();
        registerReceiver(downloadManagerReceiver, intentFilter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.downloadable_menu, menu);
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        unregisterReceiver(downloadManagerReceiver);
        if(isServiceBounded) {
            unbindService(downloaderServiceConnection);
            isServiceBounded = false;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.downloadable_menu_item_download: {
                load();
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    public class DownloaderServiceConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("mylog", "onServiceConnected with manager!");
            MultiDownloaderService.MultiDownloaderServiceBinder downloaderServiceBinder = (MultiDownloaderService.MultiDownloaderServiceBinder) service;
            multiDownloaderService = downloaderServiceBinder.getService();
            isServiceBounded = true;
            if(!multiDownloaderService.isActive(downloadable.getUrl())) {
                multiDownloaderService.startLoading(downloadable.getUrl()); //downloadable.getUrl()
            }
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("mylog", "onServiceDisconnected with manager!");
            isServiceBounded = false;
        }
    }

    public class DownloadManagerReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(downloadable.getUrl().equals(intent.getStringExtra(TransferConstant.MEDIA_URL.toString()))) {
                MessageEvent event = MessageEvent.valueOf(intent.getStringExtra(TransferConstant.MESSAGE_EVENT.toString()));
                switch (event) {
                    case MESSAGE_INIT: {
                        long totalSize = intent.getLongExtra(TransferConstant.TOTAL_SIZE.toString(), -1);
                        downloadable.setTotalSize(totalSize);
                        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_INIT, downloadable);
                        downloadingHandler.sendMessage(message);
                        Log.d("mylog", "init received");
                        break;
                    }
                    case MESSAGE_IN_PROGRESS: {
                        long readSize = intent.getLongExtra(TransferConstant.READ_SIZE.toString(), -1);
                        if (!(readSize >= downloadable.getTotalSize())) {
                            downloadable.setBytesRead(readSize);
                            Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_IN_PROGRESS, downloadable);
                            downloadingHandler.sendMessage(message);
                        }
                        break;
                    }
                    case MESSAGE_DOWNLOADED: {
                        if (isServiceBounded) {
                            unbindService(downloaderServiceConnection);
                            isServiceBounded = false;
                        }
                        Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_DOWNLOADED, downloadable);
                        downloadingHandler.sendMessage(message);
                        downloadable.setBytesRead(0);
                        break;
                    }
                    default: {
                        break;
                    }
                }
            }
        }
    }

    public void load() {
        downloadable.setTargetPath(new File(resourceManager.getDownloadablePath(downloadable)));
        Intent downloaderServiceIntent = new Intent(this, MultiDownloaderService.class);
        downloaderServiceIntent.putExtra(TransferConstant.CURRENT_MEDIA.toString(), downloadable);
        startService(downloaderServiceIntent);
        bindService(downloaderServiceIntent, downloaderServiceConnection, Context.BIND_AUTO_CREATE);
    }

    public void pause() {
        if(isServiceBounded) {
            multiDownloaderService.pauseLoading(downloadable.getUrl());
            Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_PAUSED, downloadable);
            downloadingHandler.sendMessage(message);
            if(isServiceBounded) {
                unbindService(downloaderServiceConnection);
                isServiceBounded = false;
            }
        }
    }

    public void stop() {
        if(isServiceBounded) {
            multiDownloaderService.stopLoading(downloadable.getUrl());
            if(isServiceBounded){
                unbindService(downloaderServiceConnection);
                isServiceBounded = false;
            }
            downloadable.setBytesRead(0);
            downloadable.getTargetPath().delete();
            Message message = downloadingHandler.obtainMessage(DownloadingHandler.MESSAGE_STOPPED, downloadable);
            downloadingHandler.sendMessage(message);
        }
    }
}
