package com.example.student.smartmediagallery.ui.activity.list;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.constants.Constants;
import com.example.student.smartmediagallery.parser.SoundContentXmlParser;
import com.example.student.smartmediagallery.parser.VideoContentXmlParser;
import com.example.student.smartmediagallery.service.DownloadingService;
import com.example.student.smartmediagallery.ui.activity.list.MediaListActivity;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by student on 09.12.2015.
 */
public class VideoListActivity extends MediaListActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VideoContentXmlParser videoContentXmlParser = new VideoContentXmlParser();
        InputStream videoContentStream = null;
        try {
            videoContentStream = getAssets().open("VideoContent.xml");
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaItems = videoContentXmlParser.getListOfMediaByXml(videoContentStream);
        mediaListAdapter = new MediaListAdapter(mediaItems, imageLoader, options);
        recyclerView.setAdapter(mediaListAdapter);

    }

    class VideoDownloadingReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(Constants.STATUS.toString(), -1);
            int position = intent.getIntExtra(Constants.CURRENT_MEDIA_POS.toString(), -1);
            View root = recyclerView.getChildAt(position);
            ImageView imageViewDownloadingStatus = (ImageView) root.findViewById(R.id.imageViewDownloadingSatus);

            switch (status) {
                case DownloadingService.STATUS_OK: {
                    imageViewDownloadingStatus.setImageResource(R.mipmap.ic_play_saved);
                    Toast.makeText(getApplicationContext(), "Downloaded successfully!", Toast.LENGTH_SHORT).show();
                    break;
                }
                case DownloadingService.STATUS_PAUSED: {
                    break;
                }
                case DownloadingService.STATUS_FAILED: {
                    imageViewDownloadingStatus.setImageResource(R.mipmap.ic_save);
                    Toast.makeText(getApplicationContext(), "FAILED!", Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    }

    @Override
    public void onMediaItemClick(View view, int position) {
        View root = recyclerView.getChildAt(position);
        ImageView imageViewDownloadingStatus = (ImageView) root.findViewById(R.id.imageViewDownloadingSatus);

        if(imageViewDownloadingStatus.getTag() == null) {
            imageViewDownloadingStatus.setTag(R.mipmap.ic_save);
        }

        switch ((Integer)imageViewDownloadingStatus.getTag()) {
            case R.mipmap.ic_save: {
                imageViewDownloadingStatus.setImageResource(R.mipmap.ic_wait);
                imageViewDownloadingStatus.setTag(R.mipmap.ic_wait);

                Intent downloadingServiceIntent = new Intent(this, DownloadingService.class);
                downloadingServiceIntent.putExtra(Constants.CURRENT_MEDIA.toString(), mediaItems.get(position));
                downloadingServiceIntent.putExtra(Constants.CURRENT_MEDIA_POS.toString(), position);
                startService(downloadingServiceIntent);
                Log.d("mylog", "launch service!");
                break;
            }
            case R.mipmap.ic_wait: {
                Toast.makeText(getApplicationContext(), "Downloading...", Toast.LENGTH_SHORT).show();
                imageViewDownloadingStatus.setImageResource(R.mipmap.ic_play_saved);
                imageViewDownloadingStatus.setTag(R.mipmap.ic_play_saved);
                break;
            }
            case R.mipmap.ic_play_saved: {
                Toast.makeText(getApplicationContext(), "Playing", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}
