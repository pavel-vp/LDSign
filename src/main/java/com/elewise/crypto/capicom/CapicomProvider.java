package com.elewise.crypto.capicom;

import by.creepid.capicom.wrapper.*;
import com.elewise.Server;
import com.elewise.api.CertificatListRequest;
import com.elewise.api.CertificateRec;
import com.elewise.api.SignRequest;
import com.elewise.api.SignResponse;
import com.elewise.crypto.IProvider;

import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

public class CapicomProvider implements IProvider {
    private static final Logger logger = Logger.getLogger(Server.class.getName());

    private final SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");//2012-04-23T18:25:43.511Z,     // Date, валиден с указанной даты
    private final SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm:ss");//2012-04-23T18:25:43.511Z,     // Date, валиден с указанной даты


    private Map<Integer, String> mapStoreName = new HashMap<Integer, String>() {
        {
            this.put(2, "My");
            this.put(4, "Card"); // ???
        }
    };

    private volatile List<CapicomCertificate> certificates = null;
    private volatile long lastTimeRefresh = 0;

    private synchronized void refreshCertificates(Integer storeLocation) {
        List<CapicomCertificate> result = new ArrayList<>();

        CapicomStore store = new CapicomStore(storeLocation, mapStoreName.get(storeLocation), 2);
        CapicomCertificate[] certs = store.getCertificates().getAll();

        for (CapicomCertificate capicomCertificate : certs) {
            result.add(capicomCertificate);
        }
        this.certificates = result;
        this.lastTimeRefresh = new Date().getTime();
    }

    private List<CapicomCertificate> getAllCerts(Integer storeLocation) {
        long now = new Date().getTime();
        if (certificates == null || now - lastTimeRefresh > (30 * 1000)) {
            refreshCertificates(storeLocation);
        }
        return certificates;
    }

    private static String getThumbprint(X509Certificate cert)
            throws NoSuchAlgorithmException, CertificateEncodingException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        byte[] der = cert.getEncoded();
        md.update(der);
        byte[] digest = md.digest();
        String digestHex = DatatypeConverter.printHexBinary(digest);
        return digestHex.toLowerCase();
    }

    private CertificateRec convertCapicom2Rec(CapicomCertificate cert) {
        try {
            CertificateRec rec = new CertificateRec();
            String certThumbPrint = getThumbprint(cert.getX509Certificate());
            rec.setThumbprint(certThumbPrint);
            String simpleSubjectName = cert.getInfo(CapicomCertInfo.CERT_INFO_SUBJECT_SIMPLE_NAME);
            rec.setSimpleName(simpleSubjectName);
            rec.setHasPrivateKey(cert.hasPrivateKey());
            rec.setSubjectName(cert.getSubjectName());
            rec.setValidFromDate(sdfDate.format(cert.getValidFromDate())+"T"+sdfTime.format(cert.getValidFromDate()));
            rec.setValidToDate(sdfDate.format(cert.getValidToDate())+"T"+sdfTime.format(cert.getValidToDate()));
            rec.setPublicKeyAlg(cert.getX509Certificate().getPublicKey().getAlgorithm());
            return rec;
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return null;
    }


    private CapicomCertificate getCapicomCertByThumbprint(Integer storeLocation, String thumbprint) {
        try {
            String trimmedThumbpring = thumbprint.replaceAll(" ", "");
            for (CapicomCertificate cert : getAllCerts(storeLocation)) {
                String certThumbPrint = getThumbprint(cert.getX509Certificate());
                if (trimmedThumbpring.equalsIgnoreCase(certThumbPrint)) {
                    return cert;
                }
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    @Override
    public CertificateRec getCertRecByThumbprint(Integer storeLocation, String thumbprint) {
        CapicomCertificate cert = getCapicomCertByThumbprint(storeLocation, thumbprint);
        if (cert != null) {
            CertificateRec rec = convertCapicom2Rec(cert);
            return rec;
        }
        return null;
    }

    @Override
    public List<CertificateRec> getAllCertsRec(CertificatListRequest req) {
        List<CertificateRec> result = new ArrayList<>();
        try {
            for (CapicomCertificate cert : getAllCerts(req.getStoreLocation())) {
                    CertificateRec rec = convertCapicom2Rec(cert);
                    result.add(rec);
            }
            return result;
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
        return null;
    }

    @Override
    public SignResponse sign(SignRequest req) {
        SignResponse resp = new SignResponse();
        try {
            CapicomCertificate cert = getCapicomCertByThumbprint(req.getStoreLocation(), req.getThumbprint());
            CapicomSigner signer = new CapicomSigner();
            signer.setCertificate(cert);

            CapicomSignedData signedData = new CapicomSignedData();
            //"test" - signing string
            String decoded = new String(Base64.getDecoder().decode(req.getContent().getBytes()));
            signedData.setContent(decoded);

            CapicomAttribute signingTime = new CapicomAttribute();
            //Add signing time attribute, see CapicomAttributeEnum
            signingTime.setName(CapicomAttributeEnum.CAPICOM_AUTHENTICATED_ATTRIBUTE_SIGNING_TIME);
            signingTime.setValue(new Date());

            signer.getAuthenticatedAttributes().add(signingTime);
            //return signature, throws InvalidCertificate otherwice
            String  signature = signedData.sign(signer, req.isDetached());
            String newSignature = signature.replaceAll("\\r\\n", "");
            resp.setSignedData(newSignature);
            saveToFile(req.getThumbprint(), newSignature);
        } catch (Exception e) {
            resp.setError(e.getMessage());
        }
        return resp;
    }

    private void saveToFile(String thumbprint, String signature) {
        byte[] decoded = Base64.getDecoder().decode(signature.getBytes());
        File file = new File("c:/" + thumbprint + ".sign");
        try (FileOutputStream os = new FileOutputStream(file)) {
            os.write(decoded);
            os.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
