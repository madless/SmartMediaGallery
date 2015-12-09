package com.example.student.smartmediagallery.ui.activity.list;

import android.os.Bundle;
import android.view.View;

import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.parser.SoundContentXmlParser;
import com.example.student.smartmediagallery.parser.VideoContentXmlParser;
import com.example.student.smartmediagallery.ui.activity.list.MediaListActivity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by student on 09.12.2015.
 */
public class VideoListActivity extends MediaListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoContentXmlParser videoContentXmlParser = new VideoContentXmlParser();
        InputStream videoContentStream = null;
        try {
            videoContentStream = getAssets().open("VideoContent.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaItems = videoContentXmlParser.getListOfMediaByXml(videoContentStream);
        mediaListAdapter = new MediaListAdapter(mediaItems, imageLoader, options);
        recyclerView.setAdapter(mediaListAdapter);
    }

    @Override
    public void onMediaItemClick(View view, int position) {

    }

}
