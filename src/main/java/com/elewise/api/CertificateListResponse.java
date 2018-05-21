package com.elewise.api;

import java.io.Serializable;
import java.util.List;

public class CertificateListResponse implements Serializable {
    private List<CertificateRec> certs;
    private String error;

    public List<CertificateRec> getCerts() {
        return certs;
    }

    public void setCerts(List<CertificateRec> certs) {
        this.certs = certs;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
