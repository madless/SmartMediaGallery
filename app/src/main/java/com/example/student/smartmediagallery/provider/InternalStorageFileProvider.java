package com.example.student.smartmediagallery.provider;

import java.io.File;
import java.io.FileNotFoundException;

import android.app.Application;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class InternalStorageFileProvider extends ContentProvider {
    //private final Logger logger = Logger.getLogger(InternalStorageFileProvider.class.getSimpleName());

    private static final String CLASS_NAME = "InternalStorageFileProvider";

    // The authority is the symbolic name for the provider class
    public static final String AUTHORITY = "com.example.student.smartmediagallery.internal_storage_provider";

    public static final String CONTENT_TYPE_SOUNDBOARD = "ringtone";
    public static final String CONTENT_COMPONENTS_DEVIDER = "___";

    // UriMatcher used to match against incoming requests
    private UriMatcher uriMatcher;

    private void send() {
        Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
        emailIntent.setType("application/audio");
        //emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{});
        //emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Test Subject");
        //emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "From My App");
        //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(("content://" + "com.example.student.smartmediagallery.Music" + Service.getFileName(myMediaPlayer.getCurrentSound().getSoundAddress()))));
    }

    @Override
    public boolean onCreate() {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        // Add a URI to the matcher which will match against the form
        // 'content://com.stephendnicholas.gmailattach.provider/*'
        // and return 1 in the case that the incoming Uri matches this pattern
        uriMatcher.addURI(AUTHORITY, "*", 1);

        return true;
    }

    public static String[] convertFileNameIntoComponents(String path) {
        return path.split(CONTENT_COMPONENTS_DEVIDER);
    }

//    public static Uri createUriForSoundboardRingtone(Application application, ExtrasSoundboardItem soundboardItem) {
//        Uri uri = Uri.parse("content://" + AUTHORITY + "/"
//                + CONTENT_TYPE_SOUNDBOARD + CONTENT_COMPONENTS_DEVIDER
//                + application.getAppId() + CONTENT_COMPONENTS_DEVIDER
//                + FileUtils.getFileNameFromFilePath(soundboardItem.getSoundUrl()));
//
//        return uri;
//    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {

        String LOG_TAG = CLASS_NAME + " - openFile";

        // Check incoming Uri against the matcher
        switch (uriMatcher.match(uri)) {

            // If it returns 1 - then it matches the Uri defined in onCreate
            case 1: {
                try {
                    ParcelFileDescriptor pfd = null;
                    String path = null;
                    String[] components = convertFileNameIntoComponents(uri.getLastPathSegment());

                    //logger.error("components: " + components);

                    if (components[0].equals(CONTENT_TYPE_SOUNDBOARD)) {
                        path = getSoundboardPath(components[1], components[2]);
                    }

                    //logger.error("path: " + path);

                    if (path != null) {
                        pfd = ParcelFileDescriptor.open(new File(path), ParcelFileDescriptor.MODE_READ_ONLY);
                    }

                    if (pfd != null) {
                        return pfd;
                    }
                } catch (Exception e) {
                    //logger.error("error: " + e);

                    e.printStackTrace();
                }
            }
            default:
                Log.v(LOG_TAG, "Unsupported uri: '" + uri + "'.");
        }

        throw new FileNotFoundException("Unsupported uri: "
                + uri.toString());
    }

    private String getSoundboardPath(String appId, String ringtoneName) throws FileNotFoundException {
//        Application application = Container.getInstance().getDaoContainer().getApplicationDAO().get(Integer.parseInt(appId));
//
//        String path = Container.getInstance().getResourcesManager().getExtrasSoundboardSoundsPath(application) + "/" + ringtoneName;
    return null;
        //return path;
    }

    // //////////////////////////////////////////////////////////////
    // Not supported / used
    // //////////////////////////////////////////////////////////////

    @Override
    public int update(Uri uri, ContentValues contentvalues, String s,
                      String[] as) {
        return 0;
    }

    @Override
    public int delete(Uri uri, String s, String[] as) {
        return 0;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentvalues) {
        return null;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String s, String[] as1,
                        String s1) {
        return null;
    }
}
