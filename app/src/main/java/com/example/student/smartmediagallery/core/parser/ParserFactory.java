package com.example.student.smartmediagallery.core.parser;

import android.content.Context;

import com.example.student.smartmediagallery.core.constants.ParserType;
import com.example.student.smartmediagallery.core.manager.ParserManager;

import java.io.InputStream;

/**
 * Created by student on 24.12.2015.
 */
public class ParserFactory {
    private Context context;
    private static ParserFactory instance;
    private ParserManager parserManager;

    public static ParserFactory getInstance() {
        if(instance == null) {
            instance = new ParserFactory();
        }
        return instance;
    }

    public void prepareFactory(Context context) {
        this.context = context;
        parserManager = new ParserManager(context);
    }

    public MediaContentXmlParser createParserByType(ParserType parserType) {
        InputStream inputStream = parserManager.getStreamByParserType(parserType);
        MediaContentXmlParser parser = null;
        switch (parserType) {
            case PHOTO_PARSER: {
                parser = new PhotoContentXmlParser(inputStream);
                break;
            }
            case SOUND_PARSER: {
                parser = new SoundContentXmlParser(inputStream);
                break;
            }
            case VIDEO_PARSER: {
                parser = new VideoContentXmlParser(inputStream);
                break;
            }
        }
        return parser;
    }

}
