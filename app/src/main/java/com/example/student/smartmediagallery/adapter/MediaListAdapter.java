package com.example.student.smartmediagallery.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.model.MediaItem;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.List;

public class MediaListAdapter extends RecyclerView.Adapter<MediaListAdapter.ViewHolder> {
    List<MediaItem> mediaItems;
    ImageLoader imageLoader;
    DisplayImageOptions options;


    public MediaListAdapter(List<MediaItem> mediaItems, ImageLoader imageLoader, DisplayImageOptions options) {
        this.mediaItems = mediaItems;
        this.imageLoader = imageLoader;
        this.options = options;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media_list_item, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MediaItem mediaItem = mediaItems.get(position);
        holder.getText().setText(mediaItem.getTitle());
        imageLoader.displayImage(mediaItem.getIconUrl(), holder.getIcon(), options);
    }

    @Override
    public int getItemCount() {
        return mediaItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView text;
        public ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            text = (TextView) itemView.findViewById(R.id.tv_item_text);
            icon = (ImageView) itemView.findViewById(R.id.iv_item_icon);
        }

        public TextView getText() {
            return text;
        }

        public ImageView getIcon() {
            return icon;
        }

    }
}
