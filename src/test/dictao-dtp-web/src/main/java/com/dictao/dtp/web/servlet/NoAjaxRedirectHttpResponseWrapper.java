package com.dictao.dtp.web.servlet;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Wrapper for HttpServletRequest.
 * Throws an exception if a redirection response is sent to user in AJAX mode.
 * See http://www.w3.org/TR/XMLHttpRequest/#infrastructure-for-the-send%28%29-method
 */
public class NoAjaxRedirectHttpResponseWrapper extends HttpServletResponseWrapper {

    private static final Logger LOG = LoggerFactory.getLogger(NoAjaxRedirectHttpResponseWrapper.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user." + NoAjaxRedirectHttpResponseWrapper.class.getName());
    private static final int[] HTTP_REDIRECT_STATUS_CODES = new int[]{301, 302, 303, 307, 308};
    

    public NoAjaxRedirectHttpResponseWrapper(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        throw new UnsupportedOperationException(String.format("Cannot redirect user to location: '%s' in RPC request.", location));
    }

    @Override
    public void setStatus(int sc) {
        EnsureNotRedirection(sc);
        super.setStatus(sc);
    }

    @Deprecated
    @Override
    public void setStatus(int sc, String sm) {
        EnsureNotRedirection(sc);
        super.setStatus(sc, sm);
    }

    void EnsureNotRedirection(int sc) {

        for (int i = 0; i < HTTP_REDIRECT_STATUS_CODES.length; i++) {
            if (HTTP_REDIRECT_STATUS_CODES[i] == sc) {
                throw new UnsupportedOperationException(String.format("Cannot redirect user with status: '%s' in RPC request.", sc));
            }
        }

    }
}
