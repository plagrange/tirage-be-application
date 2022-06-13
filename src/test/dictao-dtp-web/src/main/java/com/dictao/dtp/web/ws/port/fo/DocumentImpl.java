package com.dictao.dtp.web.ws.port.fo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.jws.WebService;

import org.apache.log4j.MDC;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.TransactionStatusEnum;
import com.dictao.dtp.persistence.entity.TransactionSubStatusEnum;
import com.dictao.dtp.web.ws.ExceptionConverter;
import com.dictao.dtp.web.ws.Task;
import com.dictao.dtp.web.ws.TransactionalTask;
import com.dictao.dtp.web.ws.WebServiceImpl;
import com.dictao.dtp.web.ws.conversion.SignaturesConverter;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import com.dictao.wsdl.dtp.frontoffice.v3.DocumentPort;
import com.dictao.wsdl.dtp.frontoffice.v3.SystemFaultException;
import com.dictao.wsdl.dtp.frontoffice.v3.UserFaultException;
import com.dictao.xsd.dtp.common.v2012_03.Document;
import com.dictao.xsd.dtp.common.v2012_03.IndexEntry;
import com.dictao.xsd.dtp.common.v2012_03.Metadata;
import com.dictao.xsd.dtp.common.v2012_03.PersonalSignature;
import com.dictao.xsd.dtp.common.v2012_03.Signatures;
import com.dictao.xsd.dtp.common.v2012_03.VisibleSignature;
import com.dictao.xsd.dtp.error.v3.SystemErrorInfo;
import com.dictao.xsd.dtp.error.v3.UserErrorInfo;
import com.sun.xml.ws.developer.SchemaValidation;
import java.util.Locale;
import org.w3c.dom.Element;

/**
 * 
 * @author msauvee
 */
@SchemaValidation
@WebService(serviceName = "FrontOfficeService", portName = "documentPort", endpointInterface = "com.dictao.wsdl.dtp.frontoffice.v3.DocumentPort", targetNamespace = "http://dictao.com/wsdl/dtp/frontoffice/v3")
public class DocumentImpl extends WebServiceImpl implements DocumentPort {

    private static final Logger PULL_LOG = LoggerFactory.getLogger("user."
            + DocumentImpl.class.getName());

    private static final ExceptionConverter<UserFaultException, SystemFaultException> ec = new ExceptionConverter<UserFaultException, SystemFaultException>() {

        @Override
        protected UserFaultException newUserException(String msg,
                UserErrorInfo faultInfo) {
            return new UserFaultException(msg, faultInfo);
        }

        @Override
        protected SystemFaultException newEnvironmentException(String msg,
                SystemErrorInfo faultInfo) {
            return new SystemFaultException(msg, faultInfo);
        }
    };

    private static final String SEAL = "SEAL";

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */

    /**
     * 
     * @param transactionId
     * @param service
     * @param name
     * @return
     * @throws UserFaultException
     * @throws SystemFaultException
     */
    @Override
    public Document getDocument(final String transactionId,
            final String service, final String name) throws UserFaultException,
            SystemFaultException {
        Task<Document, UserFaultException, SystemFaultException> t = new TransactionalTask<Document, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Document run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                
                // no getDocument on a cancelled or expired transaction
                checkIfCancelledOrExpired (transactionId);
                
                IECMService svc = (IECMService) getService(transactionId,
                        service);
                ECMDocument doc = svc.get(transactionId, name);
                if (doc == null) {
                    throw new UserException(
                            UserException.Code.DTP_USER_NOT_FOUND,
                            "Unable to find document '%s'(service '%s', transactionId '%s')",
                            name, service, transactionId);
                }
                
                return ECMDocumentToDocument(doc, transactionId, service);
            }

            // getLogger removed: https://jira.dictao.com/browse/DTPJAVA-507

            @Override
            public String toString() {
                return String
                        .format("getDocument [transactionId='%s', service='%s', name='%s']",
                                transactionId, service, name);
            }
        };
        return t.execute();
    }

    /**
     * 
     * @param transactionId
     * @param service
     * @param document
     * @param type
     *            can be null
     * @throws UserFaultException
     * @throws SystemFaultException
     */
    @Override
    public void putDocument(final String transactionId, final String service,
            final Document document, final String type)
            throws UserFaultException, SystemFaultException {
        Task<Void, UserFaultException, SystemFaultException> t = new TransactionalTask<Void, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Void run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                if (document == null) {
                    throw new UserException(
                            UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "Unable to put the document : parameter is null (service '%s', transactionId '%s')",
                            service, transactionId);
                }
                TransactionHandler handler = getTransactionFactory().find(
                        transactionId, getSSLCertificate());
                RequestInfo requestInfo = collectRequestInfo();
                handler.setRequestInfo(requestInfo);
                
                ECMDocument ecmDoc = DocumentToECMDocument(document);
                IECMService svc = (IECMService) getService(transactionId,
                        service);

                // Check access rights
                
                // No putDocument on a closed transaction.
                if (handler.getDatabaseTransaction().getStatus() == TransactionStatusEnum.Closed) {
                    throw new UserException(
                            UserException.Code.DTP_USER_UNAUTHORIZED,
                            "Unable to put document on a closed transaction (id [%s])",
                            transactionId);
                }
                
                // No putDocument in the seal docSet on a sealed tx
                if (handler.getDatabaseTransaction().getSubStatus() == TransactionSubStatusEnum.Sealed) {
                    boolean isTypeInSealDocSet = false;
                    if (null != handler.getDocumentTypesToSeal()){
                        isTypeInSealDocSet = handler.getDocumentTypesToSeal().contains(type);
                    }else{
                        isTypeInSealDocSet = svc.getTypesFromDocSet(transactionId, SEAL).contains(type);
                    }
                    
                    if (isTypeInSealDocSet) {
                        throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                                "Unable to put document on a sealed transaction (id [%s])", transactionId);
                    }
                }
                String tenant = handler.getDatabaseTransaction().getTenant();
                String appName = handler.getApplicationName();
                Locale locale = getConfigurationReference().getLocale(tenant,appName);
                // we have to use the handler hook in case putDocument is
                // overriden
                handler.putDocument(svc, ecmDoc, type, getResourceBundleHandlerReference(), locale);
                return null;
            }

            @Override
            public String toString() {
                if (document == null) {
                    return String
                            .format("putDocument [transactionId='%s', service='%s',type='%s', no document]",
                                    transactionId, service,type);
                }
                return String
                        .format("putDocument [transactionId='%s', service='%s', type='%s', filename='%s', mimetype='%s', label='%s', description='%s', signatures='%s', metadata=[%s]]",
                                transactionId, service,type,
                                document.getFilename(),
                                document.getMimetype(),
                                document.getLabel(),
                                document.getDescription(),
                                signaturesToString(document.getSignatures()),
                                metadataToString(document.getMetadata()));
            }
        };
        t.execute();
    }
    
    private String signaturesToString(Signatures signatures){
        
        if(signatures == null)
            return "null";
    
        StringBuilder sb = new StringBuilder();
        sb.append("signatures : [");
            
        sb.append("entity:");
        if(signatures.getEntity() == null) {
            sb.append("null");
        } else {
            sb.append("[");
            VisibleSignature visibleSignature = signatures.getEntity().getVisibleSignature();
            if (null != visibleSignature) {
                sb.append("visible:[");
                sb.append(String.format("x='%s', y='%s', width='%s', height='%s', page='%s', layout='%s'",
                        visibleSignature.getX(),
                        visibleSignature.getY(),
                        visibleSignature.getWidth(),
                        visibleSignature.getHeight(),
                        visibleSignature.getPage(),
                        visibleSignature.getLayout()));
                sb.append("]");
            }

            sb.append("]");
        }
        
        sb.append(", personals: [");
        for (PersonalSignature personalSignature : signatures.getPersonal()) {
                sb.append("personal:[");
                VisibleSignature visibleSignature = personalSignature.getVisibleSignature();
                if (null != visibleSignature) {
                    sb.append(String.format("user='%s', label='%s', x='%s', y='%s', width='%s', height='%s', page='%s', layout='%s'",
                            personalSignature.getUser(),
                            personalSignature.getSignatureLabel(),
                            visibleSignature.getX(),
                            visibleSignature.getY(),
                            visibleSignature.getWidth(),
                            visibleSignature.getHeight(),
                            visibleSignature.getPage(),
                            visibleSignature.getLayout()));  
                } else {
                    sb.append(String.format("user='%s', label='%s'", 
                            personalSignature.getUser(), 
                            personalSignature.getSignatureLabel()));
                }
                sb.append("]");
        }
        sb.append(']');
        
        sb.append(']');
        
        return sb.toString();
    }

    @Override
    public List<IndexEntry> getIndex(final String transactionId,
            final String service) throws UserFaultException,
            SystemFaultException {
        Task<List<IndexEntry>, UserFaultException, SystemFaultException> t = new TransactionalTask<List<IndexEntry>, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public List<IndexEntry> run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                
                // no getDocument on a cancelled or expired transaction
                checkIfCancelledOrExpired(transactionId);
                        
                IECMService svc = (IECMService) getService(transactionId,
                        service);
                List<IndexEntry> result = getListIndex(svc, transactionId);
                return result;
            }

            @Override
            protected Logger getUserLogger() {
                // in case of pulling, we do not want to get tons of log in
                // production
                return PULL_LOG;
            }

            @Override
            public String toString() {
                return String.format(
                        "getIndex [transactionId='%s', service='%s']",
                        transactionId, service);
            }
        };
        return t.execute();
    }

    @Override
    public Document getDocumentByType(final String transactionId,
            final String service, final String type) throws UserFaultException,
            SystemFaultException {
        Task<Document, UserFaultException, SystemFaultException> t = new TransactionalTask<Document, UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public Document run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                
                // no getDocument on a cancelled or expired transaction
                checkIfCancelledOrExpired (transactionId);
                
                IECMService svc = (IECMService) getService(transactionId,
                        service);
                ECMDocument doc = svc.getFromType(transactionId, type);
                if (doc == null) {
                    throw new UserException(
                            UserException.Code.DTP_USER_NOT_FOUND,
                            "Unable to find document where type=%s (service '%s', transactionId '%s')",
                            type, service, transactionId);
                }
                
                return ECMDocumentToDocument(doc, transactionId, service);
            }

            @Override
            protected Logger getUserLogger() {
                // in case of pulling, we do not want to get tons of log in
                // production
                return PULL_LOG;
            }

            @Override
            public String toString() {
                return String
                        .format("getDocumentByType [transactionId='%s', service='%s', type='%s']",
                                transactionId, service, type);
            }
        };
        return t.execute();
    }

    @Override
    public byte[] getDocumentsByType(final String transactionId,
            final String service, final List<String> type)
            throws UserFaultException, SystemFaultException {
        Task<byte[], UserFaultException, SystemFaultException> t = new TransactionalTask<byte[], UserFaultException, SystemFaultException>(
                ec, getSSLCertificate()) {

            @Override
            public byte[] run() {
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                
                // no getDocument on a cancelled or expired transaction
                checkIfCancelledOrExpired (transactionId);
                
                IECMService svc = (IECMService) getService(transactionId,
                        service);
                List<String> docNames = new ArrayList<String>();
                for (com.dictao.dtp.core.services.ecm.IndexEntry entry : svc
                        .getIndexedDocumentListFromType(transactionId, type)) {
                    docNames.add(entry.getDocumentFilename());
                }
                if (docNames == null || docNames.isEmpty()) {
                    throw new UserException(
                            UserException.Code.DTP_USER_NOT_FOUND,
                            "Unable to find document(service '%s', transactionId '%s')",
                            service, transactionId);
                }
                
                InputStream input = null;
                byte[] result = null;
                try {
                    input = svc.getDocumentsAsZip(transactionId, docNames);
                    result = new byte[input.available()];
                    input.read(result);

                } catch (IOException ex) {
                    throw new EnvironmentException(
                            ex,
                            EnvironmentException.Code.DTP_ENV_FILE,
                            "Unable to create zip (service '%s', transactionId '%s')",
                            service, transactionId);
                }
                return result;
            }

            @Override
            protected Logger getUserLogger() {
                // in case of pulling, we do not want to get tons of log in
                // production
                return PULL_LOG;
            }

            @Override
            public String toString() {
                String types = "";
                if (null != type) {
                    for (String tmp : type) {
                        types = types.concat(tmp + " ");
                    }
                }
                return String
                        .format("getDocumentsByType(transactionId=%s, service=%s, types=[%s])",
                                transactionId, service, types);
            }
        };
        return t.execute();
    }

    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */
    private List<IndexEntry> getListIndex(IECMService svc, String transactionId) {
        List<IndexEntry> result = new ArrayList<IndexEntry>();
        com.dictao.dtp.core.services.ecm.IndexEntry[] entries = svc.getIndexedDocumentList(transactionId);
        if (entries != null && entries.length > 0) {
            for (com.dictao.dtp.core.services.ecm.IndexEntry entry : entries) {
                IndexEntry ie = new IndexEntry();
                ie.setFilename(entry.getDocumentFilename());
                ie.setType(entry.getType());
                result.add(ie);
            }
        }
        return result;
    }

    private ECMDocument DocumentToECMDocument(Document doc) {
        String filename = doc.getFilename();
        String contentMimeType = doc.getMimetype();
        byte[] content = doc.getContent();
        ECMDocument result;
        Element md = null;
        if (doc.getMetadata() != null) {
            md = doc.getMetadata().getAny();
        }
        result = new ECMDocument(filename, contentMimeType, content, md);
        result.setLabel(doc.getLabel());
        result.setDescription(doc.getDescription());
        result.setSignatures(SignaturesConverter.WSSignaturesToSignature(doc.getSignatures()));
        return result;
    }

    private Document ECMDocumentToDocument(ECMDocument doc,
            String transactionId, String service) {
        Document result = new Document();
        result.setFilename(doc.getFilename());
        result.setMimetype(doc.getContentMimeType());
        result.setLabel(doc.getLabel());
        result.setDescription(doc.getDescription());
        result.setSignatures(SignaturesConverter.SignatureToWSSignatures(doc.getSignatures()));
        
        byte[] data;
        try {
            data = new byte[doc.readContent().available()];
            doc.readContent().read(data);
        } catch (IOException ex) {
            throw new EnvironmentException(
                    ex,
                    EnvironmentException.Code.DTP_ENV_DATABASE,
                    "Unable to access to content of document '%s' (service '%s', transactionId '%s')",
                    doc.getFilename(), service, transactionId);
        }
        result.setContent(data);
        // add metadata
        if (null != doc.getXmlDocMetadata()) {
            Metadata md = new Metadata();
            md.setAny(doc.getXmlDocMetadata());
            result.setMetadata(md);
        }
        return result;
    }
    
    private void checkIfCancelledOrExpired(final String transactionId) {
        TransactionHandler handler = getTransactionFactory().find(transactionId, getSSLCertificate());

        if (handler.getDatabaseTransaction().getSubStatus() == TransactionSubStatusEnum.Cancelled) {
            throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                    "Unable to get document on a cancelled transaction (id [%s])",
                    transactionId);
        }

        if (handler.getDatabaseTransaction().getSubStatus() == TransactionSubStatusEnum.Expired) {
            throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                    "Unable to get document on an expired transaction (id [%s])",
                    transactionId);
        }

    }

}