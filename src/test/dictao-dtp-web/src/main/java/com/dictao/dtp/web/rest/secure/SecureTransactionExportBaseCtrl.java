package com.dictao.dtp.web.rest.secure;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.transactions.serialization.ExportVersion;
import com.dictao.dtp.core.transactions.serialization.RestDocumentVersion;
import com.dictao.dtp.core.transactions.serialization.TransactionExportService;
import com.dictao.dtp.core.transactions.serialization.TransactionWriter;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.web.AbstractCtrl;
import com.dictao.dtp.web.servlet.DocumentServlet;
import javax.inject.Inject;
import org.apache.commons.httpclient.URIException;


public abstract class SecureTransactionExportBaseCtrl extends AbstractCtrl {
    
    /**
     * Path used by servlet to access documents : Document access
     */
    protected static final String DOCUMENT_SERVLET_PATH = "documentAccessPath";
    
    @Inject    
    protected TransactionExportService txExportSvc;

    @Inject
    protected TransactionFactory txSvc;

    
    protected TransactionWriter createWriter(ExportVersion exportVersion, 
            final RestDocumentVersion restDocVersion,
            final String documentAccessServletPath) {
        TransactionWriter writer = new TransactionWriter(exportVersion) {
            @Override
            public String getURLForDocument(
                    String applicationName, String tenant, String transactionId,
                    String svcName, String filename) {
                String url = null;
                // when retrieving asynchronous export in the file system, documentAccessServletPath is null (DTPJAVA-2169)
                String path = (documentAccessServletPath == null) ? "/" : documentAccessServletPath;
                String webAppPath = path + RestDocumentVersion.getDefaultVersion() + "/";
                try {
                    
                    url = DocumentServlet.getDocumentUrl(
                            webAppPath, tenant, applicationName, svcName,
                            transactionId, filename, null, null, null);
                }
                catch(URIException ex) {
                    throw new EnvironmentException(ex, EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                            "Unable to build URL for document access.");
                }
                return url;
            }
        };
        return writer;
    }

}
