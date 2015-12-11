package com.example.student.smartmediagallery.provider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;

public class InternalFileProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.student.smartmediagallery.internal_file_provider";
    private UriMatcher uriMatcher;

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "audio/*", 1);
        return true;
    }

    @Nullable
    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode) throws FileNotFoundException {
        switch (uriMatcher.match(uri)) {
            case 1:
                String fileLocation = getContext().getFilesDir() + File.separator + uri.getLastPathSegment();
                ParcelFileDescriptor sound = ParcelFileDescriptor.open(new File(fileLocation), ParcelFileDescriptor.MODE_READ_ONLY);
                return sound;
            default:
                throw new FileNotFoundException("Unsupported uri: " + uri.toString());
        }
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        MatrixCursor c = null;
        String fileLocation = getContext().getFilesDir() + File.separator + uri.getLastPathSegment();
        File file = new File(fileLocation);
        long time = System.currentTimeMillis();
        c = new MatrixCursor(new String[] { "_id", "_data", "orientation", "mime_type", "datetaken", "_display_name" });
        c.addRow(new Object[] { 0,  file, 0, "audio/mp3", time, uri.getLastPathSegment() });
        return c;
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
