package com.dictao.dtp.web.rest;

import java.io.IOException;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

public abstract class AbstractEchoCtrl {

    /* ******************************************************************** */
    /* CONSTANTS */ 
    /* ******************************************************************** */
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + AbstractEchoCtrl.class.getName());

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */

    @GET
    @Path("echo")
    public String getDocument(@QueryParam("request") String request) throws IOException {
        LOGU.info("Return value='%s'", request);
        return request;
    }
}
