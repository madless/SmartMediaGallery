package com.example.student.smartmediagallery.core.container;

import android.content.Context;

import com.example.student.smartmediagallery.core.constants.ParserType;
import com.example.student.smartmediagallery.core.parser.PhotoContentXmlParser;
import com.example.student.smartmediagallery.core.parser.SoundContentXmlParser;
import com.example.student.smartmediagallery.core.parser.VideoContentXmlParser;
import com.example.student.smartmediagallery.core.manager.ParserManager;

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
        parserManager = new ParserManager(context);
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
        return parserManager;
    }

    public VideoContentXmlParser getVideoParser() {
//        if(videoParser == null) {
//            InputStream inputStream = getParserManager().getStreamByParserType(ParserType.VIDEO_PARSER);
//            videoParser = new VideoContentXmlParser(inputStream);
//        }
//        return videoParser;
        InputStream inputStream = getParserManager().getStreamByParserType(ParserType.VIDEO_PARSER);
        return new VideoContentXmlParser(inputStream);
    }

    public SoundContentXmlParser getSoundParser() {
//        if(soundParser == null) {
//            InputStream inputStream = getParserManager().getStreamByParserType(ParserType.SOUND_PARSER);
//            soundParser = new SoundContentXmlParser(inputStream);
//        }
//        return soundParser;
        InputStream inputStream = getParserManager().getStreamByParserType(ParserType.SOUND_PARSER);
        return new SoundContentXmlParser(inputStream);
    }

    public PhotoContentXmlParser getPhotoParser() {
//        if(photoParser == null) {
//            InputStream inputStream = getParserManager().getStreamByParserType(ParserType.PHOTO_PARSER);
//            photoParser = new PhotoContentXmlParser(inputStream);
//        }
//        return photoParser;
        InputStream inputStream = getParserManager().getStreamByParserType(ParserType.PHOTO_PARSER);
        return new PhotoContentXmlParser(inputStream);
    }
}
