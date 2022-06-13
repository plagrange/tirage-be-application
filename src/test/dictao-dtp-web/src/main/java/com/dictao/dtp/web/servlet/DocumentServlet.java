package com.dictao.dtp.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.log4j.MDC;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.TransactionStatusEnum;
import com.dictao.dtp.persistence.entity.TransactionSubStatusEnum;
import com.dictao.util.convert.Base64;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import javax.inject.Inject;

/**
 * Servlet to download and upload files.
 *
 * @author glaurent
 */
@Deprecated
public class DocumentServlet extends DocumentServletBase {

    /*
     * **************************** CONSTANTS ***************************
     */
    private static final Logger LOG = LoggerFactory.getLogger(DocumentServlet.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + DocumentServlet.class.getName());
    private static final long serialVersionUID = -3647298593522205507L;
    public static final String SIGNATURE_VALUE = "SIGNATURE_VALUE";
    @Inject
    private ApplicationListConf configuration;

    /*
     * **************************** ATTRIBUTES **************************
     */

    /*
     * ************************** PUBLIC METHODS ************************
     */
    /**
     * Compute the url to access/download a document stored in an ecm service The user access id (or external user
     * access id) are mandatory if the access should be done through SSL server only. If none of them provided, the
     * access will be done through HTTP SSL mutul
     *
     * @param pathInfo pathInfo to the document servlet (relative or absolute)
     * @param appId applicationId
     * @param serviceName ecm service name
     * @param transactionId transactionId
     * @param documentFileName document filename
     * @param accessId user access id (optional)
     * @param externalAccessId external user access id (optional)
     * @return
     * @throws URIException
     */
    public static String getDocumentUrl(
            String pathInfo,
            String tenant,
            String appName,
            String serviceName,
            String transactionId,
            String documentFileName,
            String type,
            String accessId, String externalAccessId) throws URIException {


        if (!pathInfo.endsWith("/")) {
            pathInfo += "/";
        }
        String url = URIUtil.encodePath(pathInfo + serviceName + "/" + transactionId + "/" + documentFileName);
        HttpMethod method = new GetMethod(url);
        List<NameValuePair> parameters = new ArrayList<NameValuePair>();

        if (accessId != null) {
            parameters.add(new NameValuePair(URL_PARAM_USERACCESS_ID, accessId));

            parameters.add(new NameValuePair(URL_PARAM_APPLICATION_NAME, appName));
            parameters.add(new NameValuePair(URL_PARAM_TENANT_NAME, tenant));
        }
        if (externalAccessId != null && accessId == null) {
            parameters.add(new NameValuePair(URL_PARAM_EXTERNALE_USERACCESS_AID, accessId));
            parameters.add(new NameValuePair(URL_PARAM_APPLICATION_NAME, appName));
        }
        if (type != null) {
            parameters.add(new NameValuePair("type", type));
        }
        method.setQueryString(parameters.toArray(new NameValuePair[]{}));
        return method.getURI().getEscapedURI();
    }
    /*
     * ********************* PROTECTED/PRIVATE METHODS ******************
     */

    /**
     * Http GET for download a file, you need build a url like :
     * base_url/service_ecm_name/transaction_id/document_name<BR>
     *
     * If no document_name, a ZIP file is created with all directory content.<BR>
     *
     * you can specify url parameters :<BR> - aid : (access_id) need for no authenticated access to documents <BR> -
     * format : mime type for the transformation<BR> - disposition : inline (visualisation) or attachment (download).
     */
    @Override
    protected void doGet(final HttpServletRequest request,
            final HttpServletResponse response) throws ServletException, IOException {
        new ServletTask(response) {

            @Override
            public void run() throws Exception {
                // Parameter extraction

                String[] params = URLDecoder.decode(request.getPathInfo(), "UTF-8").substring(1).split("/");
                if (params.length < 2) {
                    throw new UserException(
                            UserException.Code.DTP_USER_INVALID_PARAMETER,
                            "Error decode url parameters: %s",
                            request.getPathInfo());
                }

                String serviceName = params[0];
                String transactionId = params[1];
                if (transactionId != null) {
                    MDC.put("TID", transactionId);
                }
                String documentName = params.length > 2 ? params[2] : null;
                String accessId = request.getParameter(URL_PARAM_USERACCESS_ID);
                if (accessId != null) {
                    MDC.put("AID", accessId);
                }
                String externalAccessId = request.getParameter(URL_PARAM_EXTERNALE_USERACCESS_AID);
                String tenant = request.getParameter(URL_PARAM_TENANT_NAME);
                String appName = request.getParameter(URL_PARAM_APPLICATION_NAME);

                String outputMimeType = request.getParameter(URL_PARAM_FORMAT);
                String disposition = request.getParameter(URL_PARAM_DISPOSITION);
                if (outputMimeType != null) {
                    outputMimeType = URLDecoder.decode(outputMimeType, "UTF-8");
                }
                if (disposition != null) {
                    disposition = URLDecoder.decode(disposition, "UTF-8");
                    if (!(disposition.equals(URL_PARAM_DISPOSITION_VALUE_INLINE) || disposition.equals(URL_PARAM_DISPOSITION_VALUE_ATTACHMENT))) {
                        disposition = URL_PARAM_DISPOSITION_VALUE_INLINE;
                    }
                } else {
                    disposition = URL_PARAM_DISPOSITION_VALUE_INLINE;
                }
                String transform = request.getParameter(URL_PARAM_TRANSFORM);
                if (transform != null) {
                    transform = getInitParameter(URLDecoder.decode(transform,
                            "UTF-8"));
                }

                // Retrieve txHandler And ecm

                IECMService ecm = null;
                TransactionFactory txSvc = getTransactionFactory();
                X509Certificate certificate = getSSLCertificate(request);
                TransactionHandler txHandler;
                if (null != certificate) {
                    ecm = (IECMService) txSvc.getService(transactionId,
                            serviceName, certificate);
                    txHandler = txSvc.find(transactionId, certificate);
                } else if (null != accessId) {
                    ecm = (IECMService) txSvc.getService(transactionId,
                            accessId, serviceName);
                    txHandler = txSvc.find(accessId);
                } else if (null != externalAccessId && null != tenant && null != appName) {
                    //String appId = configuration.getConfigurationIDAssociated(tenant, appName);
                    ecm = (IECMService) txSvc.getService(tenant, appName, transactionId,
                            serviceName, externalAccessId);
                    txHandler = txSvc.find(tenant, appName, externalAccessId);
                } else {
                    throw new UserException(
                            UserException.Code.DTP_USER_UNAUTHORIZED,
                            "insufficient access conditions in url : %s",
                            request.getPathInfo());
                }

                if (ecm == null) {
                    throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                            "ECM service %s not found", serviceName);
                }
                if ((txHandler == null) || (txHandler.getDatabaseTransaction() == null)) {
                    throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                            "Transaction id [%s] not found", transactionId);
                }

                // Check user access validity
                if ((null == certificate) && !txHandler.isValidUserAccess()) {
                    throw new UserException(UserException.Code.DTP_USER_NOT_FOUND,
                            "Transaction id [%s] not found. User access expired", transactionId);
                }

                // Check access rights

                if (null != certificate) { // Transaction retrieved using client certificate

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

                } else { // Transaction retrieved using accessId or externalAccessId

                    // No getDocument on a closed tx for UserAccess
                    if ((txHandler.getDatabaseTransaction().getStatus() == TransactionStatusEnum.Closed)
                            && (certificate == null)) {
                        throw new UserException(
                                UserException.Code.DTP_USER_UNAUTHORIZED,
                                "Unable to get document on a closed transaction (id [%s])",
                                transactionId);
                    }

                }

                if (documentName != null) {
                    if (tryServeDocumentViewing(request, response, ecm, txHandler, documentName)) {
                        // Content served by tryServeDocumentViewing
                    } else {
                        // Serve a single document
                        serveECMDocument(request, response, ecm, transactionId,
                                documentName, disposition, transform, outputMimeType);
                    }
                } else {

                    // Serve the 
                    String docFileName = transactionId + ".zip";

                    List<String> listDocsName = ecm.getDocumentList(transactionId);

                    InputStream input = ecm.getDocumentsAsZip(transactionId, listDocsName);

                    response.reset();
                    ServletOutputStream output = response.getOutputStream();

                    String contentDisposition;
                    contentDisposition = disposition + ";filename=\"" + docFileName
                            + "\"";
                    String contentType = "application/zip";

                    response.setHeader("Content-Disposition", contentDisposition);
                    response.setContentType(contentType);

                    copyAll(input, output);

                    output.flush();
                    output.close();
                }
            }
        }.execute();
    }
}
