package com.elewise.api;

import java.io.Serializable;

public class CertificateResponse implements Serializable {
    private CertificateRec cert;
    private String error; //": "" // string, описание ошибки. в случае отсутствия ошибки - пустая строка


    public CertificateRec getCert() {
        return cert;
    }

    public void setCert(CertificateRec cert) {
        this.cert = cert;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
