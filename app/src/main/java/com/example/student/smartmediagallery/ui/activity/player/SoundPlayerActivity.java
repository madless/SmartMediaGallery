package com.example.student.smartmediagallery.ui.activity.player;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.example.student.smartmediagallery.net.ProgressFileLoader;
import com.example.student.smartmediagallery.net.ProgressFileLoaderBasedOnUrlConnection;
import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.Constants;
import com.example.student.smartmediagallery.model.SoundItem;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by student on 09.12.2015.
 */
public class SoundPlayerActivity extends AppCompatActivity{
    private MediaPlayer mediaPlayer;
    private ImageButton btnReplay, btnPlay, btnStop, btnSongPrev, btnSongNext, btnRewind, btnForward, btnDownload;
    private SeekBar sbMusic;
    private TextView tvMusicHeader, tvTimer;
    private String soundUrl, title;
    private boolean stoped;
    private int soundPosition;
    private final Handler handler = new Handler();
    private ArrayList<SoundItem> sounds;
    private AlertDialog.Builder alertDialog;
    private NumberProgressBar progressBar;
    private View dialogContentView;
    private Dialog d;
    private TextView tvLoadingFilename, tvLoadingProgress;
    private ProgressFileLoaderBasedOnUrlConnection loader;
    private long bytesRead;
    private boolean isDownloadingStopped = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_player);

        PlayerButtonsOnClickListener btnListener = new PlayerButtonsOnClickListener();
        //btnReplay = (ImageButton) findViewById(R.id.btnReplay);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnSongPrev = (ImageButton) findViewById(R.id.btnSongPrev);
        btnSongNext = (ImageButton) findViewById(R.id.btnSongNext);
        btnRewind = (ImageButton) findViewById(R.id.btnRewind);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        btnDownload = (ImageButton) findViewById(R.id.btnDownload);
        sbMusic = (SeekBar) findViewById(R.id.sbMusic);
        tvMusicHeader = (TextView) findViewById(R.id.tvMusicHeader);
        tvTimer = (TextView) findViewById(R.id.tvTimer);

        //btnReplay.setOnClickListener(btnListener);
        btnPlay.setOnClickListener(btnListener);
        btnStop.setOnClickListener(btnListener);
        btnSongPrev.setOnClickListener(btnListener);
        btnSongNext.setOnClickListener(btnListener);
        btnRewind.setOnClickListener(btnListener);
        btnForward.setOnClickListener(btnListener);
        btnDownload.setOnClickListener(btnListener);

        alertDialog = new AlertDialog.Builder(SoundPlayerActivity.this);
        alertDialog.setMessage(R.string.dialog_loading_title);
        alertDialog.setPositiveButton(R.string.dialog_loading_pause_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                loader.cancel();
            }
        });
        alertDialog.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                bytesRead = 0;
                isDownloadingStopped = true;
                File f = new File(getFilesDir().getAbsolutePath() + "/" + sounds.get(soundPosition).getTitle());
                f.delete();
                loader.cancel();

            }
        });

        alertDialog.setView(dialogContentView);
        alertDialog.create();

        soundPosition = getIntent().getIntExtra(Constants.CURRENT_MEDIA_POS.toString(), 0);
        sounds = getIntent().getParcelableArrayListExtra(Constants.MEDIA_LIST.toString());
        soundUrl = sounds.get(soundPosition).getSoundUrl();
        title = sounds.get(soundPosition).getTitle();

        tvMusicHeader.setText(title);

        mediaPlayer = getPreparedMediaPlayer(soundUrl);

        sbMusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.seekTo(sbMusic.getProgress());
                }
                return false;
            }
        });

        mediaPlayer.prepareAsync();

    }

    public MediaPlayer getPreparedMediaPlayer(String soundUrl) {
        MediaPlayer mediaPlayer = new MediaPlayer();
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
//                case R.id.btnReplay: {
//                    mediaPlayer.stop();
//                    sbMusic.setProgress(0);
//                    mediaPlayer.prepareAsync();
//                    break;
//                }
                case R.id.btnPlay: {
                    if(stoped){
                        stoped = false;
                        mediaPlayer.prepareAsync();
                    } else {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(sbMusic.getProgress());
                            mediaPlayer.start();
                            startPlayProgressUpdater();
                            btnPlay.setImageResource(R.drawable.ic_action_pause);
                        } else {
                            mediaPlayer.pause();
                            btnPlay.setImageResource(R.drawable.ic_action_play);
                        }
                    }
                    break;
                }
                case R.id.btnStop: {
                    mediaPlayer.stop();
                    stoped = true;
                    sbMusic.setProgress(0);
                    tvTimer.setText(getTimeStr(0));
                    btnPlay.setImageResource(R.drawable.ic_action_play);
                    break;
                }
                case R.id.btnSongPrev: {
                    if(soundPosition != 0) {
                        soundPosition--;
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = getPreparedMediaPlayer(sounds.get(soundPosition).getSoundUrl());
                        sbMusic.setProgress(0);
                        title = sounds.get(soundPosition).getTitle();
                        tvMusicHeader.setText(title);

                        mediaPlayer.prepareAsync();
                    }
                    break;
                }
                case R.id.btnSongNext: {
                    if(soundPosition != sounds.size()) {
                        soundPosition++;
                        mediaPlayer.stop();
                        mediaPlayer.release();
                        mediaPlayer = getPreparedMediaPlayer(sounds.get(soundPosition).getSoundUrl());
                        sbMusic.setProgress(0);
                        title = sounds.get(soundPosition).getTitle();
                        tvMusicHeader.setText(title);
                        mediaPlayer.prepareAsync();
                    }
                    break;
                }
                case R.id.btnRewind: {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 3000);
                    break;
                }
                case R.id.btnForward: {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 3000);
                    break;
                }
                case R.id.btnDownload: {
                    dialogContentView = View.inflate(getApplicationContext(), R.layout.dialog_loading_content, null);
                    alertDialog.setView(dialogContentView);
                    alertDialog.create();
                    d = alertDialog.show();
                    progressBar = (NumberProgressBar) dialogContentView.findViewById(R.id.pb_loading);
                    progressBar.setMax(100);
                    tvLoadingFilename = (TextView) dialogContentView.findViewById(R.id.tv_loading_filename);
                    tvLoadingProgress = (TextView) dialogContentView.findViewById(R.id.tv_loading_progress);
                    tvLoadingFilename.setText(sounds.get(soundPosition).getTitle());
                    DownloadingTask downloadingTask = new DownloadingTask();
                    if(!isDownloadingStopped){
                        downloadingTask.execute(sounds.get(soundPosition).getSoundUrl(), getFilesDir().getAbsolutePath() + "/" + sounds.get(soundPosition).getTitle(), String.valueOf(bytesRead));
                    } else {
                        downloadingTask.execute(sounds.get(soundPosition).getSoundUrl(), getFilesDir().getAbsolutePath() + "/" + sounds.get(soundPosition).getTitle(), String.valueOf(0));
                    }
                    isDownloadingStopped = false;
                }
            }
        }
    }

    class PlayerOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            sbMusic.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();
            mediaPlayer.seekTo(0);
            startPlayProgressUpdater();
            btnPlay.setImageResource(R.drawable.ic_action_pause);
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mediaPlayer.stop();
        stoped = true;
        mediaPlayer.release();
    }
    public String getTimeStr(int time){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));
        String min = "" + calendar.get(Calendar.MINUTE);
        String sec = "" + calendar.get(Calendar.SECOND);
        return (min.length() > 1 ? min : "0" + min) + ":" + (sec.length() > 1 ? sec : "0" + sec);
    }
    public void startPlayProgressUpdater() {
        if(mediaPlayer.isLooping()) {
            sbMusic.setProgress(mediaPlayer.getCurrentPosition());
            tvTimer.setText(getTimeStr(mediaPlayer.getCurrentPosition()));

            if (mediaPlayer.isPlaying()) {
                Runnable notification = new Runnable() {
                    public void run() {
                        startPlayProgressUpdater();
                    }
                };
                handler.postDelayed(notification, 1000);
            }else{
                if(stoped){
                    sbMusic.setProgress(0);
                }
            }
        }
    }
    @Override
    protected void onStop() {
        super.onStop();
        mediaPlayer.release();
    }
    public class DownloadingTask extends AsyncTask<String, Long, Void> implements ProgressFileLoader.LoaderListener{
        String url;
        String targetPath;
        long totalSize;
        long bytesReadInStart;
        @Override
        protected Void doInBackground(String... params) {
            url = params[0];
            targetPath = params[1];
            bytesRead = Long.valueOf(params[2]);
            bytesReadInStart = Long.valueOf(params[2]);
            loader = new ProgressFileLoaderBasedOnUrlConnection(url, targetPath, bytesRead);
            loader.setProgressListener(this);
            try {
                loader.download();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Long... values) {
            bytesRead = values[0];
            totalSize = values[1] + bytesReadInStart;
            int percentDownloaded = (int)((bytesRead * 100f) / totalSize);
            progressBar.setProgress(percentDownloaded);
            tvLoadingProgress.setText(bytesRead + "/" + totalSize + " bytes");
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            if(bytesRead == totalSize) {
                Toast.makeText(getApplicationContext(), "DOWNLOADED", Toast.LENGTH_SHORT).show();
            }
        }
        @Override
        public void onTotalSizeCalculated(long totalSize) {}
        @Override
        public void onTotalSizeFetched(long totalSize) {}
        @Override
        public void onProgressUpdated(long totalSize, long readSize) {
            publishProgress(readSize, totalSize);
        }
        @Override
        public void onDownloadComplete() {
            bytesRead = 0;
        }
    }
}
