package com.example.student.smartmediagallery.container;

import android.content.Context;
import android.provider.MediaStore;

import com.example.student.smartmediagallery.constants.ParserType;
import com.example.student.smartmediagallery.parser.PhotoContentXmlParser;
import com.example.student.smartmediagallery.parser.SoundContentXmlParser;
import com.example.student.smartmediagallery.parser.VideoContentXmlParser;
import com.example.student.smartmediagallery.resource.ParserManager;

import java.io.InputStream;

/**
 * Created by student on 22.12.2015.
 */
public class ParserContainer {

    private Context context;
    private static ParserContainer instance;

    ParserManager parserManager;

    VideoContentXmlParser videoParser;
    SoundContentXmlParser soundParser;
    PhotoContentXmlParser photoParser;

    private ParserContainer(Context context) {
        this.context = context;
    }

    public static ParserContainer getInstance(Context context) {
        if(instance == null) {
            instance = new ParserContainer(context);
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public ParserManager getParserManager() {
        if(parserManager == null) {
            parserManager = new ParserManager(context);
        }
        return parserManager;
    }

    public VideoContentXmlParser getVideoParser() {
        if(videoParser == null) {
            InputStream inputStream = getParserManager().getStreamByParserType(ParserType.VIDEO_PARSER);
            videoParser = new VideoContentXmlParser(inputStream);
        }
        return videoParser;
    }

    public SoundContentXmlParser getSoundParser() {
        if(soundParser == null) {
            InputStream inputStream = getParserManager().getStreamByParserType(ParserType.SOUND_PARSER);
            soundParser = new SoundContentXmlParser(inputStream);
        }
        return soundParser;
    }

    public PhotoContentXmlParser getPhotoParser() {
        if(photoParser == null) {
            InputStream inputStream = getParserManager().getStreamByParserType(ParserType.PHOTO_PARSER);
            photoParser = new PhotoContentXmlParser(inputStream);
        }
        return photoParser;
    }
}
