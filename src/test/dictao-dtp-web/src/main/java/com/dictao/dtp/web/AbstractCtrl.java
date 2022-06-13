package com.dictao.dtp.web;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.X509Certificate;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

public abstract class AbstractCtrl {
    /*
     * ********************************************************************
     */
    /*
     * CONSTANTS
     */
    /*
     * ********************************************************************
     */
    private static final Logger LOG = LoggerFactory.getLogger(AbstractCtrl.class);
    private static final String TYPE_X509 = "javax.servlet.request.X509Certificate";
    private static final int DEFAULT_BUFFER_SIZE = 10240; // ..bytes = 10KB.

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
     * ********************************************************************
     */
    /*
     * PROTECTED METHODS
     */
    /*
     * ********************************************************************
     */
    protected X509Certificate getSSLCertificate(HttpServletRequest req) {
        if (req != null) {
            X509Certificate[] sslCerts = (X509Certificate[]) req.getAttribute((TYPE_X509));
            if ((sslCerts != null) && (sslCerts.length > 0)) {
                return sslCerts[0];
            }
        }
        return null;
    }

    protected ResponseBuilder getStreamResponseBuilder(final InputStream input, String contentDisposition, String contentType) throws IOException {
        if (null != contentType && contentType.startsWith("text")) {
            contentType += ";charset=UTF-8";
        }
        StreamingOutput streamingOutput = new StreamingOutput() {

            @Override
            public void write(OutputStream output) throws IOException,
                    WebApplicationException {
                try {
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    int read;
                    while ((read = input.read(buffer)) > 0) {
                        output.write(buffer, 0, read);
                    }
                } catch (Exception e) {
                    // no more details found into exception message, toString applied.
                    LOG.warn("An exception occurred while response stream writing, processing stopped(detailed message=%s).",
                            e);
                }
            }
        };

        ResponseBuilder response = Response.ok(streamingOutput);
        response.header("Content-Disposition", contentDisposition);
        response.type(contentType);

        return response;
    }
}
