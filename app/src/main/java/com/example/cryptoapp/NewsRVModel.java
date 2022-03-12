package com.example.cryptoapp;

public class NewsRVModel {
    private String headline;
    private String sourceURL;
    private String sourceName;

    public NewsRVModel(String headline, String sourceURL, String sourceName) {
        this.headline = headline;
        this.sourceURL = sourceURL;
        this.sourceName = sourceName;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getSourceURL() {
        return sourceURL;
    }

    public void setSourceURL(String sourceURL) {
        this.sourceURL = sourceURL;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }
}
