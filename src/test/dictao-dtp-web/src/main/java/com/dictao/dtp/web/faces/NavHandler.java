package com.dictao.dtp.web.faces;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.UserAccess;
import com.dictao.dtp.web.INavHandler;
import com.dictao.dtp.web.data.RedirectStatus;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

public class NavHandler implements INavHandler {

    private static final Logger LOG = LoggerFactory.getLogger(NavHandler.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."+ NavHandler.class.getName());
    
    private FacesContext facesContext = null;
    private TransactionFactory txFct = null;
    private TransactionHandler handler = null;
    
    public NavHandler(FacesContext facesContext, TransactionFactory txFct) {
        this.facesContext = facesContext;
        this.txFct = txFct;
    }
      
    @Override
    public String getBasePath() {
        return facesContext.getExternalContext().getRequestContextPath();
    }

    @Override
    public void redirect(String url) {
        try {
            LOGU.info("redirection to url='%s'", url);
            facesContext.getExternalContext().redirect(url);
        }
        catch (IOException ioe) {
            LOG.error(ioe, "Unable to redirect to '%s'", url);
        }
    }
    
    private void redirect(String url, String statusName) {
        
        int qpos = url.indexOf('?');
        int hpos = url.indexOf('#');
        
        String params = qpos == -1 ? "?" : "&";
        
        // append tid if found
        if (handler != null && handler.getTransactionId() != null)
            params += "tid=" + handler.getTransactionId() + "&";
                
        params += "status=" + statusName;

        url = hpos == -1 ? url + params : url.substring(0, hpos) + params
                + url.substring(hpos);

        redirect(url);
    }

    @Override
    public void setTransactionHandler(TransactionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void redirectWithStatus(String statusName) {
        if (handler == null
                || handler.getDatabaseUserAccess() == null
                || handler.getDatabaseUserAccess().getUIInfo() == null) {
            redirectOnError(RedirectStatus.INTERNAL_ERROR);
            return;
        } else if (handler.getDatabaseUserAccess().getUIInfo().getBackUrl() == null
                || handler.getDatabaseUserAccess().getUIInfo().getBackUrl().isEmpty()) {
            redirectOnError(RedirectStatus.USER_ERROR);
            return;
        }
        if (handler.getTransactionId() != null ) {
            MDC.put("TID", handler.getTransactionId());
        }
        redirect(handler.getDatabaseUserAccess().getUIInfo().getBackUrl(), statusName);
        //clear log Metadatas
        MDC.clear();
    }

    @Override
    public void redirectOnError(RedirectStatus statusCode) {
        
        LOG.entering("redirection on error with statusCode=%s", statusCode.toString());
        try {
            String url = null;
            String tenant = null;
            String currentURL = ((HttpServletRequest) facesContext
                    .getExternalContext().getRequest()).getRequestURL()
                    .toString();
            LOG.debug("current url='%s'", currentURL);
            if (handler != null) {
                if (handler.getTransactionId() != null ) {
                    if (handler.getTransactionId() != null ) {
                        MDC.put("TID", handler.getTransactionId());
                    }
                }

                // Persist error step
                handler.addStep(statusCode.getStepName());

                // Try retrieve back url from user access
                UserAccess ua = handler.getDatabaseUserAccess();
                if (ua != null && ua.getUIInfo()!=null && ua.getUIInfo().getBackUrl()!=null){
                    url = handler.getDatabaseUserAccess().getUIInfo().getBackUrl();
                }

                // If no back url is found, use error back url (of the current application id)
                if(url == null || url.isEmpty())
                    url = handler.getErrorBackURL();
                
                LOG.debug("redirection on application url='%s'", url);
            }
            if (url == null || url.isEmpty() ) {
                tenant = findTenant();
                url = txFct.buildErrorBackURL(tenant);
                LOG.debug("redirection on tenant url='%s'", url);
            }
            if (url == null || url.isEmpty() || url.equalsIgnoreCase(currentURL)) {
                //static error page
                LOG.error("Unable to identify tenant from id='%s' to redirection on error. Default error url set '%s'",tenant, url);
                facesContext.getExternalContext().responseSendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"");
                return;
            }
            redirect(url,statusCode.toString());
            
        } catch (Exception e) {
            // cause of redirection bug
            LOG.error(e, "redirect after exception raised");
            try {
                facesContext.getExternalContext().responseSendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,"");
            } catch (IOException ex) {
                LOG.error(ex, "Unable to send error response %s", HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
        //clear log Metadatas
        MDC.clear();
    }
    

    /**
     * @return corresponding tenant got from current URL.
     */
    private String findTenant() {
        String currentURL = ((HttpServletRequest) facesContext
                .getExternalContext().getRequest()).getPathInfo();
        String[] pathToken = currentURL.split("/");
        if (pathToken.length > 0)
            return pathToken[1];
        return null;
    }

    @Override
    public RequestInfo getRequestInfo() {
        HttpServletRequest request = (HttpServletRequest)facesContext.getExternalContext().getRequest();
        return new RequestInfo(request.getRemoteAddr(),
                request.getRemoteHost(), 
                request.getRemotePort());
    }
}
