package com.wb.vapps.mvc.views.impl.tablets.visitor;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import com.wb.vapps.container.Container;
import com.wb.vapps.encryption.MediaEncrypter;
import com.wb.vapps.encryption.factory.MediaEncrypterFactory;
import com.wb.vapps.hpfranchise.R;
import com.wb.vapps.localization.Localizer;
import com.wb.vapps.model.Application;
import com.wb.vapps.model.extras.items.ExtrasItem;
import com.wb.vapps.model.extras.items.ExtrasSoundboardItem;
import com.wb.vapps.model.extras.section.ExtrasSectionSoundboard;
import com.wb.vapps.mvc.views.adapters.ExtrasSoundboardItemsAdapter;
import com.wb.vapps.mvc.views.impl.utils.AlertDialogUtil;
import com.wb.vapps.network.loader.error.ServerError;
import com.wb.vapps.network.loader.queue.MediaQueue;
import com.wb.vapps.network.loader.queue.model.MediaItem;
import com.wb.vapps.network.loader.queue.observer.MediaItemListener;
import com.wb.vapps.resources.InternalStorageFileProvider;
import com.wb.vapps.resources.ResourcesManager;
import com.wb.vapps.resources.queue.container.MediaQueuesContainer;
import com.wb.vapps.resources.queue.model.ExtrasSoundboardMediaItem;
import com.wb.vapps.settings.LocalizeKeys;
import com.wb.vapps.utils.FileUtils;
import com.wb.vapps.utils.Logger;
import com.wb.vapps.utils.TextUtils;

public class ExtrasSoundboardSectionVisitor extends BaseExtrasSectionVisitor 
	implements OnItemClickListener, MediaItemListener, OnCompletionListener, OnClickListener {

	private final Logger logger = Logger.getLogger(ExtrasSoundboardSectionVisitor.class.getSimpleName());
	
	private Localizer localizer;
	private ResourcesManager resourceManager;
	private MediaQueue extrasSoundsMediaQueue;
	private MediaPlayer mediaPlayer;
	private ExtrasSoundboardItemsAdapter extrasSoundboardItemsAdapter;
	private ExtrasSoundboardItemsAdapter.ViewHolder viewHolder;
	private boolean sendMode = false;
	private Set<ExtrasSoundboardItem> sendItems = new HashSet<ExtrasSoundboardItem>();
	private MediaEncrypterFactory mediaEncrypterFactory;
	
	private Button sendButton;
	private Button sendSDButton;
	private Button cancelButton;
	
	private int[] animationResources = new int[]{R.drawable.audio_play_anim_0, R.drawable.audio_play_anim_1,
			R.drawable.audio_play_anim_2, R.drawable.audio_play_anim_3, R.drawable.audio_play_anim_4,
			R.drawable.audio_play_anim_5, R.drawable.audio_play_anim_6, R.drawable.audio_play_anim_7,
			R.drawable.audio_play_anim_8, R.drawable.audio_play_anim_9, R.drawable.audio_play_anim_10,
			R.drawable.audio_play_anim_11, R.drawable.audio_play_anim_12};
	
	private Drawable[] animationDrawables = new Drawable[animationResources.length];
			
	private Handler handler = new Handler();
		
	public ExtrasSoundboardSectionVisitor(Activity context,
			Application application, View contentView,
			List<ExtrasItem> extrasItems) {
		super(context, application, contentView, extrasItems);
		
		localizer = Container.getInstance().getLocalizer();
		resourceManager = Container.getInstance().getResourcesManager();
		mediaEncrypterFactory = Container.getInstance().getMediaEncrypterFactory();
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setOnCompletionListener(this);
		
		MediaQueuesContainer mediaQueuesContainer = Container.getInstance().getMediaQueuesContainer();
		extrasSoundsMediaQueue = mediaQueuesContainer.getExtrasSoundboardSoundsMediaQueue();
		
		Resources resources = context.getResources();
		for (int i = 0; i < animationResources.length; i++) {
			animationDrawables[i] = resources.getDrawable(animationResources[i]);
		}
	}
	
	@Override
	public void populateUI(ExtrasSectionSoundboard section) {
		super.populateUI(section);
		sendButton = (Button) contentView.findViewById(R.id.tab_content_button_save);
		if(sendButton != null) {
			sendButton.setOnClickListener(this);
		}
		
		sendSDButton = (Button) contentView.findViewById(R.id.tab_content_button_save_sd);
		if(sendSDButton != null) {
			sendSDButton.setOnClickListener(this);
		}
		
		cancelButton = (Button) contentView.findViewById(R.id.tab_content_button_cancel);
		if(cancelButton != null) {
			cancelButton.setText(localizer.getLocalizedString(LocalizeKeys.SOUNDBOARD_CANCEL_BUTTON_TITLES, application));
			cancelButton.setOnClickListener(this);
		}
		
		updateSoundboardPanel();
				
		GridView gridView = (GridView) contentView.findViewById(R.id.tab_content_gridview);
		if(gridView != null) {
			extrasSoundboardItemsAdapter = new ExtrasSoundboardItemsAdapter(context, application, extrasItems, sendMode); 
			gridView.setAdapter(extrasSoundboardItemsAdapter);
			gridView.setOnItemClickListener(this);
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		ExtrasSoundboardItem soundboardItem = (ExtrasSoundboardItem) extrasItems.get(position);
		if(!purchaseModePolicy.shouldExtrasItemBeEnabled(application, soundboardItem)) {
			return;
		}
		
		ExtrasSoundboardItemsAdapter.ViewHolder viewHolder = 
			(ExtrasSoundboardItemsAdapter.ViewHolder) view.getTag();
		
		if(resourceManager.isExtrasSoundboardSoundExists(application, soundboardItem) &&
				!extrasSoundsMediaQueue.containItemForUrl(soundboardItem.getSoundUrl())) {
			if(sendMode) {
				if(sendItems.contains(soundboardItem)) {
					viewHolder.itemTap.setVisibility(View.VISIBLE);
					sendItems.remove(soundboardItem);
				} else {
					viewHolder.itemTap.setVisibility(View.INVISIBLE);
					sendItems.add(soundboardItem);
				}
			} else {
				stopAnimation(viewHolder);
				playSound(soundboardItem);
			}
		} else {
			if (Container.getInstance().getConnectionManager().isInternetAvailable()) {
				if(!extrasSoundsMediaQueue.containItemForUrl(soundboardItem.getSoundUrl())) {
					ExtrasSoundboardMediaItem mediaItem = new ExtrasSoundboardMediaItem(soundboardItem, application, 
							this, viewHolder, soundboardItem.getSoundUrl(), 
							resourceManager.getExtrasSoundboardSoundsPath(application), this);
					extrasSoundsMediaQueue.pushMediaItem(mediaItem);
					viewHolder.loadingIndicator.setVisibility(View.VISIBLE);
				}					
			} else {
				AlertDialogUtil.showNoConnectionAlert(application, context);
			}
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.tab_content_button_save:
			if(!sendMode) {
				stopAnimation(null);
				stopSound();
			} else {
				if (Container.getInstance().getConnectionManager().isInternetAvailable()) {
					if(!sendItems.isEmpty()) {
						Intent intent = new Intent(Intent.ACTION_SEND_MULTIPLE);
						intent.setType("plain/text");
						intent.putExtra(Intent.EXTRA_SUBJECT, localizer.getLocalizedString(
								sendItems.size() < 2 ? LocalizeKeys.SOUNDBOARD_RINGTONE_MAIL_SUBJECT : 
									LocalizeKeys.SOUNDBOARD_RINGTONES_MAIL_SUBJECT, application));
						ArrayList<Uri> sendUris = new ArrayList<Uri>();
						for (ExtrasSoundboardItem soundboardItem : sendItems) {
							String path = resourceManager.getExtrasSoundboardSoundPath(
									application, soundboardItem);
							
							Uri sendUri = null;
							if (resourceManager.isSdCardAvailable()) {
								File file = new File(path);
								sendUri = Uri.fromFile(file);
							}
							else {								
								sendUri = InternalStorageFileProvider.createUriForSoundboardRingtone(application, soundboardItem);
							}							
							 
							sendUris.add(sendUri);
							
						}
						intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, sendUris);
						context.startActivity(intent);
					}
				} else {
					AlertDialogUtil.showNoConnectionAlert(application, context);
				}
			}
			break;
		case R.id.tab_content_button_save_sd:
			if(!sendItems.isEmpty()) {
				MediaEncrypter encrypter = null;
				if(mediaEncrypterFactory != null) {
					encrypter = mediaEncrypterFactory.createMediaEncrypter();
				}
				
				File ringtonesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_RINGTONES);
				String toPath = ringtonesDir.getAbsolutePath();
				
				for (ExtrasSoundboardItem soundboardItem : sendItems) {
					String fromPath = resourceManager.getExtrasSoundboardSoundPath(application, soundboardItem);
					
					String result = FileUtils.copyFileToDir(fromPath, toPath, false, encrypter);
					if(!TextUtils.isEmpty(result)) {
						MediaScannerConnection.scanFile(context, new String[]{result}, null, null);
					}
				}
				
				Toast toast = Toast.makeText(context, 
						localizer.getLocalizedString(LocalizeKeys.RINGTONES_WAS_SAVED_TEXT, application), 
						Toast.LENGTH_SHORT);
				toast.show();
			}
			break;
		}
		
		if(sendMode) {
			sendItems.clear();
		}
		
		sendMode = !sendMode;
		updateSoundboardPanel();
		extrasSoundboardItemsAdapter.setSendMode(sendMode);
		extrasSoundboardItemsAdapter.notifyDataSetChanged();
	}
	
	@Override
	public void stopAdapter() {
		extrasSoundsMediaQueue.removeAllMediaItemsForDelegate(this);
		if(mediaPlayer.isPlaying()) {
			mediaPlayer.stop();
		}
		mediaPlayer.release();
	}

	@Override
	public void onMediaItemDownloaded(final MediaItem mediaItem) {
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				ExtrasSoundboardItemsAdapter.ViewHolder viewHolder = 
					(ExtrasSoundboardItemsAdapter.ViewHolder) mediaItem.getPayload();
				viewHolder.loadingIndicator.setVisibility(View.INVISIBLE);
				
				ExtrasSoundboardMediaItem soundboardMediaItem = (ExtrasSoundboardMediaItem) mediaItem;
				if(sendMode) {
					viewHolder.itemTap.setVisibility(View.INVISIBLE);
					sendItems.add(soundboardMediaItem.getExtrasSoundboardItem());
				} else {
					stopAnimation(viewHolder);
					playSound(soundboardMediaItem.getExtrasSoundboardItem());
				}
			}
		});
	}

	@Override
	public void onMediaItemFailedToDownload(MediaItem mediaItem, ServerError serverError) {}
	
	@Override
	public void onCompletion(MediaPlayer arg0) {
		stopAnimation(null);
	}
	
	private void playSound(ExtrasSoundboardItem soundboardItem) {
		try {
			stopSound();
			
			if (resourceManager.isExtrasSoundboardSoundExists(application, soundboardItem)) {
				String path = resourceManager.getExtrasSoundboardSoundPath(application, soundboardItem);
				
				logger.debug("audio path" + path);				
				
				FileInputStream fileInputStream = new FileInputStream(path);
				mediaPlayer.setDataSource(fileInputStream.getFD());
				mediaPlayer.setOnCompletionListener(this);				

				mediaPlayer.prepare();
				playAnimation(mediaPlayer.getDuration());
				mediaPlayer.start();			
			}
		} catch (Exception e) {
			logger.error("play audio", e);
		}
	}
	
	private void stopSound() {
		if(mediaPlayer != null) {
			if(mediaPlayer.isPlaying()) {
				mediaPlayer.stop();
			}
			mediaPlayer.reset();
		}
	}
	
	private void playAnimation(int duration) {
		AnimationDrawable playAnimation = new AnimationDrawable();
		if(duration != 0) {
			int frameDuration = (duration - 100) / animationDrawables.length;
			for (int i = 0; i < animationDrawables.length; i++) {
				playAnimation.addFrame(animationDrawables[i], frameDuration);
			}
		}
		viewHolder.animationView.setVisibility(View.VISIBLE);
		viewHolder.animationView.setImageDrawable(playAnimation);
		playAnimation.start();
	}
	
	private void stopAnimation(ExtrasSoundboardItemsAdapter.ViewHolder newHolder) {
		if(viewHolder != null) {
			AnimationDrawable animation = (AnimationDrawable) viewHolder.animationView.getDrawable();
			if(animation != null) {
				animation.stop();
			}
			viewHolder.animationView.setVisibility(View.INVISIBLE);
		}
		this.viewHolder = newHolder;
	}
	
	private void updateSoundboardPanel() {
		if(sendButton != null) {
			String localizerKey = sendMode ? LocalizeKeys.SOUNDBOARD_SEND_BUTTON_TITLES : LocalizeKeys.SOUNDBOARD_SAVE_BUTTON_TITLES;
			sendButton.setText(localizer.getLocalizedString(localizerKey, application));
		}
		
		if(sendSDButton != null) {
			sendSDButton.setText(localizer.getLocalizedString(LocalizeKeys.SOUNDBOARD_SEND_BUTTON_SD, application));
			sendSDButton.setVisibility(sendMode ? View.VISIBLE : View.INVISIBLE);
		}
		
		if(cancelButton != null) {
			cancelButton.setVisibility(sendMode ? View.VISIBLE : View.INVISIBLE);
		}
	}
}
