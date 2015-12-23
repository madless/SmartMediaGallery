package com.example.student.smartmediagallery.core.parser;

import com.example.student.smartmediagallery.core.model.MediaItem;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by student on 09.12.2015.
 */
public abstract class MediaContentXmlParser {
    protected final String MEDIA_TITLE_TAG = "title";
    protected final String MEDIA_ICON_TAG = "iconURL";
    protected List<MediaItem> mediaItems = new ArrayList<>();
    protected BufferedReader reader;
    protected InputStream inputStream;

    public MediaContentXmlParser(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    protected XmlPullParser prepareParser() {
        XmlPullParser xpp = null;
        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            xpp = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return xpp;
    }

    protected abstract List<MediaItem> getMediaList();
}
