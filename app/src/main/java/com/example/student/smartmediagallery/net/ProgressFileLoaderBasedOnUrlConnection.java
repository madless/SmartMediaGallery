package com.example.student.smartmediagallery.net;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

public class ProgressFileLoaderBasedOnUrlConnection extends ProgressFileLoader implements Serializable {

    public ProgressFileLoaderBasedOnUrlConnection(String url, String targetPath, long readBytes) {
        super(url, targetPath, readBytes);
    }

    public ProgressFileLoaderBasedOnUrlConnection(String url) {
        super(url);
    }

    public void download() throws IOException, InterruptedException {
        File ofile = new File(targetPath);
        if (!ofile.exists()) {
            if (!new File(ofile.getParent()).mkdirs() && !ofile.createNewFile()) {
                throw new FileNotFoundException("File could not be created!");
            }
        }

        URL resourceUrl = new URL(url);
        URLConnection connection = resourceUrl.openConnection();
        connection.setConnectTimeout(DEFAULT_TIMEOUT);

        connection.setRequestProperty("Range", "bytes=" + readBytes + "-");

        BufferedInputStream in = null;
        BufferedOutputStream out = null;

        try {
            in = new BufferedInputStream(connection.getInputStream(), BUFFER_SIZE);
            out = new BufferedOutputStream(new FileOutputStream(targetPath, true), BUFFER_SIZE);

            List<String> headerFields = connection.getHeaderFields().get("Content-Length");
            if (headerFields != null && !headerFields.isEmpty()) {
                String sLength = (String) headerFields.get(0);

                if (sLength != null) {
                    totalSize = Long.parseLong(sLength);

                    if(progressListener != null) {
                        progressListener.onTotalSizeCalculated(url, totalSize);
                    }
                }
            }

            byte[] buffer = new byte[BUFFER_SIZE];
            int length;

            while ((length = in.read(buffer)) != -1) {
                out.write(buffer, 0, length);

                readBytes += length;

                if(progressListener != null) {
                    progressListener.onProgressUpdated(url, totalSize, readBytes);
                }

                if (cancelled) {
                    throw new InterruptedException();
                }
            }

            progressListener.onDownloadComplete(url);
        }
        finally {
            in.close();
            out.close();
        }
    }

    public void requestContentLenght() throws IOException {
        URL resourceUrl = new URL(url);
        URLConnection connection = resourceUrl.openConnection();
        connection.setConnectTimeout(DEFAULT_TIMEOUT);

        List<String> headerFields = connection.getHeaderFields().get("Content-Length");
        if (headerFields != null && !headerFields.isEmpty()) {
            String sLength = (String) headerFields.get(0);

            if (sLength != null) {
                totalSize = Long.parseLong(sLength);
                if(progressListener != null) {
                    progressListener.onTotalSizeFetched(url, totalSize);
                }
            }
        }
    }
}
