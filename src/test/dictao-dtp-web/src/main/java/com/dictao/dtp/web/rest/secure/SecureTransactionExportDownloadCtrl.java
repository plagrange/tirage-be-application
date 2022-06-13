package com.dictao.dtp.web.rest.secure;

import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.security.cert.X509Certificate;

import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import com.dictao.dtp.core.api.converting.Converter;
import com.dictao.dtp.core.api.converting.ConverterFactory;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.transactions.serialization.ExportFileLocator;
import com.dictao.dtp.core.transactions.serialization.ExportRewriteHandler;
import com.dictao.dtp.core.transactions.serialization.ExportType;
import com.dictao.dtp.core.transactions.serialization.ExportVersion;
import com.dictao.dtp.core.transactions.serialization.RestDocumentVersion;
import com.dictao.dtp.core.transactions.serialization.TransactionWriter;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

/**
 * Handles export serving
 */
@RequestScoped
@Path("/transactions/{exportVersion}/")
public class SecureTransactionExportDownloadCtrl extends SecureTransactionExportBaseCtrl {

    public static final String DASHBOARD_RESOURCES_PATH = "/dashbord-resources/";
    public static final String XSLT_RESOURCE_EXT = ".xslt";
    /**
     * Logger.
     */
    private static final Logger LOG = LoggerFactory.getLogger(SecureTransactionExportDownloadCtrl.class);
    /**
     * The URL GET parameter name for the xslt transformation (optional).
     */
    public static final String TRANSFORM = "transform";
    /**
     * HTML mime type
     */
    public static final String HTML_MIME_TYPE = "text/html";
    /**
     * serialVersionUID.
     */
    private static final long serialVersionUID = 8015657489509832485L;

    @GET
    @Path("{tenant}/{applicationName}/{exportType}/{txIdOrDate}")
    public Response serveExport(
            @PathParam("exportVersion") ExportVersion exportVersion,
            @PathParam("tenant") final String tenant,
            @PathParam("applicationName") final String applicationName,
            @PathParam("exportType") final ExportType exportType,
            @PathParam("txIdOrDate") final String txIdOrDate,
            @Context HttpServletRequest request,
            @Context ServletContext servletContext,
            @Context ServletConfig sc,
            @QueryParam(TRANSFORM) String transform) throws IOException {

        LOG.debug("Serve file task");
        final X509Certificate cert = getSSLCertificate(request);

        // Ensure user has access to tenant/application
        txSvc.checkAuthorizedCertificate(tenant, applicationName, cert);


        final ExportFileLocator outputLocator = new ExportFileLocator(
                txExportSvc.getExportPath(tenant),
                exportType, exportVersion);
        final String documentAccessServletPath = sc.getInitParameter(DOCUMENT_SERVLET_PATH);
        final TransactionWriter txWriter = createWriter(exportVersion, RestDocumentVersion.v2012_07, documentAccessServletPath);
        String requestURLString = request.getRequestURL().toString();
        final String webAppPath = requestURLString.substring(0, requestURLString.indexOf(request.getServletPath()));

        // /!\ Attention /!\  
        // Don't close the pipes to avoid a "java.io.IOException: Pipe closed".
        // To fix this, we have to synchronize the closing of the two pipes.
        final PipedInputStream pis = new PipedInputStream();
        final PipedOutputStream pos = new PipedOutputStream(pis);
        final InputStream exportStream = txExportSvc.serve(tenant, applicationName,
                exportType, txIdOrDate, cert, outputLocator, txWriter);

        Runnable serveXMLTransactionFiles = new Runnable() {
            @Override
            public void run() {
                try {
                    ExportRewriteHandler rewriter = new ExportRewriteHandler();
                    rewriter.filter(exportStream, webAppPath, pos);
                    pos.close();
                } catch (Exception ex) {
                    LOG.error(ex, "Unexpected error occurred in thread execcutes ExportRewriteHandler while parsing internal XML streams.");
                }
            }
        };
        Thread serveXMLTransactionFilesThread = new Thread(serveXMLTransactionFiles, "serveXMLTransactionFilesThread");
        serveXMLTransactionFilesThread.start();

        InputStream resultStream;
        String resultType;

        if (null == transform) {

            LOG.debug("No xslt tranformation specified. Serve bare xml export");
            resultStream = pis;
            resultType = "text/xml;charset=UTF-8";

        } else {

            if (!transform.matches("^[a-zA-Z0-9_\\-]+$")) {
                throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER, "Invalid transform '%s'", transform);
            }

            LOG.debug("Xsl tranformation specified '%s'", transform);

            resultType = HTML_MIME_TYPE;

            InputStream xslTransform = readResourceFile(transform);

            Converter converter = ConverterFactory.getConverter(HTML_MIME_TYPE);
            converter.setXslt(xslTransform);
            resultStream = converter.convert(pis, HTML_MIME_TYPE);

            xslTransform.close();
        }

        Response.ResponseBuilder response = Response.ok(resultStream);
        response.header("Content-Disposition", "inline");
        response.type(resultType);

        return response.build();
    }

    protected InputStream readResourceFile(String resourceName) throws IOException {
        return getClass().getResourceAsStream(DASHBOARD_RESOURCES_PATH + resourceName + XSLT_RESOURCE_EXT);
    }
}