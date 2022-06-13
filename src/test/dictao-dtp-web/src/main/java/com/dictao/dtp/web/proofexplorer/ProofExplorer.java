package com.dictao.dtp.web.proofexplorer;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.dictao.dtp.core.ContentType;
import com.dictao.dtp.core.ResourceBundleHandler;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.IValidationService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.services.ecm.IndexEntry;
import com.dictao.dtp.core.services.exceptions.ValidationException;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.core.transactions.serialization.RestDocumentVersion;
import com.dictao.dtp.persistence.entity.TransactionSubStatusEnum;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

abstract public class ProofExplorer {

    private static final RestDocumentVersion restDocVersion = RestDocumentVersion.v2012_07;
    private static final String TYPE_X509 = "javax.servlet.request.X509Certificate";
    private static final Logger LOG = LoggerFactory.getLogger(ProofExplorer.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + ProofExplorer.class.getName());
    protected String sealingIndexType = "SEAL";
    protected String sealingProofIndexType = "SEALING_PROOF";
    protected String sealingProofDocumentName = "[SealingProof].xml";
    protected String PROOF_DESCRIPTION_DOCUMENT_NAME = "[description].xml";
    protected String ECM_ZIP_MODE = "zip";
    @Inject
    protected TransactionFactory txService;
    @Inject
    protected ResourceBundleHandler rbh;
    @Inject
    protected SessionEcmZipService ecmZip;
    private X509Certificate certificate;
    private boolean isCancelled = false;
    private boolean isExpired = false;

    /** setter only for tests*/
    public void setCertificate(X509Certificate certificate) {
        this.certificate = certificate;
    }
    private String txId = null;

    public String getTxId() {
        return txId;
    }

    public void setTxId(String txId) {
        this.txId = txId;
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
    private String ecmServiceName = null;

    public String getEcmServiceName() {
        return ecmServiceName;
    }

    public void setEcmServiceName(String ecmServiceName) {
        this.ecmServiceName = ecmServiceName;
    }
    private String validationServiceName = null;

    public String getValidationServiceName() {
        return validationServiceName;
    }

    public void setValidationServiceName(String validationServiceName) {
        this.validationServiceName = validationServiceName;
    }
    boolean isSealingValid;

    public boolean getIsSealingValid() {
        return isSealingValid;
    }
    private boolean isTransactionSealed;

    public boolean getIsTransactionSealed() {
        return isTransactionSealed;
    }

    /**
     * @return true if the transaction has been cancelled, false otherwise.
     */
    public boolean getIsTransactionCancelled() {
        return isCancelled;
    }

    /**
     * @return true if the transaction has expired, false otherwise.
     */
    public boolean getIsTransactionExpired() {
        return isExpired;
    }
    
    public String getRestDocVersion() {
        return restDocVersion.toString();
    }

    private List<ProofItem> items;

    public List<ProofItem> getItems() throws UnsupportedEncodingException {

        IECMService ecm;

        if (isEcmZipMode()) {
            ecm = ecmZip;
        } else {

            // If the transaction is cancelled or expired (in online mode), return an empty list
            if (isCancelled || isExpired) {
                return new ArrayList<ProofItem>();
            }

            ecm = (IECMService) txService.getService(txId, ecmServiceName, certificate);
        }

        if (ecm == null) {
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Unable to find document storing service '%s'", ecmServiceName);
        }

        // Retrieve all visualizable documents
        ArrayList<String> typesToVisualize = new ArrayList<String>();
        typesToVisualize.addAll(ecm.getTypes(txId));
        // Add sealed items
        typesToVisualize.add(sealingIndexType); // Add sealling if any
        typesToVisualize.add(sealingProofIndexType); // Add sealling proof if any...

        IndexEntry[] content = ecm.getIndexedDocumentListFromType(txId, typesToVisualize);
        items = new ArrayList<ProofItem>();
        for (IndexEntry entry : content) {
            ECMDocument doc = ecm.get(txId, entry.getDocumentFilename());
            if (null == doc) {
                LOG.warn("entry '%s' skipped, no found document by filename.", entry.getDocumentFilename());
                continue;
            }

            // Skip description file visualization
            if (PROOF_DESCRIPTION_DOCUMENT_NAME.equals(doc.getFilename())) {
                continue;
            }

            String ext = getUrlExtention(doc);
            if (entry.getTransform() != null && entry.getTransform().length() > 0) {
                ext += "&transform=" + entry.getTransform();
            }

            items.add(new ProofItem(ecm.getIndexEntryLabel(txId, entry.getType(), rbh), ecm.getIndexEntryDescription(txId, entry.getType(), rbh), urlEncode(doc.getFilename()), entry.getDvsVisualizableProof(), ext));
        }
        return items;
    }


    /*
     * <p> JSF event called on page initialization. </p>
     */
    public synchronized void initPageHandler() throws IOException {
        LOG.entering();

        //condition added because of test setter
        if (null == certificate) {
            this.certificate = getSSLCertificate();
        }

        IECMService ecm;

        if (isEcmZipMode()) {
            ecm = ecmZip;
        } else {
            ecm = (IECMService) txService.getService(txId, ecmServiceName, certificate);
        }

        if (ecm == null) {
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Unable to find document storing service '%s'", ecmServiceName);
        }

        if (!isEcmZipMode()) {

            TransactionHandler txHandler = txService.find(txId, certificate);
            if (txHandler == null) {
                throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                        "Cannot get the handler from id '%s'", txId);
            }

            isCancelled = (txHandler.getDatabaseTransaction().getSubStatus()
                    == TransactionSubStatusEnum.Cancelled);

            isExpired = (txHandler.getDatabaseTransaction().getSubStatus()
                    == TransactionSubStatusEnum.Expired);

            isTransactionSealed = (txHandler.getDatabaseTransaction().getSubStatus() == TransactionSubStatusEnum.Sealed)
                    || (txHandler.getDatabaseTransaction().getSubStatus() == TransactionSubStatusEnum.Finished)
                    || (txHandler.getDatabaseTransaction().getSubStatus() == TransactionSubStatusEnum.Archiving);
            
            if (isTransactionSealed) {

                Map<String, InputStream> files = new HashMap<String, InputStream>();

                List<String> documentNames = ecm.getDocumentList(txId);

                for (String docName : documentNames) {
                    ECMDocument doc = ecm.get(txId, docName);
                    if (null == doc) {
                        LOG.warn("entry '%s' skipped, no found document by filename.", docName);
                        continue;
                    }

                    files.put(docName, doc.readContent());
                }

                InputStream proof = ecm.getFromType(txId, sealingIndexType).readContent();

                IValidationService validation = (IValidationService) txService.getService(txId, validationServiceName, certificate);

                InputStream sealingProof = null;
                try {
                    sealingProof = validation.verifyDetachedXadesManifestSignature(proof, files);
                    isSealingValid = true;
                } catch (ValidationException ex) {
                    LOG.error("An error occured during sealing validation.", ex);
                    isSealingValid = false;
                }

                // Store sealing proof
                ECMDocument sealingProofDocument = new ECMDocument(sealingProofDocumentName, "text/xml", sealingProof, null);
                // nil TxHandler set, possible to add step through findTransaction
                ecm.put(txId, sealingProofDocument, sealingProofIndexType, null);
            }
        }

        LOG.exiting();
    }
    
    private String urlEncode(String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(value, "UTF-8");
    }

    protected String getUrlExtention(ECMDocument doc) {

        if (doc.getContentMimeType().equals("application/pdf")) {
            return "?format=application/pdf";
        }
        String filename = doc.getFilename();
        if (null == filename) {
            throw new IllegalArgumentException(
                    "no filename,cannot load output format from document");
        }
        String ext = (filename.substring(filename.lastIndexOf('.') + 1, filename.length())).toLowerCase();
        String previewMimeType = computePreviewMimeType(ext, doc.getContentMimeType());
        try {
            return "?format=" + URLEncoder.encode(previewMimeType, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            throw new UserException(ex, UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid preview mime type '%s'", previewMimeType);
        }
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

    private String computePreviewMimeType(String extension, String contentMimeType) {
        // to support specific MIME types like image/x-png or image/pjpeg.
        if (extension.equalsIgnoreCase("png")
                || extension.equalsIgnoreCase("jpg")
                || extension.equalsIgnoreCase("jpeg")) {
            return contentMimeType;
        } else {
            return ContentType.getPreviewContentTypeFromExtension(extension, contentMimeType);
        }

    }

}
