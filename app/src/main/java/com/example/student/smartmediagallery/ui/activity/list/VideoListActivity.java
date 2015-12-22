package com.example.student.smartmediagallery.ui.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.parser.VideoContentXmlParser;
import com.example.student.smartmediagallery.ui.activity.player.VideoPlayerActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class VideoListActivity extends MediaListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoContentXmlParser videoContentXmlParser = parserContainer.getVideoParser();
        mediaItems = videoContentXmlParser.getMediaList();
        mediaListAdapter = new MediaListAdapter(mediaItems, imageLoader, options);
        recyclerView.setAdapter(mediaListAdapter);
    }

    @Override
    public void onMediaItemClick(View view, int position) {
        Intent videoPlayerIntent = new Intent(this, VideoPlayerActivity.class);
        videoPlayerIntent.putExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), position);
        ArrayList<? extends Parcelable> parcelableVideos = new ArrayList<>((ArrayList<? extends Parcelable>) mediaItems);
        videoPlayerIntent.putParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString(), parcelableVideos);
        startActivity(videoPlayerIntent);
    }
}
