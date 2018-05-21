package com.elewise.api;

import java.io.Serializable;

public class SignResponse implements Serializable {
    private String signedData; //: "", // string, подписанные данные
    private String error; //": ""             // string, ошибка подписания

    public String getSignedData() {
        return signedData;
    }

    public void setSignedData(String signedData) {
        this.signedData = signedData;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
