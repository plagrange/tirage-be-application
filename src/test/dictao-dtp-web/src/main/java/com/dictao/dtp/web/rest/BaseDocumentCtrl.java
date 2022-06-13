package com.dictao.dtp.web.rest;

import com.dictao.dtp.core.api.converting.Converter;
import com.dictao.dtp.core.api.converting.ConverterFactory;
import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.services.IECMService;
import com.dictao.dtp.core.services.ecm.ECMDocument;
import com.dictao.dtp.core.services.ecm.IndexEntry;
import com.dictao.dtp.core.services.hash.Sha256HashService;
import com.dictao.dtp.core.transactions.ApplicationConf;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.web.AbstractCtrl;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Pattern;


public class BaseDocumentCtrl extends AbstractCtrl {
    
    private static final Logger LOG = LoggerFactory.getLogger(BaseDocumentCtrl.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + BaseDocumentCtrl.class.getName());

    public static final String URL_PARAM_FORMAT = "format";
    public static final String URL_PARAM_DISPOSITION = "disposition";
    public static final String URL_PARAM_DISPOSITION_VALUE_INLINE = "inline";
    public static final String URL_PARAM_DISPOSITION_VALUE_ATTACHMENT = "attachment";
    public static final String URL_PARAM_TRANSFORM = "transform";
    
    private static final String URL_PARAM_TRANSFORM_PATTERN_STRING = "[a-zA-Z0-9\\-_]+";
    public static final Pattern URL_PARAM_TRANSFORM_PATTERN = Pattern.compile(URL_PARAM_TRANSFORM_PATTERN_STRING);
    
    private static final String XSLT_TRANSFORM_BASE_PATH = "./document/xslt/";
    private static final String XSLT_TRANSFORM_EXT = ".xslt";
    
    /*
     * ********************************************************************
     */
    /*
     * PROTECTED METHODS
     */
    /*
     * ********************************************************************
     */
    
    protected static InputStream transform(InputStream input, String transform, String outputMimeType) throws IOException {
        LOG.entering();
        
        if(!URL_PARAM_TRANSFORM_PATTERN.matcher(transform).matches()){
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid transformation [%s]", transform);
        }
        
        InputStream xslt = Thread.currentThread().getContextClassLoader().getResourceAsStream(XSLT_TRANSFORM_BASE_PATH + transform + XSLT_TRANSFORM_EXT);
        
        if(transform != null && xslt == null) {
            throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                    "Invalid transformation [%s]", transform);
        }
        
        // transform
        Converter converter = ConverterFactory.getConverter(outputMimeType);
        converter.setXslt(xslt);
        InputStream is = converter.convert(input, outputMimeType);
        LOG.exiting();
        return is;
    }
    
    protected void AddServiceDocumentViewedStep(TransactionHandler txHandler, ECMDocument document, IECMService ecm) {
           
            // OCSP compatible application.
            // Add SERVICE_DOCUMENT_VIEWED step with document digest

            InputStream input = document.readContent();

            String digest;
            try {
                digest = new Sha256HashService().computeDigest(input);
                input.close();
            } catch (IOException ex) {
                throw new EnvironmentException(ex,
                        EnvironmentException.Code.DTP_ENV_INTERNAL_ERROR,
                        "Unable to compute document digest '%s'",
                        document.getFilename());
            }
            
            // Try retrieve document type
            String docType = null;
            for (IndexEntry entry : ecm.getIndexedDocumentList(txHandler.getTransactionId())) {
                if (document.getFilename().equals(entry.getDocumentFilename())) {
                    docType = entry.getType();
                    break;
                }
            }
            
            txHandler.addStepDocViewed(ecm.getName(), document.getFilename(), docType, digest);
    }
}
