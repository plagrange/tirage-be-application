package com.dictao.dtp.web.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import com.sun.jersey.api.container.MappableContainerException;

/**
 * Maps exceptions into a MappableContainerException avoiding "dirty" jersey logs.
 * Exceptions will be converted to ServletException by jersey, and logged by the DtpUIFilter.
 * Please make sure that DtpUIFilter is configured in web.xml.
 */
@ApplicationScoped
@Provider
public class UIRestExceptionMapper implements ExceptionMapper<Exception> {
    
    private static final Logger LOG = LoggerFactory.getLogger(UIRestExceptionMapper.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + UIRestExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception e) {
        
        if (e instanceof com.sun.jersey.api.NotFoundException) {
            LOGU.error(e.getMessage());
            // complete log
            LOGU.debug(e);
            // send 404
            return Response.status(Response.Status.NOT_FOUND).build();
        }
        // Wrap the ui exception so that it's not logged by the Jersey servlet
        MappableContainerException ex = new MappableContainerException(e);
        throw ex;

    }
}
