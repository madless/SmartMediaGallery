package com.example.student.smartmediagallery.ui.activity.pager;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.adapter.PhotoPagerAdapter;
import com.example.student.smartmediagallery.animation.AccordionTransformer;
import com.example.student.smartmediagallery.core.constants.TransferConstant;
import com.example.student.smartmediagallery.core.model.PhotoItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by student on 08.12.2015.
 */
public class PhotoPagerActivity extends FragmentActivity {
    private ViewPager viewPager;
    private List<PhotoItem> photoItems;
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_pager);
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher).build();
                //.showImageOnLoading(R.mipmap.ic_loading).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));
        int pos = getIntent().getIntExtra(TransferConstant.CURRENT_MEDIA_POS.toString(), 0);

        photoItems = getIntent().getParcelableArrayListExtra(TransferConstant.MEDIA_LIST.toString());

        viewPager = (ViewPager)findViewById(R.id.pager);
        PhotoPagerAdapter adapter = new PhotoPagerAdapter(getSupportFragmentManager(), photoItems, imageLoader, options);
        viewPager.setAdapter(adapter);
        viewPager.setSaveEnabled(false);
        viewPager.setCurrentItem(pos);
        viewPager.setPageTransformer(true, new AccordionTransformer());
    }
}
