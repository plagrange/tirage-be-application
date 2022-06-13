package com.dictao.dtp.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Locale;

import javax.activation.MimeType;
import javax.activation.MimeTypeParseException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.dictao.dtp.core.api.converting.Converter;
import com.dictao.dtp.core.api.converting.ConverterFactory;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.EnvironmentException.Code;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.services.rendering.DocRenderingService;
import com.dictao.dtp.core.services.rendering.RenderingData;
import com.dictao.dtp.core.transactions.IDocumentProofResolver;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.Transaction;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

@Deprecated
public abstract class DocumentServletBase extends Servlet {

    /*
     * **************************** CONSTANTS ***************************
     */
    private static final Logger LOG = LoggerFactory.getLogger(DocumentServletBase.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + DocumentServletBase.class.getName());
    private static final String MIME_TYPE_X_PNG = "image/x-png";
    private static final String MIME_TYPE_PNG = "image/png";
    private static final String MIME_TYPE_PJPEG = "image/pjpeg";
    private static final String MIME_TYPE_JPEG = "image/jpeg";
    private static final long serialVersionUID = -1246852549654566897L;
    protected static final int DEFAULT_BUFFER_SIZE = 20480; // 20KB//10240; // ..bytes = 10KB.
    protected static final String SEAL = "SEAL";
    protected static final String URL_PARAM_USERACCESS_ID = "aid";
    protected static final String URL_PARAM_EXTERNALE_USERACCESS_AID = "eaid";
    protected static final String URL_PARAM_APPLICATION_NAME = "appName";
    protected static final String URL_PARAM_TENANT_NAME = "tenantName";
    protected static final String URL_PARAM_FORMAT = "format";
    protected static final String URL_PARAM_DISPOSITION = "disposition";
    protected static final String URL_PARAM_DISPOSITION_VALUE_INLINE = "inline";
    protected static final String URL_PARAM_DISPOSITION_VALUE_ATTACHMENT = "attachment";
    protected static final String URL_PARAM_TRANSFORM = "transform";
    protected static final String URL_PARAM_PAGE_NUMBER = "page";
    protected static final String URL_PARAM_RENDERING_ID = "rid";

    /*
     * **************************** ATTRIBUTES **************************
     */

    /*
     * ************************** PUBLIC METHODS ************************
     */

    /*
     * ********************* PROTECTED/PRIVATE METHODS ******************
     */
    /**
     * Serve a single document by name/txId from the specified ecm instance<BR> optional parameters are : <BR> >
     * disposition=inline|attachment</BR> > transform=xslt transformation</BR> > outputMimeType=a convertion may be made
     * if outputMimetype is different from document real mimetype</BR>
     */
    protected void serveECMDocument(HttpServletRequest request,
            HttpServletResponse response, IECMService ecm, String transactionId, String docName, String disposition, String transform,
            String outputMimeType) throws ServletException, IOException {
        LOG.entering();

        InputStream input = null;

        ECMDocument document = ecm.get(transactionId, docName);
        if (document == null) {
            throw new UserException(
                    UserException.Code.DTP_USER_NOT_FOUND,
                    "Document not found, transactionId='%s', documentName='%s'",
                    transactionId, docName);
        }
        String inputMimeType = document.getContentMimeType();
        String docFileName = document.getFilename();
        input = document.readContent();

        if (outputMimeType == null && transform == null) {
            outputMimeType = inputMimeType;
        }

        response.reset();
        ServletOutputStream output = response.getOutputStream();

        String contentDisposition;
        contentDisposition = disposition + ";filename=\"" + docFileName
                + "\"";
        String contentType;
        contentType = normalizeMimeType(outputMimeType);

        if (null != outputMimeType && outputMimeType.startsWith("text")) {
            contentType += ";charset=UTF-8";
        }
        response.setHeader("Content-Disposition", contentDisposition);
        response.setContentType(contentType);

        if (outputMimeType == null || inputMimeType.equals(outputMimeType)) {
            // FIXME pb d'encodage, à mettre au point
            /*
             * InputStream is = input; if(outputMimeType.startsWith("text/")) { // transform encoding ConverterFactory
             * cf = ApiFactory.getApi(ConverterFactory.class); Converter converter = cf.getConverter(outputMimeType);
             * converter.setSourceEncoding(request.getCharacterEncoding()); is = converter.convert(input,
             * outputMimeType); }
             */

            // copy
            copyAll(input, output);
        } else {
            // transform
            Converter converter = ConverterFactory.getConverter(outputMimeType);
            // init value for the converter
            if (transform != null && !transform.equals("")) {
                InputStream xslt = getServletContext().getResourceAsStream(
                        transform);
                if (xslt == null) {
                    throw new EnvironmentException(
                            EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                            "Unable to load XSLT file '%s'", transform);
                }
                converter.setXslt(xslt);
            }
            InputStream is = converter.convert(input, inputMimeType);
            copyAll(is, output);
        }
        input.close();
        output.flush();
        output.close();

        LOG.exiting();
    }

    protected boolean tryServeDocumentViewing(final HttpServletRequest request,
            final HttpServletResponse response,
            final IECMService ecm,
            final TransactionHandler txHandler,
            final String documentName) throws IOException {
        try {
            //Trimming parameters if set
            String page = request.getParameter(URL_PARAM_PAGE_NUMBER);
            if (page != null) {
                page = page.trim();
            }
            String renderingIdStr = request.getParameter(URL_PARAM_RENDERING_ID);
            long renderingId = 0;
            if (renderingIdStr != null) {
                renderingIdStr = renderingIdStr.trim();
                if (renderingIdStr.indexOf(".") > -1) {
                    renderingIdStr = renderingIdStr.substring(0, renderingIdStr.indexOf("."));
                }
                try {
                    renderingId = Long.parseLong(renderingIdStr);
                } catch (NumberFormatException nfe) {
                    throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER, "Invalid rendering ID [%s].", renderingIdStr);
                }
            }
            String outputMimeType = request.getParameter(URL_PARAM_FORMAT);
            if (outputMimeType != null) {
                outputMimeType = outputMimeType.trim();
            }

            Integer pageNumber = null;


            Transaction tx = txHandler.getDatabaseTransaction();
            //Verify request parameters
            if (tx == null || ecm == null || request == null || response == null) {
                throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER, "Parameters may not be null");
            }
            final MimeType mimeType = (outputMimeType == null || outputMimeType.isEmpty() ? null : new MimeType(outputMimeType));
            if (mimeType == null || !mimeType.match("application/json")) {
                boolean pageSet = (page != null && !page.isEmpty());
                boolean renderingIdSet = (renderingIdStr != null && !renderingIdStr.isEmpty());
                if (pageSet ^ renderingIdSet) {
                    throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER, pageSet ? "Rendering ID is missing" : "Page number is missing");
                }
                if (pageSet && renderingIdSet) {
                    try {
                        pageNumber = Integer.parseInt(page);
                    } catch (NumberFormatException e) {
                        throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER, "Unable to parse Page number");
                    }
                } else {
                    return false; // Request isn't a document viewing request. Bail!
                }
            }

            //Get the actual document
            final ECMDocument document = ecm.get(tx.getTransactionID(), documentName);
            if (document == null) {
                throw new UserException(
                        UserException.Code.DTP_USER_NOT_FOUND,
                        "Document not found, transactionId='%s', documentName='%s'",
                        tx.getTransactionID(), documentName);
            }

            //Verify Document Mime type to be PDF
            final MimeType contentMimeType = new MimeType(document.getContentMimeType());
            if (!contentMimeType.match("application/pdf")) {
                throw new UserException(
                        UserException.Code.DTP_USER_NOT_FOUND,
                        "Unsupported document mime type, transactionId='%s', documentName='%s'",
                        tx.getTransactionID(), documentName);
            }

            // Try retrieving validation proof for the requested document
            IDocumentProofResolver documentProofResolver = txHandler.getDocumentProofResolver();
            String proofFileName = documentProofResolver.getDocumentProofFilename(txHandler, ecm, document);

            ECMDocument validationProof = null;
            if (proofFileName != null) {
                validationProof = ecm.get(txHandler.getTransactionId(), proofFileName);
            }
            
            final DocRenderingService renderingService = ecm.getRenderer();
            if (renderingService == null) {
                throw new EnvironmentException(Code.DTP_ENV_CONFIGURATION, "No rendering service configured");
            }
            Locale locale = request.getLocale();
            String user = txHandler.getDatabaseUserAccess().getPersonalInfo().getUser();
            RenderingData renderingData = renderingService.getRenderingData(ecm, tx.getTransactionID(), user, document,
                    (null != validationProof) ? validationProof.readContent() : null,
                    locale);
            response.reset();
            final OutputStream out = response.getOutputStream();
            if (mimeType != null && mimeType.match("application/json")) {
                response.setContentType("application/json");
                out.write(renderingData.toJson().getBytes());
            } else {
                response.setContentType("image/png");
                byte[] imageByte = renderingService.getRenderingImage(ecm, tx.getTransactionID(), document, renderingId, pageNumber);
                InputStream in = new ByteArrayInputStream(imageByte);
                copyAll(in, out);
                in.close();
            }
            out.flush();
            out.close();
            return true; //Request has been handled
        } catch (MimeTypeParseException e) {
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER, "Error while parsing Mime types", e);
        }
    }

    /**
     * Copy the given input stream to the output stream.
     *
     * @param input The input to copy the given range to the given output for.
     * @param output The output to copy the given range from the given input for.
     * @throws IOException If something fails at I/O level.
     */
    protected void copyAll(InputStream input, OutputStream output) throws IOException {
        LOG.entering();
        byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];

        int read;
        while ((read = input.read(buffer)) != -1) {
            output.write(buffer, 0, read);
        }
        LOG.exiting();
    }

    /**
     * Fixes wrong IE mimetypes. Other browsers (especially chrome) may not handle them correctly
     */
    private String normalizeMimeType(String outputMimeType) {

        // cf. http://msdn.microsoft.com/en-us/library/ms775147%28v=vs.85%29.aspx

        if (outputMimeType.equalsIgnoreCase(MIME_TYPE_X_PNG)) {
            return MIME_TYPE_PNG;
        }

        if (outputMimeType.equalsIgnoreCase(MIME_TYPE_PJPEG)) {
            return MIME_TYPE_JPEG;
        }

        return outputMimeType;
    }
}
