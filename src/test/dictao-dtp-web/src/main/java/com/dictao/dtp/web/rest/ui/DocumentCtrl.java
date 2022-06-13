package com.dictao.dtp.web.rest.ui;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.EnvironmentException.Code;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.services.rendering.DocRenderingService;
import com.dictao.dtp.core.services.rendering.RenderingData;
import com.dictao.dtp.core.transactions.IDocumentProofResolver;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.TransactionStatusEnum;
import com.dictao.dtp.web.rest.BaseDocumentCtrl;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

@RequestScoped
@Path("/{ecmServiceName}")
public class DocumentCtrl extends BaseDocumentCtrl {

    /*
     * ********************************************************************
     * CONSTANTS
     * ********************************************************************
     */
    private static final Logger LOG = LoggerFactory
            .getLogger(DocumentCtrl.class);
    
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + DocumentCtrl.class.getName());

    private static final String DUMMY = "dummy";

    private static final SimpleDateFormat HTTP_EXPIRY_DATE_FORMAT = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
    
    static {
        HTTP_EXPIRY_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("GMT"));
    }
    
    /*
     * ********************************************************************
     * ATTRIBUTES
     * ********************************************************************
     */
    @Inject
    private TransactionFactory txFactory;

    /*
     * ********************************************************************
     * PUBLIC METHODS
     * ********************************************************************
     */
    /*
     * Default constructor.
     */
    public DocumentCtrl() {
    }

    /*
     * Constructor for test only.
     */
    public DocumentCtrl(TransactionFactory txFactory) {
        this.txFactory = txFactory;
    }

    /**
     * 
     * Retrieve a document by ECM service name, accessId and document name. No
     * client certificate required.
     * 
     */
    @GET
    @Path("{accessId}/{documentName}")
    public Response getDocumentWithAccessId(
            @PathParam("ecmServiceName") String ecmServiceName,
            @PathParam("accessId") String accessId,
            @PathParam("documentName") String documentName,
            @Context HttpServletRequest request,
            @Context ServletContext servletContext,
            @QueryParam(URL_PARAM_DISPOSITION) @DefaultValue(URL_PARAM_DISPOSITION_VALUE_INLINE) String disposition,
            @QueryParam(URL_PARAM_FORMAT) String outputMimeType,
            @QueryParam(URL_PARAM_TRANSFORM) String transform)
            throws IOException {

        // Find transaction by aid
        TransactionHandler txHandler = txFactory.find(accessId);
        if ((txHandler == null) || (txHandler.getDatabaseTransaction() == null)) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Transaction not found [aid='%s']", accessId);
        }

        // Check user access validity
        if (!txHandler.isValidUserAccess()) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Transaction id [aid='%s'] not found. User access expired",
                    accessId);
        }

        RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(),
                request.getRemoteHost(), request.getRemotePort());
        txHandler.setRequestInfo(requestInfo);

        IECMService ecm = (IECMService) txFactory.getService(
                txHandler.getTransactionId(), accessId, ecmServiceName);
        if (ecm == null) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "ECM service %s not found", ecmServiceName);
        }

        // Check access rights
        // No getDocument on a closed tx for UserAccess
        if (txHandler.getDatabaseTransaction().getStatus() == TransactionStatusEnum.Closed) {
            throw new UserException(
                    UserException.Code.DTP_USER_UNAUTHORIZED,
                    "Unable to get document on a closed transaction [aid='%s']",
                    accessId);
        }

        if (!(disposition.equals(URL_PARAM_DISPOSITION_VALUE_INLINE) || disposition
                .equals(URL_PARAM_DISPOSITION_VALUE_ATTACHMENT))) {
            disposition = URL_PARAM_DISPOSITION_VALUE_INLINE;
        }
        ECMDocument document = txHandler.getDocumentFromUserAccess(ecm,
                documentName);
        if (document == null) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Document not found, aid='%s', documentName='%s'",
                    accessId, documentName);
        }

        String docFileName = checkFilename(disposition, document.getFilename());
        InputStream input = document.readContent();
        String contentDisposition;
        contentDisposition = disposition + ";filename=\"" + docFileName + "\"";

        if (transform != null && outputMimeType != null) {
            input = transform(input, transform, outputMimeType);
        } else {
            outputMimeType = document.getContentMimeType();
        }
        ResponseBuilder response = getStreamResponseBuilder(input,
                contentDisposition, outputMimeType);
        input.close();

        AddServiceDocumentViewedStep(txHandler, document, ecm);

        return response.build();
    }

    /**
     * 
     * Retrieve a document rendering data by ECM service name, accessId and document name. No
     * client certificate required.
     * 
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Path("{accessId}/{documentName}/index")
    public Response getDocRenderingDataWithAccessId(
            @PathParam("ecmServiceName") String ecmServiceName,
            @PathParam("accessId") String accessId,
            @PathParam("documentName") String documentName,
            @Context HttpServletRequest request,
            @Context ServletContext servletContext,
            @QueryParam(DUMMY) String dummy)
            throws IOException {

        // Find transaction by aid
        TransactionHandler txHandler = txFactory.find(accessId);
        if ((txHandler == null) || (txHandler.getDatabaseTransaction() == null)) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Transaction not found [aid='%s']", accessId);
        }

        // Check user access validity
        if (!txHandler.isValidUserAccess()) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Transaction id [aid='%s'] not found. User access expired",
                    accessId);
        }

        RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(),
                request.getRemoteHost(), request.getRemotePort());
        txHandler.setRequestInfo(requestInfo);

        IECMService ecm = (IECMService) txFactory.getService(
                txHandler.getTransactionId(), accessId, ecmServiceName);
        if (ecm == null) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "ECM service %s not found", ecmServiceName);
        }

        // Check access rights
        // No getDocument on a closed tx for UserAccess
        if (txHandler.getDatabaseTransaction().getStatus() == TransactionStatusEnum.Closed) {
            throw new UserException(
                    UserException.Code.DTP_USER_UNAUTHORIZED,
                    "Unable to get document on a closed transaction [aid='%s']",
                    accessId);
        }

        ECMDocument document = txHandler.getDocumentFromUserAccess(ecm,
                documentName);
        if (document == null) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Document not found, aid='%s', documentName='%s'",
                    accessId, documentName);
        }

        final DocRenderingService renderingService = ecm.getRenderer();
        if (renderingService == null) {
            throw new EnvironmentException(Code.DTP_ENV_CONFIGURATION, "No rendering service configured");
        }

        // Try retrieving validation proof for the requested document
        IDocumentProofResolver documentProofResolver = txHandler.getDocumentProofResolver();
        String proofFileName = documentProofResolver.getDocumentProofFilename(txHandler, ecm, document);

        InputStream validationProof = null;
        if (proofFileName != null) {
            ECMDocument proofDocument = ecm.get(txHandler.getTransactionId(), proofFileName);
            if (null != proofDocument)
                validationProof = proofDocument.readContent();
        }
        /* TODO KCH to uncomment technical specifications will be ready 
        if (null == validationProof){
            // retrieves last/recent corresponding Proof to the provided document from database or by calling validation service.
            validationProof = txHandler.retrieveRecentProof(document);
        }
        */

        Locale locale = request.getLocale();
        // PersonalInfo can't be null
        String user = txHandler.getDatabaseUserAccess().getPersonalInfo().getUser();
        RenderingData renderingData = renderingService.getRenderingData(ecm, txHandler.getTransactionId(), user, document, validationProof, locale);

        ResponseBuilder response = Response.ok(renderingData.toJson());
        AddServiceDocumentViewedStep(txHandler, document, ecm);

        return response.build();
    }

    /**
     * 
     * Retrieve a document rendering page by ECM service name, accessId, document name, page and rendering id. No
     * client certificate required.
     * 
     */
    @GET
    @Path("{accessId}/{documentName}/{page}/{renderingId}.png")
    public Response getDocRenderingPageWithAccessId(
            @PathParam("ecmServiceName") String ecmServiceName,
            @PathParam("accessId") String accessId,
            @PathParam("documentName") String documentName,
            @PathParam("page") int page,
            @PathParam("renderingId") long renderingId,
            @Context HttpServletRequest request,
            @Context ServletContext servletContext)
            throws IOException {

        // Find transaction by aid
        TransactionHandler txHandler = txFactory.find(accessId);
        if ((txHandler == null) || (txHandler.getDatabaseTransaction() == null)) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Transaction not found [aid='%s']", accessId);
        }

        // Check user access validity
        if (!txHandler.isValidUserAccess()) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Transaction id [aid='%s'] not found. User access expired",
                    accessId);
        }

        RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(),
                request.getRemoteHost(), request.getRemotePort());
        txHandler.setRequestInfo(requestInfo);

        IECMService ecm = (IECMService) txFactory.getService(
                txHandler.getTransactionId(), accessId, ecmServiceName);
        if (ecm == null) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "ECM service %s not found", ecmServiceName);
        }

        // Check access rights
        // No getDocument on a closed tx for UserAccess
        if (txHandler.getDatabaseTransaction().getStatus() == TransactionStatusEnum.Closed) {
            throw new UserException(
                    UserException.Code.DTP_USER_UNAUTHORIZED,
                    "Unable to get document on a closed transaction [aid='%s']",
                    accessId);
        }

        ECMDocument document = txHandler.getDocumentFromUserAccess(ecm,
                documentName);
        if (document == null) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "Document not found, aid='%s', documentName='%s'",
                    accessId, documentName);
        }

        final DocRenderingService renderingService = ecm.getRenderer();
        if (renderingService == null) {
            throw new EnvironmentException(Code.DTP_ENV_CONFIGURATION, "No rendering service configured");
        }

        byte[] imageByte = renderingService.getRenderingImage(ecm, txHandler.getTransactionId(), document, renderingId, page);

        InputStream in = new ByteArrayInputStream(imageByte);
        ResponseBuilder response = getStreamResponseBuilder(in, "inline", "image/png");
        Calendar c = Calendar.getInstance();
        c.roll(Calendar.DAY_OF_MONTH, 1);
        synchronized (HTTP_EXPIRY_DATE_FORMAT) {
            response.header(HttpHeaders.EXPIRES, HTTP_EXPIRY_DATE_FORMAT.format(c.getTime())); 
        }
        in.close();

        return response.build();
    }

    /**
     * 
     * Retrieve a document by ECM service name, externalAccessId and document
     * name. No client certificate required.
     * 
     */
    @GET
    @Path("{tenantName}/{applicationName}/{externalAccessId}/{documentName}")
    public Response getDocumentWithExternalAccessId(
            @PathParam("ecmServiceName") String ecmServiceName,
            @PathParam("tenantName") String tenantName,
            @PathParam("applicationName") String applicationName,
            @PathParam("externalAccessId") String externalAccessId,
            @PathParam("documentName") String documentName,
            @Context HttpServletRequest request,
            @Context ServletContext servletContext,
            @QueryParam(URL_PARAM_DISPOSITION) @DefaultValue(URL_PARAM_DISPOSITION_VALUE_INLINE) String disposition,
            @QueryParam(URL_PARAM_FORMAT) String outputMimeType,
            @QueryParam(URL_PARAM_TRANSFORM) String transform)
            throws IOException {

        // Find transaction by tenantName, appName & eaid
        TransactionHandler txHandler = txFactory.find(applicationName,
                tenantName, externalAccessId);
        if ((txHandler == null) || (txHandler.getDatabaseTransaction() == null)) {
            throw new UserException(
                    UserException.Code.DTP_USER_NOT_FOUND,
                    "Transaction not found [tenant='%s', appName='%s', aid='%s']",
                    tenantName, applicationName, externalAccessId);
        }

        // Check user access validity
        if (!txHandler.isValidUserAccess()) {
            throw new UserException(
                    UserException.Code.DTP_USER_NOT_FOUND,
                    "Transaction id [tenant='%s', appName='%s', aid='%s'] not found. User access expired",
                    tenantName, applicationName, externalAccessId);
        }

        RequestInfo requestInfo = new RequestInfo(request.getRemoteAddr(),
                request.getRemoteHost(), request.getRemotePort());
        txHandler.setRequestInfo(requestInfo);

        IECMService ecm = (IECMService) txFactory.getService(tenantName,
                applicationName, txHandler.getTransactionId(), ecmServiceName,
                externalAccessId);
        if (ecm == null) {
            throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                    "ECM service %s not found", ecmServiceName);
        }

        // Check access rights
        // No getDocument on a closed tx for UserAccess
        if (txHandler.getDatabaseTransaction().getStatus() == TransactionStatusEnum.Closed) {
            throw new UserException(
                    UserException.Code.DTP_USER_UNAUTHORIZED,
                    "Unable to get document on a closed transaction [tenant='%s', appName='%s', aid='%s']",
                    tenantName, applicationName, externalAccessId);
        }

        if (!(disposition.equals(URL_PARAM_DISPOSITION_VALUE_INLINE) || disposition
                .equals(URL_PARAM_DISPOSITION_VALUE_ATTACHMENT))) {
            disposition = URL_PARAM_DISPOSITION_VALUE_INLINE;
        }

        ECMDocument document = txHandler.getDocumentFromUserAccess(ecm,
                documentName);
        if (document == null) {
            throw new UserException(
                    UserException.Code.DTP_USER_NOT_FOUND,
                    "Document not found, [tenant='%s', appName='%s', aid='%s', documentName='%s']",
                    tenantName, applicationName, externalAccessId, documentName);
        }

        String docFileName = checkFilename(disposition, document.getFilename());
        InputStream input = document.readContent();
        String contentDisposition;
        contentDisposition = disposition + ";filename=\"" + docFileName + "\"";

        if (transform != null && outputMimeType != null) {
            input = transform(input, transform, outputMimeType);
        } else {
            outputMimeType = document.getContentMimeType();
        }

        ResponseBuilder response = getStreamResponseBuilder(input,
                contentDisposition, outputMimeType);
        input.close();

        AddServiceDocumentViewedStep(txHandler, document, ecm);

        return response.build();
    }

    /**
     * 
     * 
     * @param path
     * @param ecm
     * @param aid
     * @param filename
     * @return
     */
    public static String getDocUrl(String path, String ecm, String aid,
            String filename) {
        String url = "";
        if (path.endsWith("/")) {
            url = path + "ui/document/" + ecm + "/" + aid + "/" + filename;
        } else {
            url = path + "/ui/document/" + ecm + "/" + aid + "/" + filename;
        }

        return url;
    }

    /**
     * 
     * 
     * @param path
     * @param tenant
     * @param appName
     * @param aid
     * @return
     */
    public static String getPostUrl(String path, String tenant, String appName,
            String aid) {
        String url = "";
        if (path.endsWith("/")) {
            url = path + "ui/" + tenant + "/" + appName + "/" + aid + "/";
        } else {
            url = path + "/ui/" + tenant + "/" + appName + "/" + aid + "/";
        }

        return url;
    }

    /**
     * to prevent an observable bug on IE8, if disposition equals 'inline' and
     * filename contains 'attachment'. <br>
     * More details: https://jira.dictao.com/browse/DTPJAVA-1078
     * 
     * @param disposition
     * @param filename
     * @return
     */
    private String checkFilename(String disposition, String filename) {
        if (disposition.equals(URL_PARAM_DISPOSITION_VALUE_INLINE)
                && filename.contains(URL_PARAM_DISPOSITION_VALUE_ATTACHMENT))

            return filename.replaceAll(URL_PARAM_DISPOSITION_VALUE_ATTACHMENT,
                    "pj");

        return filename;

    }
}
