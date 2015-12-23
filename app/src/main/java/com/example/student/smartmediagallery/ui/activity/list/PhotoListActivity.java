package com.example.student.smartmediagallery.ui.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.core.constants.TransferConstant;
import com.example.student.smartmediagallery.core.model.PhotoItem;
import com.example.student.smartmediagallery.core.parser.PhotoContentXmlParser;
import com.example.student.smartmediagallery.ui.activity.pager.PhotoPagerActivity;

import java.util.ArrayList;

public class PhotoListActivity extends MediaListActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PhotoContentXmlParser photoContentXmlParser = parserContainer.getPhotoParser();
        mediaItems = photoContentXmlParser.getMediaList();
        mediaListAdapter = new MediaListAdapter(mediaItems, imageLoader, options);
        recyclerView.setAdapter(mediaListAdapter);
    }

    @Override
    public void onMediaItemClick(View view, int position) {
        if(purchaseModeProxy.isAvailablePhoto(position, (PhotoItem) mediaItems.get(position))) {
            Intent photoPagerIntent = new Intent(PhotoListActivity.this, PhotoPagerActivity.class);
            photoPagerIntent.putExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), position);
            ArrayList<? extends Parcelable> parcelablePhotos = new ArrayList<>((ArrayList<? extends Parcelable>) mediaItems);
            photoPagerIntent.putParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString(), parcelablePhotos);
            startActivity(photoPagerIntent);
        } else {
            Toast.makeText(this, "Buy full version to unlock this photo", Toast.LENGTH_SHORT).show();
        }
    }

}