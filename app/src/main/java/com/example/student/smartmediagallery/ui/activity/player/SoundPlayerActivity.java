package com.example.student.smartmediagallery.ui.activity.player;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.student.smartmediagallery.model.Downloadable;
import com.example.student.smartmediagallery.net.Downloader;
import com.example.student.smartmediagallery.provider.ResourceManager;
import com.example.student.smartmediagallery.ui.handler.DownloadingHandler;
import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.Constants;
import com.example.student.smartmediagallery.model.SoundItem;
import com.example.student.smartmediagallery.ui.handler.SoundDownloadingHandler;
import com.example.student.smartmediagallery.provider.ProviderContract;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SoundPlayerActivity extends AppCompatActivity{
    private MediaPlayer mediaPlayer;
    private ImageButton btnPlay, btnStop, btnDownload, btnSend;
    private SeekBar sbMusic;
    private TextView tvMusicHeader, tvTimer;
    private String soundUrl, title;
    private boolean stopped = true;
    private int soundPosition;
    private final Handler soundPlayerHandler = new Handler();
    private ArrayList<SoundItem> sounds;
    private AlertDialog.Builder alertDialogBuilder;
    private Runnable updator;
    private Downloadable downloadable;
    private DownloadingHandler downloadingHandler;
    private ExecutorService executorService;
    private Downloader downloader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_player);

        PlayerButtonsOnClickListener btnListener = new PlayerButtonsOnClickListener();

        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnDownload = (ImageButton) findViewById(R.id.btnDownload);
        btnSend = (ImageButton) findViewById(R.id.btnSend);
        sbMusic = (SeekBar) findViewById(R.id.sbMusic);
        tvMusicHeader = (TextView) findViewById(R.id.tvMusicHeader);
        tvTimer = (TextView) findViewById(R.id.tvTimer);

        btnPlay.setOnClickListener(btnListener);
        btnStop.setOnClickListener(btnListener);
        btnDownload.setOnClickListener(btnListener);
        btnSend.setOnClickListener(btnListener);

        alertDialogBuilder = new AlertDialog.Builder(SoundPlayerActivity.this);
        alertDialogBuilder.setMessage(R.string.dialog_loading_title);
        alertDialogBuilder.setPositiveButton(R.string.dialog_loading_pause_button, new DialogInterface.OnClickListener() {
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

        soundPosition = getIntent().getIntExtra(Constants.CURRENT_MEDIA_POS.toString(), 0);
        sounds = getIntent().getParcelableArrayListExtra(Constants.MEDIA_LIST.toString());
        soundUrl = sounds.get(soundPosition).getSoundUrl();
        title = sounds.get(soundPosition).getTitle();
        tvMusicHeader.setText(title);
        sbMusic.setOnTouchListener(new SoundProgressBarOnTouchListener());
        mediaPlayer = new MediaPlayer();

    }

    @Override
    protected void onStart() {
        super.onStart();
        ResourceManager resourceManager = new ResourceManager(getApplicationContext());
        SoundItem sound = sounds.get(soundPosition);
        String title = sound.getTitle();
        String url = sound.getSoundUrl();
        File targetPath = new File(resourceManager.getSoundItemPath(sound));
        downloadable = new Downloadable(title, url, targetPath);
        downloadingHandler = new SoundDownloadingHandler(this, alertDialogBuilder);
        executorService = Executors.newFixedThreadPool(1);
        downloader = new Downloader(downloadable, downloadingHandler);
    }

    public class SoundProgressBarOnTouchListener implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.seekTo(sbMusic.getProgress());
            }
            return false;
        }
    }
    public MediaPlayer initMediaPlayer(String soundUrl) {
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setOnPreparedListener(new PlayerOnPreparedListener());
        try {
            mediaPlayer.setDataSource(soundUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mediaPlayer;
    }

    class PlayerButtonsOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.btnPlay: {
                    if(stopped){
                        stopped = false;
                        mediaPlayer = initMediaPlayer(soundUrl);
                        mediaPlayer.prepareAsync();
                        btnPlay.setImageResource(R.drawable.ic_action_pause);
                    } else {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(sbMusic.getProgress());
                            mediaPlayer.start();
                            soundProgressUpdate();
                            btnPlay.setImageResource(R.drawable.ic_action_pause);
                        } else {
                            mediaPlayer.pause();
                            if(updator != null) {
                                soundPlayerHandler.removeCallbacks(updator);
                            }
                            btnPlay.setImageResource(R.drawable.ic_action_play);
                        }
                    }
                    break;
                }
                case R.id.btnStop: {
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    if(updator != null) {
                        soundPlayerHandler.removeCallbacks(updator);
                    }
                    stopped = true;
                    sbMusic.setProgress(0);
                    tvTimer.setText(getTimeStr(0));
                    btnPlay.setImageResource(R.drawable.ic_action_play);
                    break;
                }
                case R.id.btnDownload: {

                    executorService.execute(downloader);
                    break;
                }
                case R.id.btnSend: {
                    final Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.setType(ProviderContract.SOUND_DIR + ProviderContract.ALL_IN_DIR);
                    shareIntent.putExtra(Intent.EXTRA_STREAM, ProviderContract.getDownloadableUri(downloadable));
                    startActivity(shareIntent);
                    break;
                }
            }
        }
    }

    class PlayerOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.start();
            sbMusic.setMax(mediaPlayer.getDuration());
            soundProgressUpdate();
        }
    }

    public String getTimeStr(int time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        String min = "" + calendar.get(Calendar.MINUTE);
        String sec = "" + calendar.get(Calendar.SECOND);
        return (min.length() > 1 ? min : "0" + min) + ":" + (sec.length() > 1 ? sec : "0" + sec);
    }

    public void soundProgressUpdate() {
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                sbMusic.setProgress(mediaPlayer.getCurrentPosition());
                tvTimer.setText(getTimeStr(mediaPlayer.getCurrentPosition()));
                updator = new Runnable() {
                    public void run() {
                        soundProgressUpdate();
                    }
                };
                soundPlayerHandler.postDelayed(updator, 1000);
            } else {
                if(stopped){
                    sbMusic.setProgress(0);
                }
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
        downloadingHandler.removeCallbacks(downloader);
        soundPlayerHandler.removeCallbacks(updator);
    }

}
