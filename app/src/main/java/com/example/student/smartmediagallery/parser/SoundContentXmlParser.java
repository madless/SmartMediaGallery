package com.example.student.smartmediagallery.parser;

import android.util.Log;

import com.example.student.smartmediagallery.model.MediaItem;
import com.example.student.smartmediagallery.model.PhotoItem;
import com.example.student.smartmediagallery.model.SoundItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 09.12.2015.
 */
public class SoundContentXmlParser extends MediaContentXmlParser {
    private final String MEDIA_URL_TAG = "soundURL";

    @Override
    public List<MediaItem> getListOfMediaByXml(InputStream stream) {
        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF8"));
            XmlPullParser xpp = prepareParser();
            xpp.setInput(reader);

            String lastEventElementName = "";
            String currentTitle = "";
            String currentSoundUrl = "";

            while(xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG: {
                        lastEventElementName = xpp.getName();
                        break;
                    }
                    case XmlPullParser.TEXT: {
                        switch (lastEventElementName) {
                            case MEDIA_TITLE_TAG: {
                                currentTitle = xpp.getText();
                                break;
                            }
                            case MEDIA_URL_TAG: {
                                currentSoundUrl = xpp.getText();
                                break;
                            }
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if(xpp.getName().equals(MEDIA_URL_TAG)) {
                            SoundItem sound = new SoundItem(currentTitle, currentSoundUrl);
                            mediaItems.add(sound);
                        } else {
                            lastEventElementName = "";
                        }
                        break;
                    }
                }
                xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Log.d("mylog", mediaItems.toString());
        return mediaItems;
    }
}
