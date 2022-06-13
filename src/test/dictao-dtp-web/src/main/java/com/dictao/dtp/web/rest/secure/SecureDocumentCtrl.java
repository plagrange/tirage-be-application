package com.dictao.dtp.web.rest.secure;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.transactions.TransactionContext;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.TransactionSubStatusEnum;
import com.dictao.dtp.web.rest.BaseDocumentCtrl;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.security.cert.X509Certificate;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

@RequestScoped
@Path("/document/v2012_07/{ecmServiceName}/{transactionId}")
public class SecureDocumentCtrl extends BaseDocumentCtrl {

    /*
     * ********************************************************************
     * CONSTANTS 
     * ********************************************************************
     */
    private static final Logger LOG = LoggerFactory.getLogger(SecureDocumentCtrl.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + SecureDocumentCtrl.class.getName());
    /*
     * ********************************************************************
     */
    /*
     * ATTRIBUTES
     */
    /*
     * ********************************************************************
     */
    @Inject
    private TransactionFactory txFactory;

    /*
     * ********************************************************************
     */
    /*
     * PUBLIC METHODS
     */
    /*
     * ********************************************************************
     */
    
    /*
     * Default constructor.
     */
    public SecureDocumentCtrl() {
        
    }
    
    /*
     * Constructor for test only.
     */
    public SecureDocumentCtrl(TransactionFactory txFactory) {
        this.txFactory = txFactory;
    }
    
    /**
     *
     * Retrieve a document by ECM service name, txId and document name. This method MUST be secured by a client
     * certificate access.
     *
     * @param ecmServiceName
     * @param transactionId
     * @param documentName
     * @param request
     * @param servletContext
     * @param disposition
     * @param outputMimeType
     * @param transform
     * @return
     * @throws IOException
     */
    @GET
    @Path("{documentName}")
    public Response getDocument(
            @PathParam("ecmServiceName") String ecmServiceName,
            @PathParam("transactionId") String transactionId,
            @PathParam("documentName") String documentName,
            @Context HttpServletRequest request,
            @Context ServletContext servletContext,
            @QueryParam(URL_PARAM_DISPOSITION) @DefaultValue(URL_PARAM_DISPOSITION_VALUE_INLINE) String disposition,
            @QueryParam(URL_PARAM_FORMAT) String outputMimeType,
            @QueryParam(URL_PARAM_TRANSFORM) String transform) throws IOException {

        X509Certificate certificate = getSSLCertificate(request);
        if (null == certificate) {
            throw new UserException(
                    UserException.Code.DTP_USER_UNAUTHORIZED,
                    "insufficient access conditions in url : %s",
                    request.getPathInfo());
        }

        try {

            TransactionContext.set(certificate);

            IECMService ecm = (IECMService) txFactory.getService(transactionId, ecmServiceName, certificate);
            if (ecm == null) {
                throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                        "ECM service %s not found", ecmServiceName);
            }

            TransactionHandler txHandler = txFactory.find(transactionId, certificate);
            if ((txHandler == null) || (txHandler.getDatabaseTransaction() == null)) {
                throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                        "Transaction id [%s] not found", transactionId);
            }
            
            RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(),
                request.getRemoteHost(), request.getRemotePort());
            txHandler.setRequestInfo(requestInfo);

            // Check access rights
            // No getDocument on a cancelled transaction
            if (txHandler.getDatabaseTransaction().getSubStatus().equals(TransactionSubStatusEnum.Cancelled)) {
                throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                        "Unable to get document on a cancelled cancelled transaction (id [%s])", transactionId);
            }

            // No getDocument on an expired transaction
            if (txHandler.getDatabaseTransaction().getSubStatus().equals(TransactionSubStatusEnum.Expired)) {
                throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                        "Unable to get document on an expired transaction (id [%s])", transactionId);
            }

            if (!(disposition.equals(URL_PARAM_DISPOSITION_VALUE_INLINE) || disposition.equals(URL_PARAM_DISPOSITION_VALUE_ATTACHMENT))) {
                disposition = URL_PARAM_DISPOSITION_VALUE_INLINE;
            }

            ECMDocument document = ecm.get(transactionId, documentName);
            if (document == null) {
                throw new UserException(
                        UserException.Code.DTP_USER_NOT_FOUND,
                        "Document not found, transactionId='%s', documentName='%s'",
                        transactionId, documentName);
            }
            String docFileName = document.getFilename();
            InputStream input = document.readContent();
            String contentDisposition;
            contentDisposition = disposition + ";filename=\"" + docFileName + "\"";

            if (transform != null && outputMimeType != null) {
                input = transform(input, transform, outputMimeType);
            } else {
                outputMimeType = document.getContentMimeType();
            }
            
            ResponseBuilder response = getStreamResponseBuilder(input, contentDisposition, outputMimeType);
            input.close();
            
            AddServiceDocumentViewedStep(txHandler, document, ecm);
            
            return response.build();
        } finally {
            TransactionContext.unset();
        }
    }

    /**
     * Retrieve a zip containing all document form a transaction by ECM service name, txId. This method MUST be secured
     * by a client certificate access.
     *
     * @param ecmServiceName
     * @param transactionId
     * @param request
     * @return
     * @throws IOException
     */
    @GET
    public Response getAllDocumentZipped(
            @PathParam("ecmServiceName") String ecmServiceName,
            @PathParam("transactionId") String transactionId,
            @Context HttpServletRequest request) throws IOException {

        X509Certificate certificate = getSSLCertificate(request);
        if (null == certificate) {
            throw new UserException(
                    UserException.Code.DTP_USER_UNAUTHORIZED,
                    "insufficient access conditions in url : %s",
                    request.getPathInfo());
        }
        try {
            TransactionContext.set(certificate);

            IECMService ecm = (IECMService) txFactory.getService(transactionId, ecmServiceName, certificate);
            if (ecm == null) {
                throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                        "ECM service %s not found", ecmServiceName);
            }

            TransactionHandler txHandler = txFactory.find(transactionId, certificate);
            if ((txHandler == null) || (txHandler.getDatabaseTransaction() == null)) {
                throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                        "Transaction id [%s] not found", transactionId);
            }
            
            RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(),
                request.getRemoteHost(), request.getRemotePort());
            txHandler.setRequestInfo(requestInfo);

            // Check access rights
            // No getDocument on a cancelled transaction
            if (txHandler.getDatabaseTransaction().getSubStatus().equals(TransactionSubStatusEnum.Cancelled)) {
                throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                        "Unable to get document on a cancelled cancelled transaction (id [%s])", transactionId);
            }

            // No getDocument on an expired transaction
            if (txHandler.getDatabaseTransaction().getSubStatus().equals(TransactionSubStatusEnum.Expired)) {
                throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                        "Unable to get document on an expired transaction (id [%s])", transactionId);
            }

            String docFileName = transactionId + ".zip";
            List<String> listDocsName = ecm.getDocumentList(transactionId);
            InputStream input = ecm.getDocumentsAsZip(transactionId, listDocsName);

            String contentDisposition;
            contentDisposition = "attachment;filename=\"" + docFileName + "\"";
            String contentType = "application/zip";
            ResponseBuilder response = getStreamResponseBuilder(input, contentDisposition, contentType);
            input.close();
            
            return response.build();
        } finally {
            TransactionContext.unset();
        }
    }
}
