package com.wb.vapps.network.communication;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.wb.vapps.container.Container;
import com.wb.vapps.network.communication.exception.CommunicationException;
import com.wb.vapps.utils.Logger;

/**
 * Note: HttpClient is not multi-threaded.. each of the methods here must be used synchronously. Special care must
 * be used where there is an inputStream returned.
 * 
 * @author jsimonelis
 */
public class CommunicationServiceAndroid implements CommunicationService {
	
	private static final Logger log  = Logger.getLogger(CommunicationServiceAndroid.class.getSimpleName());
	
	private static final int TIMEOUT = 2000;
	
	private final HttpClient client1;
	private final HttpClient client2;
	private final HttpClient client3;
	private final HttpClient client4;
	private boolean interrupted;
	
	public  CommunicationServiceAndroid() {
		HttpParams myHttpParams = new BasicHttpParams();
    	HttpConnectionParams.setConnectionTimeout(myHttpParams, TIMEOUT);
    	
		client1 =  new DefaultHttpClient(myHttpParams);
		client2 =  new DefaultHttpClient(myHttpParams);
		client3 =  new DefaultHttpClient(myHttpParams);
		client4 =  new DefaultHttpClient(myHttpParams);
	}
	
	private boolean isConnectedToInternet() {
		return Container.getInstance().getConnectionManager().isInternetAvailable();
	}
	
	private HttpResponse executeRequest(HttpClient client, HttpPost post) {
		HttpResponse response = null;
		try {
			response = client.execute(post);
		} catch (Exception e) { 
			log.error("Error posting request: "+ e +", to url: "+post.getURI());
		}
		return response;
	}
	
	private HttpResponse executeRequest(HttpClient client, HttpGet get) {
		HttpResponse response = null;
		try {
			response = client.execute(get);
		} catch (Exception e){ 
			log.error("Error posting request: "+ e +", to url: "+get.getURI());
		}
		return response;
	}
	
	public InputStream executeHttpPostForInputStream(String url, String postContents) throws CommunicationException, InterruptedException {
		if (!isConnectedToInternet()) {
			throw new CommunicationException("Internet is not available");
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		HttpPost post = new HttpPost(url);
		try {
			post.setEntity(new StringEntity(postContents,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("Error setting up post body", e);
		}
		
		int countResponse = 0;
		HttpResponse response = null;
		do {
			if (countResponse > 0) {
				log.debug("Repeating Request #" + countResponse);
			}
			
			response = executeRequest(client1, post);
			countResponse++;
		} while (response == null && countResponse < 3);
		
		if (response == null) {
			throw new CommunicationException("Error posting request");
		}

		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		InputStream is = null;
		if (response != null) { 
			try {
				System.out.println("getContent");
				is = response.getEntity().getContent();
			} catch (Exception e) {
				log.error("Error reading response", e);
			}
		}	
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		return is;
	}
	
	public String executeHttpPost(String url,  InputStream postContents) throws CommunicationException, InterruptedException {
		if (!isConnectedToInternet()) {
			throw new CommunicationException("Internet is not available");
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		HttpPost post = new HttpPost(url);

		post.setEntity(new InputStreamEntity(postContents,-1));
		
		int countResponse = 0;
		HttpResponse response = null;
		do {
			if (countResponse > 0) {
				log.debug("Repeating Request #" + countResponse);
			}
			
			response = executeRequest(client4, post);
			countResponse++;
		} while (response == null && countResponse < 3);
		
		if (response == null) {
			throw new CommunicationException("Error posting request");
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		if (response != null) { 
			try {
				StringBuffer buf = new StringBuffer();
				
				InputStream is = response.getEntity().getContent();
				
				/* Interrupted */
				if (interrupted) {
					interrupted = false;
					throw new InterruptedException("Interrupted");
				}
				
				
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				
				String line = br.readLine();
				while (line != null) {
					buf.append(line);
					line = br.readLine();
					
					/* Interrupted */
					if (interrupted) {
						interrupted = false;
						throw new InterruptedException("Interrupted");
					}
					
				}
				
				br.close();
				is.close();
				
				log.debug("reponse body: " + buf.toString());
		
				/* Interrupted */
				if (interrupted) {
					interrupted = false;
					throw new InterruptedException("Interrupted");
				}
				
				return buf.toString();
				
			} catch (Exception e) {
				log.error("Error reading response", e);
			}
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		
		return null;
	}
	
	public String executeHttpPost(String url, String postContents) throws CommunicationException, InterruptedException {
		if (!isConnectedToInternet()) {
			throw new CommunicationException("Internet is not available");
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		HttpPost post = new HttpPost(url);

		try {
			post.setEntity(new StringEntity(postContents,"UTF-8"));
		} catch (UnsupportedEncodingException e) {
			log.error("Error setting up post body", e);
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		int countResponse = 0;
		HttpResponse response = null;
		do {
			if (countResponse > 0) {
				log.debug("Repeating Request #" + countResponse);
			}
			
			response = executeRequest(client2, post);
			countResponse++;
		} while (response == null && countResponse < 3);
		
		if (response == null) {
			throw new CommunicationException("Error posting request");
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		if (response != null) { 
			try {
				StringBuffer buf = new StringBuffer();
		
				/* Interrupted */
				if (interrupted) {
					interrupted = false;
					throw new InterruptedException("Interrupted");
				}
				
				InputStream is = response.getEntity().getContent();
				BufferedReader br = new BufferedReader(new InputStreamReader(is));
				
				/* Interrupted */
				if (interrupted) {
					interrupted = false;
					throw new InterruptedException("Interrupted");
				}
				
				String line = br.readLine();
				while (line != null) {
					buf.append(line);
					line = br.readLine();
					
					/* Interrupted */
					if (interrupted) {
						interrupted = false;
						throw new InterruptedException("Interrupted");
					}
				}
				
				br.close();
				is.close();
				
				log.debug("reponse body: " + buf.toString());
				
				/* Interrupted */
				if (interrupted) {
					interrupted = false;
					throw new InterruptedException("Interrupted");
				}
				
				return buf.toString();
				
			} catch (Exception e) {
				log.error("Error reading response", e);
			}
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		return null;
	}

	public InputStream executeHttpGet(String url) throws CommunicationException, InterruptedException {
		String targetUrl = url.replace(" ", "%20");
		
		if (!isConnectedToInternet()) {
			throw new CommunicationException("Internet is not available");
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		InputStream is = null;

		int countResponse = 0;
		HttpResponse response = null;
		do {
			if (countResponse > 0) {
				log.debug("Repeating Request #" + countResponse);
			}
			
			response = executeRequest(client3, new HttpGet(targetUrl));
			countResponse++;
		} while (response == null && countResponse < 3);
		
		if (response == null) {
			throw new CommunicationException("Error posting request");
		}
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		if (response != null) { 
			try {
				is = response.getEntity().getContent();
				
				/* Interrupted */
				if (interrupted) {
					interrupted = false;
					throw new InterruptedException("Interrupted");
				}
				
			} catch (Exception e) {
				log.error("Error reading response", e);
			} finally {
				response = null;
			}
		}	
		
		/* Interrupted */
		if (interrupted) {
			interrupted = false;
			throw new InterruptedException("Interrupted");
		}
		
		return is;
	}

	public void interrupt() {
		log.debug("--interrupted");
		interrupted = true;
	}

	public void clearInterrupted() {
		interrupted = false;
	}

}
