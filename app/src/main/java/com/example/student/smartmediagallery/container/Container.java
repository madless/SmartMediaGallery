package com.example.student.smartmediagallery.container;

import android.content.Context;

import com.example.student.smartmediagallery.provider.InternalFileProvider;

public class Container {

    private Context context;
    private static Container instance;

    private InternalFileProvider fileProvider;

    private ParserContainer parserContainer;

    private Container(Context context) {
        this.context = context;
    }

    public static Container getInstance(Context context) {
        if(instance == null) {
            instance = new Container(context);
        }
        return instance;
    }

    public Context getContext() {
        return context;
    }

    public InternalFileProvider getFileProvider() {
        if(fileProvider == null) {
            fileProvider = new InternalFileProvider();
        }
        return fileProvider;
    }

    public ParserContainer getParserContainer() {
        if(parserContainer == null) {
            parserContainer = ParserContainer.getInstance(context);
        }
        return parserContainer;
    }
}
