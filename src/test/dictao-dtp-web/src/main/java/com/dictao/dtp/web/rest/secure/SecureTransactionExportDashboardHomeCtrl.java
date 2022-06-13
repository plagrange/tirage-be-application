package com.dictao.dtp.web.rest.secure;

import com.dictao.dtp.web.AbstractCtrl;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.enterprise.context.RequestScoped;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import org.apache.commons.io.IOUtils;

@RequestScoped
@Path("/dashboard")
public class SecureTransactionExportDashboardHomeCtrl extends AbstractCtrl {
    
    
    public static final String EXPORT_GENERATION_PATH_PARAM = "export-generation-path";
    public static final String EXPORT_DOWNLOAD_PATH_PARAM = "export-download-path";
    public static final String DASHBOARD_HOME_HTML = "/dashbord-resources/export-dashboard-home.html";
    
    @GET
    @Path("{tenant}/{applicationName}")
    @Produces(MediaType.TEXT_HTML)
    public InputStream generateExports(
            @PathParam("tenant") String tenant,
            @PathParam("applicationName") String applicationName,
            @Context HttpServletRequest request,
            @Context ServletContext servletContext,
            @Context ServletConfig sc) throws IOException {
        
        InputStream is = readResourceFile(DASHBOARD_HOME_HTML);
        
        String htmlStr = IOUtils.toString(is, "UTF-8");
        htmlStr = htmlStr.replace("%exportGenerationUrl%", request.getContextPath() + sc.getInitParameter(EXPORT_GENERATION_PATH_PARAM));
        htmlStr = htmlStr.replace("%exportDownloadUrl%", request.getContextPath() + sc.getInitParameter(EXPORT_DOWNLOAD_PATH_PARAM));
        htmlStr = htmlStr.replace("%tenantId%", tenant);
        htmlStr = htmlStr.replace("%appName%", applicationName);

        is.close();
        return new ByteArrayInputStream(htmlStr.getBytes("UTF-8"));
    }
    
    protected InputStream readResourceFile(String resourceName) throws IOException {
        return getClass().getResourceAsStream(resourceName);
    }
}