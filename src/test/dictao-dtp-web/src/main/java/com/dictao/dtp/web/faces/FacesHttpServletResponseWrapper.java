package com.dictao.dtp.web.faces;

import com.dictao.dtp.web.HttpContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

public class FacesHttpServletResponseWrapper extends HttpServletResponseWrapper {
    
    private static final String UI_JSF_PREFIX = "/ui/jsf/";

    private final String tenantName;
    private final String applicationName;
    private final String universalAccessId;

    public FacesHttpServletResponseWrapper(HttpServletResponse request, String tenantName, String applicationName, String universalAccessId) {
        super(request);

        this.tenantName = tenantName;
        this.applicationName = applicationName;
        this.universalAccessId = universalAccessId;
    }

    @Override
    public String encodeURL(final String url) {
        
        String result = super.encodeURL(url);

        StringBuilder sb = new StringBuilder();

        String contextPath = HttpContext.getRequest().getContextPath();

        if (result.startsWith(contextPath)) {
            sb.append(contextPath);
            result = result.substring(contextPath.length());
        }

        if (result.startsWith(UI_JSF_PREFIX)) {

            // Rewrite output urls : add tenant, app & aid/eaid
            sb.append(UI_JSF_PREFIX);
            result = result.substring(UI_JSF_PREFIX.length());

            if (tenantName != null) {
                sb.append(tenantName).append('/');
            }

            if (applicationName != null) {
                sb.append(applicationName).append('/');
            }

            if (universalAccessId != null) {
                sb.append(universalAccessId).append('/');
            }

        }
        
        sb.append(result);

        return sb.toString();
    }
}