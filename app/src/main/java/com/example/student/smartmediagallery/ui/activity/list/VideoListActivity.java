package com.example.student.smartmediagallery.ui.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.core.constants.TransferConstant;
import com.example.student.smartmediagallery.core.model.SoundItem;
import com.example.student.smartmediagallery.core.model.VideoItem;
import com.example.student.smartmediagallery.core.parser.VideoContentXmlParser;
import com.example.student.smartmediagallery.ui.activity.player.VideoPlayerActivity;

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
        if(purchaseModeProxy.isAvailableVideo(position, (VideoItem) mediaItems.get(position))) {
            Intent videoPlayerIntent = new Intent(this, VideoPlayerActivity.class);
            videoPlayerIntent.putExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), position);
            ArrayList<? extends Parcelable> parcelableVideos = new ArrayList<>((ArrayList<? extends Parcelable>) mediaItems);
            videoPlayerIntent.putParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString(), parcelableVideos);
            startActivity(videoPlayerIntent);
        } else {
            Toast.makeText(this, "Buy full version to unlock this video", Toast.LENGTH_SHORT).show();
        }

    }
}
