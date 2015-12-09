package com.example.student.smartmediagallery.parser;

import com.example.student.smartmediagallery.model.MediaItem;
import com.example.student.smartmediagallery.model.PhotoItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 07.12.2015.
 */

public class PhotoContentXmlParser extends MediaContentXmlParser{
    private final String MEDIA_URL_TAG = "photoURL";

    public List<MediaItem> getListOfMediaByXml(InputStream stream) {
        try {
            reader = new BufferedReader(new InputStreamReader(stream, "UTF8"));
            XmlPullParser xpp = prepareParser();
            xpp.setInput(reader);

            String lastEventElementName = "";
            String currentIconUrl = "";
            String currentPhotoUrl = "";

            while(xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_TAG: {
                        lastEventElementName = xpp.getName();
                        break;
                    }
                    case XmlPullParser.TEXT: {
                        switch (lastEventElementName) {
                            case MEDIA_ICON_TAG: {
                                currentIconUrl = xpp.getText();
                                break;
                            }
                            case MEDIA_URL_TAG: {
                                currentPhotoUrl = xpp.getText();
                                break;
                            }
                        }
                        break;
                    }
                    case XmlPullParser.END_TAG: {
                        if(xpp.getName().equals(MEDIA_URL_TAG)) {
                            PhotoItem photo = new PhotoItem(currentIconUrl, currentPhotoUrl);
                            mediaItems.add(photo);
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
        return mediaItems;
    }
}