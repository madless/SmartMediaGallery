package com.example.student.smartmediagallery.ui.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;

import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.parser.PhotoContentXmlParser;
import com.example.student.smartmediagallery.ui.activity.pager.PhotoPagerActivity;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class PhotoListActivity extends MediaListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhotoContentXmlParser photoContentXmlParser = new PhotoContentXmlParser();
        InputStream photoContentStream = null;
        try {
            photoContentStream = getAssets().open("PhotoContent.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaItems = photoContentXmlParser.getListOfMediaByXml(photoContentStream);
        mediaListAdapter = new MediaListAdapter(mediaItems, imageLoader, options);
        recyclerView.setAdapter(mediaListAdapter);
    }

    @Override
    public void onMediaItemClick(View view, int position) {
        Intent photoPagerIntent = new Intent(PhotoListActivity.this, PhotoPagerActivity.class);
        photoPagerIntent.putExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), position);
        ArrayList<? extends Parcelable> parcelablePhotos = new ArrayList<>((ArrayList<? extends Parcelable>) mediaItems);
        photoPagerIntent.putParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString(), parcelablePhotos);
        startActivity(photoPagerIntent);
    }

}