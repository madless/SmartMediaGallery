package com.example.student.smartmediagallery.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by student on 17.12.2015.
 */
public class RequestsModel {
    HashMap<String, Notificator> requests;

    public void addRequest(String url, Notificator notificator) {
        if(requests == null) {
            requests = new HashMap<>();
        }
        requests.put(url, notificator);
    }

    public int getNotificatorIdByUrl(String url) {
        if(requests != null) {
            requests.get(url).getId();
        }
        return -1;
    }

    public long getReadBytesByUrl(String url) {
        if(requests != null) {
            requests.get(url).getDownloadable().getBytesRead();
        }
        return -1;
    }

    public String getTitleByUrl(String url) {
        if(requests != null) {
            requests.get(url).getDownloadable().getTitle();
        }
        return null;
    }

    public void setReadSizeByUrl(String url, long readSize) {
        if(requests != null) {
            requests.get(url).getDownloadable().setBytesRead(readSize);
        }
    }
}
