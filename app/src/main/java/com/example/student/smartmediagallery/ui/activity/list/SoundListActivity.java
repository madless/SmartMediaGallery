package com.example.student.smartmediagallery.ui.activity.list;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Toast;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.core.constants.ParserType;
import com.example.student.smartmediagallery.core.constants.TransferConstant;
import com.example.student.smartmediagallery.core.model.PhotoItem;
import com.example.student.smartmediagallery.core.model.SoundItem;
import com.example.student.smartmediagallery.core.parser.ParserFactory;
import com.example.student.smartmediagallery.core.parser.PhotoContentXmlParser;
import com.example.student.smartmediagallery.core.parser.SoundContentXmlParser;
import com.example.student.smartmediagallery.ui.activity.player.SoundPlayerActivity;

import java.util.ArrayList;

/**
 * Created by student on 09.12.2015.
 */
public class SoundListActivity extends MediaListActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ParserFactory parserFactory = container.getParserFactory();
        parserFactory.prepareFactory(this);
        SoundContentXmlParser soundContentXmlParser = (SoundContentXmlParser) parserFactory.createParserByType(ParserType.SOUND_PARSER);
        mediaItems = soundContentXmlParser.getMediaList();
        mediaListAdapter = new MediaListAdapter(mediaItems, imageLoader, options);
        recyclerView.setAdapter(mediaListAdapter);
    }

    @Override
    public void onMediaItemClick(View view, int position) {
        if(purchaseModeProxy.isAvailableSound(position, (SoundItem) mediaItems.get(position))) {
            Intent soundPlayerIntent = new Intent(this, SoundPlayerActivity.class);
            soundPlayerIntent.putExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), position);
            ArrayList<? extends Parcelable> parcelableSounds = new ArrayList<>((ArrayList<? extends Parcelable>) mediaItems);
            soundPlayerIntent.putParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString(), parcelableSounds);
            startActivity(soundPlayerIntent);
        } else {
            Toast.makeText(this, R.string.toast_purchase_offer_sound, Toast.LENGTH_SHORT).show();
        }

    }

}
