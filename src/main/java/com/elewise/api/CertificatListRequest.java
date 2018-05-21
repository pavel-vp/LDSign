package com.elewise.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CertificatListRequest implements Serializable {

    private String provider;
    private int storeLocation;

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
