package com.example.student.smartmediagallery.core.model;

import com.example.student.smartmediagallery.net.ProgressFileLoader;

import java.util.HashMap;

/**
 * Created by student on 17.12.2015.
 */
public class RequestsModel {
    HashMap<String, LoadCommand> requests;

    public void addRequest(String url, LoadCommand loadCommand) {
        if(requests == null) {
            requests = new HashMap<>();
        }
        requests.put(url, loadCommand);
    }

    public int getLoadCommandIdByUrl(String url) {
        if(requests != null && requests.get(url) != null) {
            return requests.get(url).getId();
        }
        return -1;
    }

    public long getReadBytesByUrl(String url) {
        if(requests != null) {
            return requests.get(url).getDownloadable().getBytesRead();
        }
        return -1;
    }

    public String getTitleByUrl(String url) {
        if(requests != null) {
            return requests.get(url).getDownloadable().getTitle();
        }
        return null;
    }

    public void setReadSizeByUrl(String url, long readSize) {
        if(requests != null) {
            requests.get(url).getDownloadable().setBytesRead(readSize);
        }
    }

    public boolean getIsActiveByUrl(String url) {
        if(requests != null && requests.get(url) != null) {
            return requests.get(url).isActive();
        }
        return false;
    }

    public boolean getIsPausedByUrl(String url) {
        if(requests != null && requests.get(url) != null) {
            return requests.get(url).isPaused();
        }
        return false;
    }

    public long getTotalSizeByUrl(String url) {
        if(requests != null && requests.get(url) != null) {
            return requests.get(url).getTotalSize();
        }
        return -1;
    }

    public void setTotalSizeByUrl(String url, long totalSize) {
        if(requests != null) {
            requests.get(url).setTotalSize(totalSize);
        }
    }

    public void setIsActiveByUrl(String url, boolean isActive) {
        if(requests != null && requests.get(url) != null) {
            requests.get(url).setActive(isActive);
        }
    }

    public void setIsPausedByUrl(String url, boolean isPaused) {
        if(requests != null) {
            requests.get(url).setIsPaused(isPaused);
        }
    }

    public void deleteFileFromTargetPath(String url) {
        if(requests != null) {
            requests.get(url).getDownloadable().getTargetPath().delete();
        }
    }

    public void removeByUrl(String url) {
        if(requests != null) {
            requests.remove(url);
        }
    }

    public void prepareCommandByUrl(String url) {
        if(requests.get(url) != null) {
            requests.get(url).prepareLoading();
        }
    }

    public void setCommandListenerByUrl(String url, ProgressFileLoader.LoaderListener listener) {
        if(requests.get(url) != null) {
            requests.get(url).setListener(listener);
        }
    }

    public void executeCommandByUrl(String url) {
        if(requests.get(url) != null) {
            requests.get(url).load();
        }
    }

    public void pauseCommandByUrl(String url) {
        if(requests.get(url) != null) {
            requests.get(url).pause();
        }
    }

    public void stopCommandByUrl(String url) {
        if(requests.get(url) != null) {
            requests.get(url).stop();
        }
    }

}
