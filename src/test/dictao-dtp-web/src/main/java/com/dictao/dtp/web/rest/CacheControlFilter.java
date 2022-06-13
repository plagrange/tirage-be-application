package com.dictao.dtp.web.rest;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.core.HttpHeaders;

import com.sun.jersey.spi.container.ContainerRequest;
import com.sun.jersey.spi.container.ContainerResponse;
import com.sun.jersey.spi.container.ContainerResponseFilter;

@ApplicationScoped
public class CacheControlFilter implements ContainerResponseFilter {

    @Override
    public ContainerResponse filter(ContainerRequest request,
            ContainerResponse response) {
        if (response.getEntity() != null && (response.getStatus() == 200)) {
            response.getHttpHeaders().add("Pragma", "no-cache");
            response.getHttpHeaders().add(HttpHeaders.CACHE_CONTROL, "no-cache");
            response.getHttpHeaders().add(HttpHeaders.CACHE_CONTROL, "no-store");
            response.getHttpHeaders().add(HttpHeaders.CACHE_CONTROL, "must-revalidate");
            response.getHttpHeaders().add(HttpHeaders.EXPIRES, "Mon, 8 Aug 2006 10:00:00 GMT");
        }
        return response;
    }

}
