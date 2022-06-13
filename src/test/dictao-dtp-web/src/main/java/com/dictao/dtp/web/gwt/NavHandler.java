package com.dictao.dtp.web.gwt;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.MDC;

import com.dictao.dtp.core.data.RequestInfo;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.entity.UserAccess;
import com.dictao.dtp.web.INavHandler;
import com.dictao.dtp.web.data.RedirectStatus;
import com.dictao.dtp.web.gwt.common.shared.entity.exception.RedirectException;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;

public class NavHandler implements INavHandler {

    private static final Logger LOG = LoggerFactory
            .getLogger(NavHandler.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user."
            + NavHandler.class.getName());
    private static final String defaultErrorURL = "/WEB-INF/jsp/error.jsp";

    private HttpServletRequest request;
    private TransactionFactory txFct = null;
    private TransactionHandler handler = null;

    public NavHandler(HttpServletRequest request,HttpServletResponse response, TransactionFactory txFct) {
        this.request = request;
        this.txFct = txFct;
    }
 
    @Override
   public String getBasePath() {
        return request.getContextPath();
    }

    @Override
    public RequestInfo getRequestInfo() {
        return new RequestInfo(request.getRemoteAddr(),
                request.getRemoteHost(), request.getRemotePort());
    }
    
    @Override
    public void setTransactionHandler(TransactionHandler handler) {
        this.handler = handler;
    }

    @Override
    public void redirect(String url) { 
        LOGU.info("redirection to url='%s'", url);
        RedirectException ex = new RedirectException();
        ex.setRedirectUrl(url);
        throw ex;
    }

    private void redirect(String url, String statusName){
        int qpos = url.indexOf('?');
        int hpos = url.indexOf('#');
        String params = qpos == -1 ? "?" : "&";
        // append tid if found
        if (handler != null && handler.getTransactionId() != null)
            params += "tid=" + handler.getTransactionId() + "&";
        params += "status=" + statusName;
        url = hpos == -1 ? url + params : url.substring(0, hpos) + params
                + url.substring(hpos);
        this.redirect(url);
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
        if (handler.getTransactionId() != null) {
            MDC.put("TID", handler.getTransactionId());
        }
        redirect(handler.getDatabaseUserAccess().getUIInfo().getBackUrl(), statusName);

        // clear log Metadatas
        MDC.clear();
    }

    @Override
    public void redirectOnError(RedirectStatus statusCode) {

        LOG.entering("redirection on error with statusCode=%s",
                statusCode.toString());
        String url = null;
        String tenant = null;
        String currentURL = request.getRequestURL().toString();
        LOG.debug("current url='%s'", currentURL);
        if (handler != null) {
            if (handler.getTransactionId() != null) {
                MDC.put("TID", handler.getTransactionId());
            }
            // Persist error step
            handler.addStep(statusCode.getStepName());

            // Try retrieve back url from user access
            UserAccess ua = handler.getDatabaseUserAccess();
            if (ua != null && ua.getUIInfo()!=null && ua.getUIInfo().getBackUrl()!=null)
                url = handler.getDatabaseUserAccess().getUIInfo().getBackUrl();

            // If no back url is found, use error back url (of the current
            // application id)
            if (url == null || url.isEmpty())
                url = handler.getErrorBackURL();

            LOG.debug("redirection on application url='%s'", url);
        }
        if (url == null || url.isEmpty()) {
            tenant = findTenant();
            url = txFct.buildErrorBackURL(tenant);
            LOG.debug("redirection to tenant url='%s'", url);
        }
        if (url == null || url.isEmpty() || url.equalsIgnoreCase(currentURL)) {
            url = request.getContextPath() + defaultErrorURL;
            LOG.error("Unable to identify tenant from id='%s' to redirection on error. Default error url set '%s'",tenant, url);
        }
        redirect(url, statusCode.toString());
        
        // clear log Metadatas
        MDC.clear();
    }

    /**
     * @return corresponding tenant got from current URL.
     */
    private String findTenant() {
        String[] pathToken = request.getPathInfo().split("/");
        if (pathToken.length > 0)
            return pathToken[1];
        return null;
    }
}
