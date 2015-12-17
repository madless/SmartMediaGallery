package com.example.student.smartmediagallery.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;

import com.example.student.smartmediagallery.resource.ResourceManager;

import java.io.File;
import java.io.FileNotFoundException;

public class InternalFileProvider extends ContentProvider {
    private UriMatcher uriMatcher;
    private ResourceManager resourceManager;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(ProviderContract.AUTHORITY, ProviderContract.SOUND_DIR + ProviderContract.ALL_IN_DIR, 1);
        return true;
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        resourceManager = new ResourceManager(getContext());
        switch (uriMatcher.match(uri)) {
            case 1:
                String fileLocation = resourceManager.getItemPathByUri(uri);
                ParcelFileDescriptor sound = ParcelFileDescriptor.open(new File(fileLocation), ParcelFileDescriptor.MODE_READ_ONLY);
                return sound;
            default:
                throw new FileNotFoundException("Unsupported uri: " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
