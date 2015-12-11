package com.wb.vapps.network.loader.progress.impl;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.wb.vapps.network.loader.progress.ProgressFileLoader;
import com.wb.vapps.utils.FileUtils;

/**
 * Implementation which use |HttpClient| for all network requests;
 * 
 * @author Alexey Maximenko
 *
 */
public class ProgressFileLoaderBasedOnHttpClient extends ProgressFileLoader {
	public ProgressFileLoaderBasedOnHttpClient(String url, String targetPath, long readBytes) {
		super(url, targetPath, readBytes);
	}

	
	public ProgressFileLoaderBasedOnHttpClient(String url) {
		super(url);		
	}

	@Override	
	public void download() throws IOException, InterruptedException {
		File ofile = new File(targetPath);
		if (!ofile.exists()) {			
			if (!new File(ofile.getParent()).mkdirs() && !ofile.createNewFile()) {
				throw new FileNotFoundException("File could not be created!");
			}			
		}		
		
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Range", "bytes=" + readBytes + "-");
		HttpParams httpParameters = createHttpParams();
		
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(httpGet);		
		
		BufferedInputStream in = null;
		BufferedOutputStream out = null;
		InputStream is = null;
		
		try {			
			is = response.getEntity().getContent();
			in = new BufferedInputStream(is, BUFFER_SIZE);
			out = new BufferedOutputStream(new FileOutputStream(targetPath, true), BUFFER_SIZE);			
			
			totalSize = response.getEntity().getContentLength();
			
			if(progressListener != null) {
				progressListener.onTotalSizeCalculated(totalSize);
			}
			
			byte[] buffer = new byte[BUFFER_SIZE];
			int length;			
			
			while ((length = in.read(buffer)) != -1 && !cancelled) {
				out.write(buffer, 0, length);

				readBytes += length;
				
				if(progressListener != null) {
					progressListener.onProgressUpdated(totalSize, readBytes);
				}
			}		
			
			if (!cancelled) {
				progressListener.onDownloadComplete();
			}
			
			if (cancelled) {
				throw new InterruptedException();
			}
		}
		finally {
			httpGet.abort();
			
			FileUtils.close(in);
			FileUtils.close(is);	
			FileUtils.close(out);
		}
	}
	
	@Override
	public void requestContentLenght() throws IOException {		
		HttpGet httpGet = new HttpGet(url);
		httpGet.setHeader("Range", "bytes=" + readBytes + "-");
		HttpParams httpParameters = createHttpParams();
		
		DefaultHttpClient httpClient = new DefaultHttpClient(httpParameters);
		HttpResponse response = httpClient.execute(httpGet);
		
		totalSize = response.getEntity().getContentLength();
		
		if(progressListener != null) {
			progressListener.onTotalSizeFetched(totalSize);
		}
	}
	
	/**
	 * Creates http params with initialized timeouts 
	 * 
	 * @return
	 */
	private HttpParams createHttpParams() {
		HttpParams httpParameters = new BasicHttpParams();
		// Set the timeout in milliseconds until a connection is established.
		int timeoutConnection = DEFAULT_TIMEOUT;
		HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
		// Set the default socket timeout (SO_TIMEOUT) 
		// in milliseconds which is the timeout for waiting for data.
		int timeoutSocket = DEFAULT_TIMEOUT;
		HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
	
		return httpParameters;
	}
}
