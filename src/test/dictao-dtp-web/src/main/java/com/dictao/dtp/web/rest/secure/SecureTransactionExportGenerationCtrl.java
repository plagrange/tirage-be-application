package com.dictao.dtp.web.rest.secure;

import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.transactions.serialization.ExportFileLocator;
import com.dictao.dtp.core.transactions.serialization.ExportType;
import com.dictao.dtp.core.transactions.serialization.ExportVersion;
import com.dictao.dtp.core.transactions.serialization.RestDocumentVersion;
import com.dictao.dtp.core.transactions.serialization.TransactionWriter;
import com.dictao.dtp.web.servlet.Monitoring;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.IOException;
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

/**
 * Handles export generation/purge
 */
@RequestScoped
@Path("/generate")
public class SecureTransactionExportGenerationCtrl extends SecureTransactionExportBaseCtrl {

    /**
     * The URL GET parameter name for the run date.
     */
    public static final String RUN_DATE = "run-date";
    
    /**
     * Logger. 
     */
    private static final Logger LOG = LoggerFactory.getLogger(SecureTransactionExportGenerationCtrl.class);
    
    /**
     * serialVersionUID. 
     */
    private static final long serialVersionUID = 8015657489509832485L;

    @GET
    @Path("{tenant}/{applicationName}/{exportType}")
    public Response generateExports(
            @PathParam("tenant") String tenant,
            @PathParam("applicationName") String applicationName,
            @PathParam("exportType") ExportType exportType,
            @Context HttpServletRequest request,
            @Context ServletContext servletContext,
            @Context ServletConfig sc,
            @QueryParam(RUN_DATE) String runDateStr) throws IOException {

        X509Certificate certificate = getSSLCertificate(request);
        if (null == certificate) {
            throw new UserException(
                    UserException.Code.DTP_USER_UNAUTHORIZED,
                    "insufficient access conditions in url : %s",
                    request.getPathInfo());
        }

	// Ensure user has access to tenant/application
        txSvc.checkAuthorizedCertificate(tenant, applicationName, certificate);
        
        // Check if all parameters are provided.
        if (runDateStr == null) {
            throw new UserException(UserException.Code.DTP_USER_MISSING_PARAMETER,
                    "Missing parameter: run date [%s] parameter is mandatory.", RUN_DATE);
        }
        
        try {
             // Generate export for all versions.
            for (ExportVersion version : ExportVersion.values()) {
                ExportFileLocator outputLocator = new ExportFileLocator(
					txExportSvc.getExportPath(tenant), 
					exportType, version);
                final String documentAccessServletPath = sc.getInitParameter(DOCUMENT_SERVLET_PATH);
                TransactionWriter txWriter = createWriter(version, RestDocumentVersion.v2012_07, documentAccessServletPath);
                txExportSvc.export(tenant, applicationName, exportType, certificate, outputLocator, txWriter, runDateStr);
            }

        } catch (RuntimeException e) {
            //On error, update monitoring status...
            Monitoring.recordExportError();
            throw e;
        }
        
        LOG.debug("Export done");
        return Response.ok().build();
    }
}
