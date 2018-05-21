package com.elewise.crypto;

import com.elewise.api.CertificatListRequest;
import com.elewise.api.CertificateRec;
import com.elewise.api.SignRequest;
import com.elewise.api.SignResponse;

import java.util.List;

public interface IProvider {

    List<CertificateRec> getAllCertsRec(CertificatListRequest req);

    CertificateRec getCertRecByThumbprint(Integer storeLocation, String thumbprint);

    SignResponse sign(SignRequest req);
}
