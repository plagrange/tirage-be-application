package com.dictao.dtp.web.servlet;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.web.proofexplorer.SessionEcmZipService;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;

/**
 * Servlet class for offline proof visualization.</BR>
 * Supports proof zip upload, and single zip entries visualization & download.
 */
public class ZipProofDocumentServlet extends DocumentServletBase {

    private static final Logger LOG = LoggerFactory.getLogger(ZipProofDocumentServlet.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + ZipProofDocumentServlet.class.getName());

    /**
     * GET verb can be used to download individual files from a proof zip.</BR>
     * Syntax is {path}/transactionId/documentName</BR>
     * Optional parameters are : </BR>
     *     disposition=inline|attachment</BR>
     *     transform=transform name (audit, signature...). (xslt id defined in web.xml)</BR>
     *     format=output mimetype</BR>
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        LOG.entering();
        try {

            // Parse parameters
            String[] params = URLDecoder.decode(request.getPathInfo(), "UTF-8").substring(1).split("/");
            if (params.length != 2) {
                throw new UserException(
                        UserException.Code.DTP_USER_INVALID_PARAMETER,
                        "Error decoding url parameters: %s",
                        request.getPathInfo());
            }

            String transactionId = params[0];
            String documentName = params[1];

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

            // Use getReference() to have an instance "explicitly" injected.
            IECMService ecm = getReference(SessionEcmZipService.class);

            if (ecm == null) {
                throw new EnvironmentException(EnvironmentException.Code.DTP_ENV_PROOF_VISUALIZATION,
                        "Zip ECM not loaded");
            }

            // Serve the document...
            serveECMDocument(request, response, ecm, transactionId,
                    documentName, disposition, transform, outputMimeType);

        } catch (UserException ex) {
            LOGU.error(ex);
            sendError(response, HttpServletResponse.SC_BAD_REQUEST);
        } catch (EnvironmentException ex) {
            LOG.error(ex);
            sendError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (Exception ex) {
            LOG.error(ex);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        LOG.exiting();
    }

    /**
     * POST verb allows uploading of proof zip archives.</BR>
     * Expected parameters (in multipart/form-data format)</BR>
     *  > 1 zip file stream</BR>
     *  > redirect-url parameter : url of proof visualisation page</BR>
     *         on success, an http redirect on {redirect-url}[&|?]mode=zip&tx=transactionId</BR>
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        LOG.entering();

        try {

            // Use getReference() to have an instance "explicitly" injected.
            SessionEcmZipService ecmZip = getReference(SessionEcmZipService.class);

            ServletFileUpload upload = new ServletFileUpload();
            FileItemIterator iter = upload.getItemIterator(request);

            String redirectUrl = null;
            String zipId = null;

            while (iter.hasNext()) {

                FileItemStream item = iter.next();

                if (item.isFormField()) { // regular form parameter
                    if (item.getFieldName().equals("redirect-url")) {
                        InputStream is = item.openStream();
                        redirectUrl = Streams.asString(is);
                    } else {
                        LOG.error("Unexpected parameter '%s'", item.getFieldName());
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST);
                        return;
                    }
                } else { // File upload parameter

                    if (zipId == null) {
                        // Create a new transaction
                        InputStream zipInputStream = item.openStream();
                        zipId = ecmZip.AddTransactionFromZip(zipInputStream);
                        zipInputStream.close();
                    } else {
                        LOG.error("Multiple zip upload not supported");
                        sendError(response, HttpServletResponse.SC_BAD_REQUEST);
                    }
                }
            }

            if (null == zipId) {
                throw new UserException(UserException.Code.DTP_USER_MISSING_PARAMETER, "No zip file provided...");
            }

            if (null == redirectUrl) {
                throw new UserException(UserException.Code.DTP_USER_MISSING_PARAMETER, "No redirect url provided...");
            }

            // Everything's OK.

            // Redirect to proof visualization page
            if (redirectUrl.contains("?")) {
                redirectUrl += "&";
            } else {
                redirectUrl += "?";
            }

            redirectUrl += "mode=zip&tx=" + zipId;
            response.sendRedirect(redirectUrl);

        } catch (FileUploadException ex) {
            LOGU.error(ex);
            sendError(response, HttpServletResponse.SC_BAD_REQUEST);
        } catch (UserException ex) {
            LOGU.error(ex);
            sendError(response, HttpServletResponse.SC_BAD_REQUEST);
        } catch (EnvironmentException ex) {
            LOG.error(ex);
            sendError(response, HttpServletResponse.SC_SERVICE_UNAVAILABLE);
        } catch (Exception ex) {
            LOG.error(ex);
            sendError(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

        LOG.exiting();
    }
}
