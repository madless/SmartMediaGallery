package com.wb.vapps.utils;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import android.content.Context;
import android.os.StatFs;

import com.wb.vapps.encryption.MediaEncrypter;
import com.wb.vapps.settings.Constants;

public class FileUtils {
	private final static Logger logger = Logger.getLogger(FileUtils.class.getSimpleName());
	
	public static void close(Closeable closeable) {
		if(closeable == null) {
			return;
		}
		try { closeable.close(); } catch (Exception e) {}
	}
	
	public static String getFileNameFromFilePath(String path) {
		int indexOfLastSlash = path.lastIndexOf("/") + 1;
		path = path.substring(indexOfLastSlash);
		return path.replace(" ", "_");
	}
	
	public static void extractZipFile(InputStream is, String toPath) throws IOException {
		ZipInputStream zis = null;
		FileOutputStream fos = null;
		try {
			zis = new ZipInputStream(is);
			
			ZipEntry ze = null;
			byte[] buffer = new byte[8192];
			int length = 0;
			while ((ze = zis.getNextEntry()) != null) {
				fos = new FileOutputStream(new File(toPath, ze.getName()));
				while((length = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, length);
				}
				fos.flush();
				fos.close();
			}
		} finally {
			FileUtils.close(fos);
			FileUtils.close(zis);
			FileUtils.close(is);
		}
	}
	
	public static InputStream getStreamFromAssetsFile(Context context, String assetsPath) {		    	
    	InputStream is = null;

		try { 
			if (assetsPath.startsWith(Constants.ASSETS_ROOT)) {
				int start = Constants.ASSETS_ROOT.length();
				int end = assetsPath.length();
				assetsPath = assetsPath.substring(start, end);
			}			
	    	    	
	    	return context.getAssets().open(assetsPath);
	    	
		} catch (Exception e) {
			logger.error("CAUGHT EXCEPTION WHEN READ FROM ASSETS: " + e);
		}
		
		return is;
	}
	
	public static String getFileContent(String filePath) {		    	
    	InputStream is = null;
    	StringBuffer buf = null;
    	BufferedReader br = null;
    	String result = null;

		try { 
			is = new FileInputStream(filePath);			
	    	buf = new StringBuffer();
	    	br = new BufferedReader(new InputStreamReader(is));
	
	    	String line = br.readLine();
			while (line != null) {
				buf.append(line);
				line = br.readLine();			
			}
			result = buf.toString();
		} catch (Exception e) {
			logger.error("CAUGHT EXCEPTION WHEN READ FROM ASSETS: " + e);
		} finally {
			try { 
				if (br != null) {
					br.close();
					br = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (Exception e) {
				logger.error("CAUGHT EXCEPTION WHEN CLOSING OPENED STREAMS: " + e);
			}
		}
		
		return result;
	}

	
	public static String getFileFromAssets(Context context, String assetsPath) {		    	
    	InputStream is = null;
    	StringBuffer buf = null;
    	BufferedReader br = null;
    	String result = null;

		try { 
			if (assetsPath.startsWith(Constants.ASSETS_ROOT)) {
				int start = Constants.ASSETS_ROOT.length();
				int end = assetsPath.length();
				assetsPath = assetsPath.substring(start, end);
			}			
	    	    	
	    	is = context.getAssets().open(assetsPath);
	    	buf = new StringBuffer();
	    	br = new BufferedReader(new InputStreamReader(is));
	
	    	String line = br.readLine();
			while (line != null) {
				buf.append(line);
				line = br.readLine();			
			}
			result = buf.toString();
		} catch (Exception e) {
			logger.error("CAUGHT EXCEPTION WHEN READ FROM ASSETS: " + e);
		} finally {
			try { 
				if (br != null) {
					br.close();
					br = null;
				}
				if (is != null) {
					is.close();
					is = null;
				}
			} catch (Exception e) {
				logger.error("CAUGHT EXCEPTION WHEN CLOSING OPENED STREAMS: " + e);
			}
		}
		
		return result;
	}
	
	public static boolean isFileExistInAssets(Context context, String assetsPath) {
		InputStream is = null;
		boolean exist = false; 

		try {
			if (assetsPath.startsWith(Constants.ASSETS_ROOT)) {
				int start = Constants.ASSETS_ROOT.length();
				int end = assetsPath.length();
				assetsPath = assetsPath.substring(start, end);
			}
			
			is = context.getAssets().open(assetsPath);
			
			if (is != null) {
				exist = true;
			}
		}
		catch (IOException ex) {}
		finally {
			FileUtils.close(is);
		}

		return exist;
	}
	
	public static String  copyFileFromAssets(Context context, String assetsPath, String targerPath) throws IOException {		
		String targerPathWithFile = targerPath + "/" + getFileNameFromFilePath(assetsPath);    	

		if (assetsPath.startsWith(Constants.ASSETS_ROOT)) {
			int start = Constants.ASSETS_ROOT.length();
			int end = assetsPath.length();
			assetsPath = assetsPath.substring(start, end);
		}
    	    	
    	InputStream is = context.getAssets().open(assetsPath);    	
        
    	File outputFile = new File(targerPathWithFile);
		File parent = new File(outputFile.getParent());		
		
		if (!parent.exists()) {
			boolean created = parent.mkdirs();
			if (!created) {
				throw new RuntimeException("Could not create dir: "+ parent.getAbsolutePath());
			}
		}
		
		if (!outputFile.exists()) {
			boolean created = outputFile.createNewFile();
			if (!created) {
				throw new RuntimeException("Could not create: "+ targerPath);
			}
		}
		
		FileOutputStream fos = new FileOutputStream(outputFile);

		byte[] buffer = new byte[1024];
		int bytesRead = -1;
		while ((bytesRead = is.read(buffer)) != -1) {
			fos.write(buffer, 0, bytesRead);
		}
		
		FileUtils.close(fos);
		FileUtils.close(is);
		
		return targerPathWithFile;
	}
	
	public static InputStream getFileInputStream(String filePath) {
		try {
			return new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			logger.error("Cannot get is from file: " + filePath + ", exception: " + e);
		}
		return null;
	}
	
	public static InputStream getFileInputStream(Context context, String filePath) {
		try {
			if (filePath.startsWith(Constants.ASSETS_ROOT)) {
				int start = Constants.ASSETS_ROOT.length();
				int end = filePath.length();
				filePath = filePath.substring(start, end);
				
				return context.getAssets().open(filePath);
			}
			
		} catch (IOException e) {
			logger.error("cannot get is from asset: " + filePath + ", exception: "+ e );
		}
	    	
		try {	
			return new FileInputStream(filePath);
		} catch (FileNotFoundException e) {
			logger.error("cannot get is from file: " + filePath + ", exception: "+ e );
		}
		return null;
	}
	
	public static boolean streamToFile(InputStream is, String dstFilePath, boolean overwrite, MediaEncrypter encrypter) {
		// create file if does not exists
		if (is == null) {
			logger.error("input stream is null");
			return false;
		}
		File ofile = new File(dstFilePath);
		if (!ofile.exists()) {
			try {
				if (!new File(ofile.getParent()).mkdirs() && !ofile.createNewFile()) {
					return false;
				}
			} catch (IOException e) {
				logger.error("cannot create new file", e);
				return false;
			}
		} else if (ofile.exists() && !overwrite) {
			logger.warn("dst file exists");
			return false;
		}
		
		logger.debug("Writing file to path: " + dstFilePath);
		if (encrypter != null) {
			logger.debug("Encripting file mode is true");
		}
		
		FileOutputStream fos = null;
		try {
			
			fos = new FileOutputStream(dstFilePath);
			
			byte[] buffer = new byte[2048];
			int bytesRead = -1;
			while ((bytesRead = is.read(buffer)) != -1) {
				if (encrypter != null) {
					encrypter.encrypt(buffer);
				}
				fos.write(buffer, 0, bytesRead);
			}
			
			fos.flush();
			return true;
		} catch (FileNotFoundException e) {
			logger.error("cannot find file file", e);
		} catch (IOException e) {
			logger.error("cannot read/write streams", e);
		} finally {
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (IOException e) {}
			}
			if (is != null) {
				try {
					is.close();
					is = null;
				} catch (IOException e) {}
			}
		}
		return false;
	}
	
	public static String copyFileToDir(String srcFilePath, String dstDirPath, boolean overwrite, MediaEncrypter encrypter) {
		// create dir if does not exists
		File ofile = new File(dstDirPath);
		if (!ofile.exists()) {
			if (!ofile.mkdirs()) {
				return null;
			}
		}
		if (!dstDirPath.endsWith(File.separator)) {
			dstDirPath += File.separator;
		}
		if (copyFiles(srcFilePath, dstDirPath + getFileNameFromFilePath(srcFilePath), overwrite, encrypter)) {
			return dstDirPath + getFileNameFromFilePath(srcFilePath);
		}
		return null;
	}
	
	private static boolean copyFiles(String srcFilePath, String dstFilePath, boolean overwrite, MediaEncrypter encrypter) {
		// create file if does not exists
		File ofile = new File(dstFilePath);
		if (!ofile.exists()) {
			try {
				if (!new File(ofile.getParent()).mkdirs() && !ofile.createNewFile()) {
					return false;
				}
			} catch (IOException e) {
				logger.error("cannot create new file", e);
				return false;
			}
		} else if (ofile.exists() && !overwrite) {
			logger.warn("dst file exists");
			return false;
		}
		
		
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			
			fis = new FileInputStream(srcFilePath);
			fos = new FileOutputStream(dstFilePath);
			
			byte[] buffer = new byte[1024];
			int length = -1;
			
			while ((length = fis.read(buffer)) != -1) {
				if (encrypter != null) {
					encrypter.decrypt(buffer);
				}
				fos.write(buffer, 0, length);
			}
			
			fos.flush();
			return true;
		} catch (FileNotFoundException e) {
			logger.error("cannot find file file", e);
		} catch (IOException e) {
			logger.error("cannot read/write streams", e);
		} finally {
			if (fis != null) {
				try {
					fis.close();
					fis = null;
				} catch (IOException e) {}
			}
			if (fos != null) {
				try {
					fos.close();
					fos = null;
				} catch (IOException e) {}
			}
		}
		return false;
	}
	
	public static boolean removeFile(String filePath) {
		try {
			File f = new File(filePath);
			if (f.exists()) {
				return f.delete();
			}
		} catch (Exception e) {
			logger.error("Caught exception when removing file: " + filePath);
			
			e.printStackTrace();
		}
		return false;
	}
	
	public static boolean removeAllFilesFromPath(String filePath) {
		boolean success = true;
		try {
			File directory = new File(filePath);
			 
			if (directory.exists()) {
				File[] files = directory.listFiles();
				for (File file : files) {
					if (!file.delete()) {			
						success = false;
					}
				}
			}
		} catch (Exception e) {
			logger.error("Caught exception when removing all files from path: " + filePath);
			
			e.printStackTrace();
			
			success = false;
		}
		
		return success;
	}
	
	public static boolean isFileExists(String filePath) {
		File file = new File(filePath);
		return file.exists() && !file.isDirectory();
	}
	
	public static boolean isFileExists(Context context, String filePath) {		
		if (isFilePathToAssets(filePath)) {
			return isFileExistInAssets(context, filePath);
		}
		else {
			return isFileExists(filePath);
		}		
	}
	
	public static boolean isFilePathToAssets(String filePath) {
		return filePath.startsWith(Constants.ASSETS_ROOT);
	}
	
	public static String trimAssetsPath(String filePath) {
		int start = Constants.ASSETS_ROOT.length();
		int end = filePath.length();
		String newFilePath = filePath.substring(start, end);
		
		return newFilePath;
	}
	
	private String getStoragePath() {
		boolean sdCardPresented = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		// check in log
		String storageDir = null;
		
	    

	    if (sdCardPresented) {
	    	String dir = Environment.getExternalStorageDirectory().getAbsolutePath() + MemoryStorage.getStorageRelativePath(getApplicationContext());
	        
	        if (isDirWritable(storageDir)) {
	        	storageDir = dir;
	        }
	    }
	    
	    if (storageDir == null) {
	    	storageDir = getApplicationInfo().dataDir;
	    }
	    
	    return storageDir;
	}
	
	/**
	 * Get available bytes in device or storage by path
	 * @param path - path where free space is checking
	 * @return
	 */
	public static long getAvailableMemorySize(String path) {
		StatFs stat = new StatFs(path);
		long blockSize = stat.getBlockSize();
		
		long availableBlocks = stat.getAvailableBlocks();
		long availableMemory = availableBlocks * blockSize;		
		
		return availableMemory;
	}
}
