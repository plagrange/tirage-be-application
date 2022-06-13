package com.dictao.dtp.web.proofexplorer;

import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.util.convert.Base64;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

public class ProofVisualization {

    private static final Logger LOG = LoggerFactory.getLogger(ProofVisualization.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + ProofVisualization.class.getName());

    private static final String TYPE_X509 = "javax.servlet.request.X509Certificate";
    protected static final String DVS_VISUALIZATION_URL_KEY = "dvs-proof-visualization-url";
    protected static final String ECM_ZIP_MODE = "zip";

    private String txId = null;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
    }
    private String ecmServiceName = null;

    public String getEcmServiceName() {
        return ecmServiceName;
    }

    public void setEcmServiceName(String ecmServiceName) {
        this.ecmServiceName = ecmServiceName;
    }
    private String proofName = null;

    public String getProofName() {
        return proofName;
    }

    public void setProofName(String proofName) {
        this.proofName = proofName;
    }
    private String ecmMode;

    public String getEcmMode() {
        return ecmMode;
    }

    public void setEcmMode(String ecmMode) {
        this.ecmMode = ecmMode;
    }

    public boolean isEcmZipMode() {
        return ECM_ZIP_MODE.equals(ecmMode);
    }
    @Inject
    protected TransactionFactory txService;
    
    @Inject
    protected SessionEcmZipService ecmZip;
    
    @Inject
    protected ApplicationListConf conf;

    public void initProofVisualization() throws IOException {

        X509Certificate cert = getSSLCertificate();

        IECMService ecm;

        if (isEcmZipMode()) {
            ecm = ecmZip;
        } else {
            ecm = (IECMService) txService.getService(txId, ecmServiceName, cert);
        }

        ECMDocument doc = ecm.get(txId, proofName);

        base64Proof = Base64.encodeBytes(copyStreamToMemory(doc.readContent()), Base64.DONT_BREAK_LINES);

        // Retreive visualization url from global conf Url
        postUrl = conf.getUrl(DVS_VISUALIZATION_URL_KEY);
    }

    private String base64Proof;
    private String postUrl;

    public String getBase64Proof() {
        return base64Proof;
    }

    public String getPostUrl() {
        return postUrl;
    }

    private X509Certificate getSSLCertificate() {

        HttpServletRequest servletRequest = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest());
        // Si la requete existe
        if (servletRequest != null) {
            // On recupere un ensemble de certificat associe la
            // connexion
            X509Certificate[] sslCerts = (X509Certificate[]) servletRequest.getAttribute(TYPE_X509);
            // Si on a aucun certificat
            if ((sslCerts != null) && (sslCerts.length > 0)) {
                return sslCerts[0];
            }
        }
        return null;
    }

    private static byte[] copyStreamToMemory(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();

        byte[] buf = new byte[8192];

        int length;

        while (0 <= (length = input.read(buf))) {
            output.write(buf, 0, length);
        }

        return output.toByteArray();
    }
}
