package com.dictao.dtp.web.rest;

import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.util.UUID;
import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@ApplicationScoped
@Provider
public class RestExceptionMapper implements ExceptionMapper<Exception> {
    
    private static final Logger LOG = LoggerFactory.getLogger(RestExceptionMapper.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + RestExceptionMapper.class.getName());

    @Override
    public Response toResponse(Exception e) {
        
        if(e instanceof UserException) {
            
            LOGU.error(e, "User error caught while serving rest resource.");
            
            UserException ue = (UserException)e;
            switch(ue.getCode()) {
                case DTP_USER_UNAUTHORIZED:
                    return Response.status(Response.Status.FORBIDDEN).build();
                default:
                    return Response.status(Response.Status.NOT_FOUND).build();
            }
        }
        
        // Log error using a simple bug id implementation
        String bugId = UUID.randomUUID().toString();
        LOG.error(e, "An unexpected exception occured [track id='%s']", bugId);
        return Response.serverError().entity(String.format("Resource unavailable. Please Try again later (track id='%s')", bugId)).build();
    }
}
