package com.elewise.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CertificateRec implements Serializable {
    private boolean hasPrivateKey; //Есть ли приватный ключ (true - да, false - нет)
    private String publicKeyAlg; //": "",   // string, Алгоритм, используемый публичным ключем
    private String thumbprint; //": "",     // string, отпечаток ключа
    private String subjectName; //": "",    // string, наименование серт-та
    private String validFromDate; //": 2012-04-23T18:25:43.511Z,     // Date, валиден с указанной даты
    private String validToDate; //": 2020-04-23T18:25:43.511Z, // Date, вадиден до указанной даты
    private String simpleName;  //": ""      // string, простое имя серт-та

    public boolean isHasPrivateKey() {
        return hasPrivateKey;
    }

    public void setHasPrivateKey(boolean hasPrivateKey) {
        this.hasPrivateKey = hasPrivateKey;
    }

    public String getPublicKeyAlg() {
        return publicKeyAlg;
    }

    public void setPublicKeyAlg(String publicKeyAlg) {
        this.publicKeyAlg = publicKeyAlg;
    }

    public String getThumbprint() {
        return thumbprint;
    }

    public void setThumbprint(String thumbprint) {
        this.thumbprint = thumbprint;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getValidFromDate() {
        return validFromDate;
    }

    public void setValidFromDate(String validFromDate) {
        this.validFromDate = validFromDate;
    }

    public String getValidToDate() {
        return validToDate;
    }

    public void setValidToDate(String validToDate) {
        this.validToDate = validToDate;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }
}
