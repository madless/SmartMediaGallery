package com.example.student.smartmediagallery.ui.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.model.ListItem;
import com.example.student.smartmediagallery.parser.SoundContentXmlParser;
import com.example.student.smartmediagallery.ui.activity.player.SoundPlayerActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by student on 09.12.2015.
 */
public class SoundListActivity extends MediaListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SoundContentXmlParser soundContentXmlParser = new SoundContentXmlParser();
        InputStream soundContentStream = null;
        try {
            soundContentStream = getAssets().open("SoundBoardContent.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaItems = soundContentXmlParser.getListOfMediaByXml(soundContentStream);
        mediaListAdapter = new MediaListAdapter(mediaItems, imageLoader, options);
        recyclerView.setAdapter(mediaListAdapter);
    }

    @Override
    public void onMediaItemClick(View view, int position) {
        Intent soundPlayerIntent = new Intent(this, SoundPlayerActivity.class);
        soundPlayerIntent.putExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), position);
        ArrayList<? extends Parcelable> parcelableSounds = new ArrayList<>((ArrayList<? extends Parcelable>) mediaItems);
        soundPlayerIntent.putParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString(), parcelableSounds);
        startActivity(soundPlayerIntent);
    }

}
