package com.example.student.smartmediagallery.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.constants.Constants;
import com.example.student.smartmediagallery.model.PhotoItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.w3c.dom.Text;

/**
 * Created by student on 08.12.2015.
 */
public class PhotoPageFragment extends Fragment {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;

    public void init(ImageLoader imageLoader, DisplayImageOptions options) {
        this.imageLoader = imageLoader;
        this.options = options;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_photo_on_page);
        TextView tvPostionInfo = (TextView) view.findViewById(R.id.tv_position_info);
        TextView tvPhotoTitle = (TextView) view.findViewById(R.id.tv_photo_title);

        PhotoItem photoItem = getArguments().getParcelable(Constants.CURRENT_MEDIA.toString());
        int position = getArguments().getInt(Constants.CURRENT_MEDIA_POS.toString());
        int length = getArguments().getInt(Constants.MEDIA_LIST_LENGTH.toString());

        imageLoader.displayImage(photoItem.getPhotoUrl(), iv, options);
        tvPostionInfo.setText("" + (position + 1) + "/" + length);
        tvPhotoTitle.setText(photoItem.getTitle());
        return view;
    }
}
