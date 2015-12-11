package logic;

import java.io.File;

import service.Service;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;

public class ContentProvider {

	private static final String TAG = "ContentProvider";

	
	private String pathOfFile;

	public ContentProvider(Context con, String pathIc) {

		//context = con;

		pathOfFile = pathIc;

	}

	public boolean isIconExist(Image img) {

		log("boolean isPhotoExist()");
		log(img.getIcon());

		String nameIconForSearch = Service.getFileName(img.getIcon());

		File fileOfIcon = new File(pathOfFile + File.separator
				+ nameIconForSearch);

		return fileOfIcon.exists();
	}
	
	public boolean isPhotoExist(Image img) {

		log("boolean isPhotoExist()");
		log(img.getPhoto());

		String nameIconForSearch = Service.getFileName(img.getPhoto());

		File fileOfPhoto = new File(pathOfFile + File.separator
				+ nameIconForSearch);

		return fileOfPhoto.exists();
	}
	
	public boolean isSoundExist(Sound sound) {

		log("boolean isPhotoExist()");		

		String nameSoundForSearch = Service.getFileName(sound.getSoundAddress());		

		File fileOfSound = new File(pathOfFile + File.separator
				+ nameSoundForSearch);

		log("!!!! Attention !" + pathOfFile + File.separator + nameSoundForSearch);
		log("exsist() - " + fileOfSound.exists());
		
		
		return fileOfSound.exists();
	}

	public Drawable getPhoto(Image img) {

		log("Drawable getPhoto()");

		String nameIcon = Service.getFileName(img.getPhoto());

		return Drawable.createFromPath(pathOfFile + File.separator + nameIcon);
	}

	public String photoPath(Image img) {

		return (pathOfFile + File.separator + Service
				.getFileName(img.getIcon()));
	}

	public Drawable getPhotoIcon(Image img) {

		log("Drawable getPhotoIcon()");

		String nameIcon = Service.getFileName(img.getIcon());

		return Drawable.createFromPath(pathOfFile + File.separator + nameIcon);
	}

	public String photoIconPath(Image img) {

		return (pathOfFile + File.separator + Service
				.getFileName(img.getIcon()));
	}

	public String iconsPath() {
		return pathOfFile;
	}

	private void log(String sms) {
		 Log.d(TAG, sms);
	}

}
