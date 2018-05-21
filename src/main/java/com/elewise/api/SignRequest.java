package com.elewise.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;


@JsonIgnoreProperties(ignoreUnknown = true)
public class SignRequest implements Serializable {
    private String provider;
    private int storeLocation;
    private String content;
    private String thumbprint;
    private boolean detached;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getThumbprint() {
        return thumbprint;
    }

    public void setThumbprint(String thumbprint) {
        this.thumbprint = thumbprint;
    }

    public boolean isDetached() {
        return detached;
    }

    public void setDetached(boolean detached) {
        this.detached = detached;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public int getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(int storeLocation) {
        this.storeLocation = storeLocation;
    }
}
