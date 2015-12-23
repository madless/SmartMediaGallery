package com.example.student.smartmediagallery.core.listener;

import android.view.View;

public interface OnMediaItemClickListener {
    void onMediaItemClick(View view, int position);
    void onMediaItemLongClick(View view, int position);
}
