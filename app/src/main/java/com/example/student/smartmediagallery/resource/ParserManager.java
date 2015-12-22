package com.example.student.smartmediagallery.resource;

import android.content.Context;

import com.example.student.smartmediagallery.constants.ParserType;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by student on 22.12.2015.
 */
public class ParserManager {
    private String PHOTO_CONTENT = "PhotoContent.xml";
    private String SOUND_CONTENT = "SoundBoardContent.xml";
    private String VIDEO_CONTENT = "VideoContent.xml";

    Context context;

    public ParserManager(Context context) {
        this.context = context;
    }

    public InputStream getStreamByParserType(ParserType parserType) {
        try {
            switch (parserType) {
                case PHOTO_PARSER: {
                    return context.getAssets().open(PHOTO_CONTENT);
                }
                case SOUND_PARSER: {
                    return context.getAssets().open(SOUND_CONTENT);
                }
                case VIDEO_PARSER: {
                    return context.getAssets().open(VIDEO_CONTENT);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
