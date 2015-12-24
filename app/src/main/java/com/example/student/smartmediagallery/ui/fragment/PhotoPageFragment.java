package com.example.student.smartmediagallery.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.core.constants.TransferConstant;
import com.example.student.smartmediagallery.core.container.Container;
import com.example.student.smartmediagallery.core.manager.PurchaseManager;
import com.example.student.smartmediagallery.core.model.PhotoItem;
import com.example.student.smartmediagallery.core.policy.PurchaseModeProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by student on 08.12.2015.
 */
public class PhotoPageFragment extends Fragment {
    private ImageLoader imageLoader;
    private DisplayImageOptions options;
    private Container container;
    private PurchaseManager purchaseManager;
    protected PurchaseModeProxy purchaseModeProxy;

    public void init(ImageLoader imageLoader, DisplayImageOptions options) {
        this.imageLoader = imageLoader;
        this.options = options;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        container = Container.getInstance(getContext());
        purchaseManager = container.getPurchaseManager();
        purchaseModeProxy = new PurchaseModeProxy(purchaseManager);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo_page, null);
        ImageView iv = (ImageView) view.findViewById(R.id.iv_photo_on_page);
        TextView tvPostionInfo = (TextView) view.findViewById(R.id.tv_position_info);
        TextView tvPhotoTitle = (TextView) view.findViewById(R.id.tv_photo_title);

        PhotoItem photoItem = getArguments().getParcelable(TransferConstant.CURRENT_MEDIA.toString());
        int position = getArguments().getInt(TransferConstant.CURRENT_MEDIA_POS.toString());
        int length = getArguments().getInt(TransferConstant.MEDIA_LIST_LENGTH.toString());

        if(purchaseModeProxy.isAvailablePhoto(position, photoItem)) {
            imageLoader.displayImage(photoItem.getUrl(), iv, options);
        } else {
            imageLoader.displayImage("drawable://" + R.drawable.locked_content, iv, options);
            //Toast.makeText(getContext(), R.string.toast_locked_photo_info, Toast.LENGTH_SHORT).show();
        }
        tvPostionInfo.setText("" + (position + 1) + "/" + length);
        tvPhotoTitle.setText(photoItem.getTitle());
        return view;
    }
}
