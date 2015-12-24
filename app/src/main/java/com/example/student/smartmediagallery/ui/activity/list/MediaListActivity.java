package com.example.student.smartmediagallery.ui.activity.list;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.student.smartmediagallery.R;
import com.example.student.smartmediagallery.adapter.MediaListAdapter;
import com.example.student.smartmediagallery.core.constants.TransferConstant;
import com.example.student.smartmediagallery.core.container.Container;
import com.example.student.smartmediagallery.core.listener.OnMediaItemClickListener;
import com.example.student.smartmediagallery.core.listener.PurchaseObserver;
import com.example.student.smartmediagallery.core.listener.RecyclerItemClickListener;
import com.example.student.smartmediagallery.core.manager.PurchaseManager;
import com.example.student.smartmediagallery.core.model.MediaItem;
import com.example.student.smartmediagallery.core.policy.PurchaseModeProxy;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.List;

/**
 * Created by student on 09.12.2015.
 */
public abstract class MediaListActivity extends AppCompatActivity implements PurchaseObserver, OnMediaItemClickListener {
    private ProgressDialog progressDialog;
    protected Container container;
    protected PurchaseManager purchaseManager;
    protected PurchaseModeProxy purchaseModeProxy;
    protected Button buttonPurchase;
    protected View header;

    protected RecyclerView recyclerView;
    protected List<MediaItem> mediaItems;
    protected MediaListAdapter mediaListAdapter;
    protected ImageLoader imageLoader;
    protected DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.media_list_activity);
        recyclerView = (RecyclerView) findViewById(R.id.icons_recycle_list);
        options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .showImageOnLoading(R.mipmap.ic_loading).build();
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(this));

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(itemAnimator);
        recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerView, this));


        header = findViewById(R.id.purchase_header);
        buttonPurchase = (Button) header.findViewById(R.id.button_purchase);
        buttonPurchase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                purchaseManager.purchase();
            }
        });

        container = Container.getInstance(this);
        purchaseManager = container.getPurchaseManager();
        purchaseModeProxy = new PurchaseModeProxy(purchaseManager);

        if(container.getPurchaseManager().isPurchased()) {
            header.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.navigable_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent mediaIntent;
        switch (item.getItemId()) {
            case R.id.menu_item_photo: {
                mediaIntent = new Intent(this, PhotoListActivity.class);
                startActivity(mediaIntent);
                return true;
            }
            case R.id.menu_item_music: {
                mediaIntent = new Intent(this, SoundListActivity.class);
                startActivity(mediaIntent);
                return true;
            }
            case R.id.menu_item_video: {
                mediaIntent = new Intent(this, VideoListActivity.class);
                startActivity(mediaIntent);
                return true;
            }
            default: {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public void onMediaItemLongClick(View view, int position) {
        RemoveMediaDialogFragment removeMediaDialogFragment = new RemoveMediaDialogFragment();
        Bundle arg = new Bundle();
        arg.putInt(TransferConstant.CURRENT_MEDIA_POS.toString(), position);
        removeMediaDialogFragment.setArguments(arg);
        removeMediaDialogFragment.show(getSupportFragmentManager(), "tag");
    }

    @SuppressLint("ValidFragment")
    public class RemoveMediaDialogFragment extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final int pos = getArguments().getInt(TransferConstant.CURRENT_MEDIA_POS.toString());;
            AlertDialog.Builder ad = new AlertDialog.Builder(MediaListActivity.this);
            ad.setMessage(R.string.dialog_media_removing_message);
            ad.setPositiveButton(R.string.dialog_media_removing_remove_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mediaItems.remove(pos);
                    // may be you need to uncomment this if something go wrong with removing
                    //recyclerView.removeViewAt(pos);
                    mediaListAdapter.notifyItemRemoved(pos);
                    //mediaListAdapter.notifyItemRangeChanged(pos, mediaItems.size());
                    //mediaListAdapter.notifyDataSetChanged();
                    Toast.makeText(MediaListActivity.this, R.string.toast_media_removed_message, Toast.LENGTH_SHORT).show();
                }
            });
            ad.setNegativeButton(R.string.dialog_cancel_button, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {}
            });
            return ad.create();
        }
    }

    @Override
    public void onPreparePurchasing() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle(getString(R.string.progress_dialog_purchase_header));
        progressDialog.setMessage(getString(R.string.progress_dialog_purchase_text));
        progressDialog.setIndeterminate(false);
        progressDialog.setCancelable(false);
        progressDialog.show();
        Log.d("mylog", "progress dialog is showing");
    }

    @Override
    public void onPurchased() {
        Toast.makeText(this, R.string.toast_purchase_success, Toast.LENGTH_SHORT).show();
        progressDialog.dismiss();
        header.setVisibility(View.GONE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        purchaseManager.subscribe(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        purchaseManager.unsubscribe(this);
    }
}
