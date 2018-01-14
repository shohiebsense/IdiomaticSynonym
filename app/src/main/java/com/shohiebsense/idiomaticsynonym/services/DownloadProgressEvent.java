package com.shohiebsense.idiomaticsynonym.services;

import java.util.List;
//event progress
/**
 * Created by Shohiebsense on 17/09/2017.
 */

public class DownloadProgressEvent {


    public enum DownloadProgressType{FETCHED, STARTED, RUNNING, FINISHED, ERROR, ALREADYDOWNLOADED}
    public String name;
    public DownloadProgressType downloadProgressType;
    public int progress;
    public String filePath;

    public DownloadProgressEvent(DownloadProgressType downloadProgressType, List<String> list) {
        this.downloadProgressType = downloadProgressType;
    }

    public DownloadProgressEvent(String name, DownloadProgressType downloadProgressType) {
        this.name = name;
        this.downloadProgressType = downloadProgressType;
    }

    public DownloadProgressEvent setProgress(int progress){
        this.progress = progress;
        return this;
    }

    public DownloadProgressEvent setType(DownloadProgressType downloadProgressType){
        this.downloadProgressType = downloadProgressType;
        return this;
    }

    public DownloadProgressEvent setFile(String filePath){
        this.filePath = filePath;
        return this;
    }

}
