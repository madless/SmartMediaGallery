package com.example.student.smartmediagallery.ui.activity.player;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.model.Downloadable;
import com.example.student.smartmediagallery.model.VideoItem;
import com.example.student.smartmediagallery.net.DownloadManager;
import com.example.student.smartmediagallery.provider.ResourceManager;
import com.example.student.smartmediagallery.ui.handler.DownloadingHandler;
import com.example.student.smartmediagallery.ui.handler.SoundDownloadingHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VideoPlayerActivity extends AppCompatActivity {

    private AlertDialog.Builder alertDialogBuilder;
    private Downloadable downloadable;
    private DownloadingHandler downloadingHandler;
    private ExecutorService executorService;
    private DownloadManager downloader;

    ProgressDialog pDialog;
    VideoView videoview;
    ArrayList<VideoItem> videos;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);
        videoview = (VideoView) findViewById(R.id.VideoView);

        position = getIntent().getIntExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), 0);
        videos = getIntent().getParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString());

        pDialog = new ProgressDialog(this);
        pDialog.setTitle(videos.get(position).getTitle());
        pDialog.setMessage("Buffering...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(videoview);
            Uri video = Uri.parse(videos.get(position).getVideoUrl());
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        alertDialogBuilder = new AlertDialog.Builder(VideoPlayerActivity.this);
        alertDialogBuilder.setMessage(R.string.dialog_loading_title);
        alertDialogBuilder.setPositiveButton(R.string.dialog_loading_ok_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //downloader.pause();
            }
        });
        alertDialogBuilder.setNeutralButton(R.string.dialog_loading_pause_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloader.pause();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                downloader.stop();
            }
        });

        ResourceManager resourceManager = new ResourceManager(getApplicationContext());
        VideoItem video = videos.get(position);
        String title = video.getTitle();
        String url = video.getVideoUrl();
        File targetPath = new File(resourceManager.getVideoItemPath(video));
        downloadable = new Downloadable(title, url, targetPath);
        downloadingHandler = new SoundDownloadingHandler(this, alertDialogBuilder);
        executorService = Executors.newFixedThreadPool(1);
        downloader = new DownloadManager(VideoPlayerActivity.this, downloadable, downloadingHandler);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.downloadable_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.downloadable_menu_item_download: {
                executorService.execute(downloader);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }
}
