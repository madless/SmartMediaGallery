package com.example.student.smartmediagallery.core.container;

import android.content.Context;

import com.example.student.smartmediagallery.core.manager.PurchaseManager;
import com.example.student.smartmediagallery.core.parser.ParserFactory;
import com.example.student.smartmediagallery.core.provider.InternalFileProvider;

public class Container {

    private Context context;
    private static Container instance;

    private InternalFileProvider fileProvider;

    private ParserFactory parserFactory;

    private PurchaseManager purchaseManager;

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

    public ParserFactory getParserFactory() {
        if(parserFactory == null) {
            parserFactory = ParserFactory.getInstance();
        }
        return parserFactory;
    }

    public PurchaseManager getPurchaseManager() {
        if(purchaseManager == null) {
            purchaseManager = new PurchaseManager(context);
        }
        return purchaseManager;
    }
}
