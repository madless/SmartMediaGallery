package com.example.student.smartmediagallery.core.container;

import android.content.Context;

import com.example.student.smartmediagallery.core.manager.PurchaseManager;
import com.example.student.smartmediagallery.core.provider.InternalFileProvider;

public class Container {

    private Context context;
    private static Container instance;

    private InternalFileProvider fileProvider;

    private ParserContainer parserContainer;

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

    public ParserContainer getParserContainer() {
        if(parserContainer == null) {
            parserContainer = ParserContainer.getInstance(context);
        }
        return parserContainer;
    }

    public PurchaseManager getPurchaseManager() {
        if(purchaseManager == null) {
            purchaseManager = new PurchaseManager(context);
        }
        return purchaseManager;
    }
}
