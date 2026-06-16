package com.example.sherpaasr.data;

public class ModelItem {
    public enum Status { NOT_DOWNLOADED, DOWNLOADING, DOWNLOADED }

    private final String name;
    private final String url;
    private final String fileName;
    private Status status;
    private boolean statusExtract = false;
    private int progress; // 0..100
    private boolean paused;
    private String language;

    public ModelItem(String name, String url, String fileName, String language) {
        this.name = name;
        this.url = url;
        this.fileName = fileName;
        this.status = Status.NOT_DOWNLOADED;
        this.progress = 0;
        this.paused = false;
        this.language = language;
    }

    public String getName() { return name; }
    public String getUrl() { return url; }
    public String getFileName() { return fileName; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
    public boolean getStatusExtract() { return statusExtract; }
    public void setStatusExtract(boolean statusExtract) { this.statusExtract = statusExtract; }
    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }
    public boolean isPaused() { return paused; }
    public void setPaused(boolean paused) { this.paused = paused; }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
}
