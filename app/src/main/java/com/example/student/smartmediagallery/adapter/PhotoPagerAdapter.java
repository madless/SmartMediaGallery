package com.example.student.smartmediagallery.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;

import com.example.student.smartmediagallery.constants.TransferConstant;
import com.example.student.smartmediagallery.model.PhotoItem;
import com.example.student.smartmediagallery.ui.fragment.PhotoPageFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class PhotoPagerAdapter extends FragmentStatePagerAdapter {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private List<PhotoItem> photoItems;

    public PhotoPagerAdapter(FragmentManager fragmentManager, List<PhotoItem> photoItems, ImageLoader imageLoader, DisplayImageOptions options) {
        super(fragmentManager);
        this.photoItems = photoItems;
        this.imageLoader = imageLoader;
        this.options = options;
    }

    @Override
    public Fragment getItem(int index) {
        PhotoPageFragment fragment = new PhotoPageFragment();
        fragment.init(imageLoader, options);
        Bundle bundle = new Bundle();
        bundle.putParcelable(TransferConstant.CURRENT_MEDIA.toString(), photoItems.get(index));
        bundle.putInt(TransferConstant.CURRENT_MEDIA_POS.toString(), index);
        bundle.putInt(TransferConstant.MEDIA_LIST_LENGTH.toString(), photoItems.size());

        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public int getItemPosition(Object object){
        return PagerAdapter.POSITION_NONE;
    }

    @Override
    public int getCount() {
        return photoItems.size();
    }
}
