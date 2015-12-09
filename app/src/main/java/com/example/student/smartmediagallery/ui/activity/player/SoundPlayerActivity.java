package com.example.student.smartmediagallery.ui.activity.player;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.Constants;
import com.example.student.smartmediagallery.model.SoundItem;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;

/**
 * Created by student on 09.12.2015.
 */
public class SoundPlayerActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private ImageButton btnReplay, btnPlay, btnPause, btnStop, btnSongPrev, btnSongNext, btnRewind, btnForward;
    private SeekBar sbMusic;
    private TextView tvMusicHeader, tvTimer;
    private String soundUrl, title;
    private int duration;
    private boolean stoped;
    private int soundPosition;
    private final Handler handler = new Handler();
    ArrayList<SoundItem> sounds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_player);
        PlayerButtonsOnClickListener btnListener = new PlayerButtonsOnClickListener();
        btnReplay = (ImageButton) findViewById(R.id.btnReplay);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);
        btnPause = (ImageButton) findViewById(R.id.btnPause);
        btnStop = (ImageButton) findViewById(R.id.btnStop);
        btnSongPrev = (ImageButton) findViewById(R.id.btnSongPrev);
        btnSongNext = (ImageButton) findViewById(R.id.btnSongNext);
        btnRewind = (ImageButton) findViewById(R.id.btnRewind);
        btnForward = (ImageButton) findViewById(R.id.btnForward);
        sbMusic = (SeekBar) findViewById(R.id.sbMusic);
        tvMusicHeader = (TextView) findViewById(R.id.tvMusicHeader);
        tvTimer = (TextView) findViewById(R.id.tvTimer);

        btnReplay.setOnClickListener(btnListener);
        btnPlay.setOnClickListener(btnListener);
        btnPause.setOnClickListener(btnListener);
        btnStop.setOnClickListener(btnListener);
        btnSongPrev.setOnClickListener(btnListener);
        btnSongNext.setOnClickListener(btnListener);
        btnRewind.setOnClickListener(btnListener);
        btnForward.setOnClickListener(btnListener);

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
                case R.id.btnReplay: {
                    mediaPlayer.stop();
                    sbMusic.setProgress(0);
                    mediaPlayer.prepareAsync();
                    break;
                }
                case R.id.btnPlay: {
                    if(stoped){
                        stoped = false;
                        mediaPlayer.prepareAsync();
                    } else {
                        if (!mediaPlayer.isPlaying()) {
                            mediaPlayer.seekTo(sbMusic.getProgress());
                            mediaPlayer.start();
                            startPlayProgressUpdater();
                        }
                    }
                    break;
                }
                case R.id.btnPause: {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                    }
                    break;
                }
                case R.id.btnStop: {
                    mediaPlayer.stop();
                    stoped = true;
                    sbMusic.setProgress(0);
                    tvTimer.setText(getTimeStr(mediaPlayer.getCurrentPosition()));
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
            }
        }
    }

    class PlayerOnPreparedListener implements MediaPlayer.OnPreparedListener {
        @Override
        public void onPrepared(MediaPlayer fFmpegMediaPlayer) {
            sbMusic.setMax(mediaPlayer.getDuration());
            mediaPlayer.start();
            mediaPlayer.seekTo(0);
            startPlayProgressUpdater();
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
        Time t = new Time(time);
        String min = "" + t.getMinutes();
        String sec = "" + t.getSeconds();
        return (min.length() > 1 ? min : "0" + min) + ":" + (sec.length() > 1 ? sec : "0" + sec);
    }

    public void startPlayProgressUpdater() {
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
