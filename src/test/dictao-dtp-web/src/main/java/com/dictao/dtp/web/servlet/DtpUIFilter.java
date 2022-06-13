package com.dictao.dtp.web.servlet;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.dtp.core.transactions.ApplicationListConf;
import com.dictao.dtp.core.transactions.HandlerTypeConf;
import com.dictao.dtp.core.transactions.TransactionFactory;
import com.dictao.dtp.core.transactions.TransactionHandler;
import com.dictao.dtp.persistence.UserAccessService;
import com.dictao.dtp.persistence.entity.StepNameEnum;
import com.dictao.dtp.persistence.entity.UserAccess;
import com.dictao.dtp.web.HttpContext;
import com.dictao.dtp.web.faces.FacesHttpServletResponseWrapper;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import javax.inject.Inject;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import org.apache.log4j.MDC;

/**
 *
 *
 * @author kchakali, VRB
 */
public class DtpUIFilter implements Filter {

    private enum RedirectStatus {

        INTERNAL_ERROR,
        USER_ERROR,
        ENVIRONMENT_ERROR;
    }
    private static final Logger LOG = LoggerFactory.getLogger(DtpUIFilter.class);
    private static final Logger LOGU = LoggerFactory.getLogger("user." + DtpUIFilter.class.getName());
    private static final String UI_PREFIX = "/ui/";
    private static final String JSF_PREFIX = "jsf/";
    private static final String EAID_PREFIX = "ext:";
    private static final String CONFIG_EXCLUDE_PATH_REGEX = "com.dictao.web.filter.config.ExcludePathRegex";
    private static final String UAID_REGEX = "((" + EAID_PREFIX + ".*)|([A-Fa-f0-9]{8}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{4}-[A-Fa-f0-9]{12}))";
    private static final String FORWARD_PARAMS_ATTRIBUTE = "forward-filter-params";
    public static final String RPC_MIMETYPE = "application/x-dictao-json";
    public static final String RPC_LOCATION_HEADER = "x-dictao-location";
    public static final String LOCATION_HEADER = "Location";
    private transient Pattern exludePathPattern;
    private transient Pattern uaidPattern;
    @Inject
    private ApplicationListConf appConfs;
    @Inject
    private UserAccessService uaService;
    @Inject
    private TransactionFactory txFct;

    @Override
    public void doFilter(ServletRequest servletReq,
            ServletResponse servletResp, FilterChain chain) throws IOException,
            ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) servletReq;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResp;

        String contextPath = httpRequest.getContextPath();
        String requestURI = httpRequest.getRequestURI();
        String pathInfo = requestURI.substring(contextPath.length());

        ForwardParams forwardParams = (ForwardParams) httpRequest.getAttribute(FORWARD_PARAMS_ATTRIBUTE);

        if (forwardParams == null) {
            // REQUEST-mode filter
            handleRequest(httpRequest, httpResponse, chain, pathInfo);
        } else {
            // FORWARD-mode filter
            handleForward(httpRequest, httpResponse, chain, forwardParams);
        }
    }

    /**
     * Handles the incoming request : Extract parameters, validate context and
     * forward requests internally (url rewrite).
     *
     * @param httpRequest the http servlet request
     * @param httpResponse the http servlet response
     * @param pathInfo the query path (without context path)
     * @throws IOException
     */
    private void handleRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse, FilterChain chain, String pathInfo) throws IOException {

        try {

            UserAccess ua = null;
            String defaultBackUrl = null;
            String uaBackUrl = null;
            String internalAccessId = null;

            try {

                HttpContext.set(httpRequest, httpResponse, null, null, null, null);

                // Skip excluded paths
                if (exludePathPattern != null
                        && exludePathPattern.matcher(pathInfo).matches()) {
                    chain.doFilter(httpRequest, httpResponse);
                    return;
                }

                // pathInfo should be in one the the two formats :
                // /ui/tenant/app/aid/xxx
                // /ui/jsf/tenant/app/aid/xxx

                StringBuilder rewritePath = new StringBuilder();
                if (!pathInfo.startsWith(UI_PREFIX)) {
                    // Invalid path
                    sendError(httpResponse, HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }

                rewritePath.append(UI_PREFIX);
                pathInfo = pathInfo.substring(UI_PREFIX.length());

                boolean isJSF = false;
                if (pathInfo.startsWith(JSF_PREFIX)) {
                    isJSF = true;
                    rewritePath.append(JSF_PREFIX);
                    pathInfo = pathInfo.substring(JSF_PREFIX.length());
                }

                String[] pathInfoSegment = pathInfo.split("/");

                String tenantName;
                String appName;
                String aid;
                String eaid;
                String universalAccessId;
                int uaidIdx = getUaidIdx(pathInfoSegment);

                if (uaidIdx != 2) { // UAID expected at 3rd position
                    // 404
                    LOGU.error("Incoming request path does not match the pattern of UserAccessID. RequestURI='%s']", pathInfo);
                    sendError(httpResponse, HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                tenantName = pathInfoSegment[0];
                appName = pathInfoSegment[1];
                universalAccessId = pathInfoSegment[2];

                // defaultBackUrl must be computed first !!
                if ((tenantName != null) && (appName != null)) {
                    defaultBackUrl = appConfs.getErrorBackURL(tenantName, appName);
                }

                if (universalAccessId.startsWith(EAID_PREFIX)) {
                    eaid = universalAccessId.substring(EAID_PREFIX.length());
                    aid = null;
                } else {
                    aid = universalAccessId;
                    eaid = null;
                }

                LOGU.info("Incoming request. Query context=[tenant='%s', app='%s', aid='%s', eaid='%s']", tenantName, appName, aid, eaid);

                // Retrieve app Id
                String appId = appConfs.getApplicationIDAssociated(tenantName, appName);

                // App Id is valid, add request params to the logger context
                MDC.put("TENANT", tenantName);
                MDC.put("APPLICATION", appName);
                MDC.put("AID", universalAccessId);

                LOGU.debug("Application Id retrieved from context : applicationId='%s'", appId);

                // Try retrieve UserAccess from aid ou appId/eaid
                if (eaid != null) {
                    ua = uaService.findFromExternalId(appId, eaid);
                    if (null == ua) {
                        throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                                "Access unauthorized. No user access was found corresponding to identifiers [application='%s',eaid='%s']",
                                appId, eaid);
                    }
                } else {
                    ua = uaService.find(aid);
                    if (null == ua) {
                        throw new UserException(UserException.Code.DTP_USER_UNAUTHORIZED,
                                "Access unauthorized. No user access was found corresponding to identifier [aid='%s']", aid);
                    }

                    if (!ua.getApplicationID().equals(appId)) {
                        LOGU.error("User access applicationId does not match requested applicationId [found='%s', requested='%s']", ua.getApplicationID(), appId);
                        // => 404
                        sendError(httpResponse, HttpServletResponse.SC_NOT_FOUND);
                        return;
                    }
                }
                // Set final UserAccessId
                internalAccessId = ua.getAccessID();
                HttpContext.set(httpRequest, httpResponse, tenantName, appName, internalAccessId, eaid);
                uaBackUrl = getUserAccessRedirectUrl(ua);

                // Compute redirection path

                if (isJSF) {

                    // Append remaining path segments
                    for (int i = uaidIdx + 1; i < pathInfoSegment.length; i++) {
                        if (i != uaidIdx + 1) {
                            rewritePath.append("/");
                        }
                        rewritePath.append(pathInfoSegment[i]);
                    }

                    // Wrap the response to override output urls
                    httpResponse = new FacesHttpServletResponseWrapper(httpResponse, tenantName, appName, internalAccessId);

                } else {

                    rewritePath.append(tenantName).append("/");
                    rewritePath.append(appName).append("/");
                    rewritePath.append(internalAccessId).append("/");


                    Class<?> clazz = null;
                    HandlerTypeConf htc = appConfs.getHandlerType(tenantName, appName, ua.getTransaction().getHandler().toLowerCase());
                    if (htc == null || null == htc.getClazz()) {
                        throw new UserException(UserException.Code.DTP_USER_INVALID_PARAMETER,
                                "Invalid Paramater, cannot find the handlerType in configuration");
                    }
                    clazz = (Class<?>) htc.getClazz();

                    rewritePath.append(clazz.getSimpleName().toLowerCase()).append("/");
                    rewritePath.append(ua.getUIInfo().getUi().toLowerCase());

                    for (int i = uaidIdx + 1; i < pathInfoSegment.length; i++) {
                        rewritePath.append("/");
                        rewritePath.append(pathInfoSegment[i]);
                    }
                }

                String newPath = rewritePath.toString();

                // Store forward parameters before forwarding
                httpRequest.setAttribute(FORWARD_PARAMS_ATTRIBUTE, new ForwardParams(defaultBackUrl, uaBackUrl, internalAccessId));

                // Forward (internal url-rewrite)
                RequestDispatcher rd = httpRequest.getRequestDispatcher(newPath);
                rd.forward(httpRequest, httpResponse);


            } catch (UserException e) {
                LOGU.error(e, "User error during rewrite filter " + UI_PREFIX
                        + " request");
                redirectOnError(defaultBackUrl, uaBackUrl, internalAccessId,
                        RedirectStatus.USER_ERROR, httpRequest, httpResponse);
            } catch (EnvironmentException e) {
                LOG.error(e, "Error occur during rewrite filter " + UI_PREFIX
                        + " request");
                redirectOnError(defaultBackUrl, uaBackUrl, internalAccessId,
                        RedirectStatus.ENVIRONMENT_ERROR, httpRequest, httpResponse);
            } catch (Exception e) {
                LOG.error(e, "Error occur during rewrite filter " + UI_PREFIX + " request");
                redirectOnError(defaultBackUrl, uaBackUrl, internalAccessId,
                        RedirectStatus.INTERNAL_ERROR, httpRequest, httpResponse);
            }

        } catch (Exception e) {
            LOG.error(e, "Error occur during rewrite filter " + UI_PREFIX + " request");
            sendError(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            HttpContext.unset();
            MDC.clear();
        }
    }

    /**
     * Intercepts and logs all exceptions during real request processing.
     *
     * @param httpRequest
     * @param httpResponse
     * @param chain
     * @param forwardParams
     * @throws IOException
     */
    private void handleForward(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
            FilterChain chain, ForwardParams forwardParams) throws IOException {

        ServletException servletException = null;

        try {

            if (isAjaxRpcRequest(httpRequest)) {
                // This is an AJAX RPC call.
                // Wrap the response to disable redirections in ajax RPC calls
                httpResponse = new NoAjaxRedirectHttpResponseWrapper(httpResponse);
            }

            try {
                try {
                    chain.doFilter(httpRequest, httpResponse);

                } catch (ServletException ex) {
                    // In case a servlet exception is caught, just handle it's
                    // root cause.
                    servletException = ex;
                    throw (Exception) ex.getRootCause();
                }

            } catch (UserErrorRedirectException e) {
                Exception ex = (servletException != null) ? servletException : e;
                LOGU.error(ex, "User error during filter " + UI_PREFIX + " request. ErrorRedirectStatus : " + e.getErrorRedirectStatus());
                redirectOnError(forwardParams.defaultBackUrl, forwardParams.uaBackUrl, forwardParams.internalAccessId,
                        e.getErrorRedirectStatus(), httpRequest, httpResponse);
            } catch (UserException e) {

                Exception ex = (servletException != null) ? servletException : e;
                LOGU.error(ex, "User error during filter " + UI_PREFIX + " request");
                redirectOnError(forwardParams.defaultBackUrl, forwardParams.uaBackUrl, forwardParams.internalAccessId,
                        RedirectStatus.USER_ERROR, httpRequest, httpResponse);
            } catch (EnvironmentException e) {
                Exception ex = (servletException != null) ? servletException : e;
                LOG.error(ex, "Error occur during filter " + UI_PREFIX + " request");
                redirectOnError(forwardParams.defaultBackUrl, forwardParams.uaBackUrl, forwardParams.internalAccessId,
                        RedirectStatus.ENVIRONMENT_ERROR, httpRequest, httpResponse);

            } catch (Exception e) {
                Exception ex = (servletException != null) ? servletException : e;
                LOG.error(ex, "Error occur during filter " + UI_PREFIX + " request");
                redirectOnError(forwardParams.defaultBackUrl, forwardParams.uaBackUrl, forwardParams.internalAccessId,
                        RedirectStatus.INTERNAL_ERROR, httpRequest, httpResponse);
            }

        } catch (Exception e) {
            LOG.error(e, "Error occur during filter " + UI_PREFIX + " request");
            sendError(httpResponse, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }

    }

    protected boolean isAjaxRpcRequest(HttpServletRequest httpRequest) {

        String acceptHeader = httpRequest.getHeader(HttpHeaders.ACCEPT);
        return (RPC_MIMETYPE.equals(acceptHeader));
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        destroy();
        String regex = filterConfig.getInitParameter(CONFIG_EXCLUDE_PATH_REGEX);
        if (regex != null && regex.length() > 0) {
            try {
                exludePathPattern = Pattern.compile(regex);
            } catch (PatternSyntaxException ex) {
                throw new EnvironmentException(
                        ex,
                        EnvironmentException.Code.DTP_ENV_CONFIGURATION,
                        "The syntax is invalid for the regular expression, "
                        + regex
                        + ", associated with the initialization parameter "
                        + CONFIG_EXCLUDE_PATH_REGEX);
            }
        }
        uaidPattern = Pattern.compile(UAID_REGEX);
    }

    @Override
    public void destroy() {
    }

    private String addParameterToUrl(String url, String name, String value)
            throws UnsupportedEncodingException {
        String res;
        if (null == name || null == value) {
            return url;
        }
        int qpos = url.indexOf('?');
        int hpos = url.indexOf('#');
        char sep = qpos == -1 ? '?' : '&';
        name = URLEncoder.encode(name, "UTF-8");
        value = URLEncoder.encode(value, "UTF-8");
        String seg = sep + name + '=' + value;
        res = hpos == -1 ? url + seg : url.substring(0, hpos) + seg + url.substring(hpos);
        return res;
    }

    private static void sendError(HttpServletResponse response, int errorStatusCode) throws IOException {
        if (!response.isCommitted()) {
            response.reset();
            response.sendError(errorStatusCode);
        } else {
            LOG.warn("the response has already been committed,"
                    + " After using sendError method, the response should be considered to be committed and should not be written to.");
        }
    }

    private int getUaidIdx(String[] pathInfoSegment) {
        int i;
        boolean matched = false;
        for (i = Math.min(2, pathInfoSegment.length - 1); i >= 0; i--) {
            if (uaidPattern != null
                    && uaidPattern.matcher(pathInfoSegment[i]).matches()) {
                matched = true;
                break;
            }
        }
        return matched == true ? i : -1;
    }

    private String getUserAccessRedirectUrl(UserAccess ua) throws UnsupportedEncodingException {

        String result = null;
        if (ua != null && ua.getUIInfo() != null && ua.getUIInfo().getBackUrl() != null) {
            result = ua.getUIInfo().getBackUrl();
            if (result.isEmpty()) {
                result = null;
            }
            // Add txid and aid as query parameters to backUrl
            result = addParameterToUrl(result, "txid", ua.getTransaction().getTransactionID());
            result = addParameterToUrl(result, "aid", ua.getAccessID());
        }
        return result;
    }

    private void redirectOnError(String defaultBackUrl, String uaBackUrl, String internalAccessId,
            RedirectStatus statusCode, HttpServletRequest httpRequest, HttpServletResponse resp) throws IOException {

        redirectOnError(defaultBackUrl, uaBackUrl, internalAccessId, statusCode.toString(), httpRequest, resp);
    }

    private void redirectOnError(String defaultBackUrl, String uaBackUrl, String internalAccessId,
            String statusCode, HttpServletRequest httpRequest, HttpServletResponse resp) throws IOException {
        LOG.entering("redirection on error with statusCode [%s]", statusCode);

        String backUrl;
        String location = null;

        if (uaBackUrl != null) {
            LOG.debug("redirection on transaction back url [%s]", uaBackUrl);
            backUrl = uaBackUrl;
        } else {
            LOG.debug("redirection on tenant or app back url [%s]", defaultBackUrl);
            backUrl = defaultBackUrl;
        }

        if (backUrl != null && !backUrl.isEmpty()) {
            location = addParameterToUrl(backUrl, "status", statusCode);

            if (!isAjaxRpcRequest(httpRequest)) {
                LOGU.info("Redirection to url [%s]", location);
                // Redirect with relative url support
                resp.setStatus(302);
                resp.setHeader(LOCATION_HEADER, location);
            } else {
                LOGU.info("Ajax RPC redirection to url [%s]", location);
                resp.addHeader(RPC_LOCATION_HEADER, location);
                resp.setStatus(500);
            }
        } else {
            LOGU.warn("No valid backUrl [uaBackUrl='%s',defaultBackUrl='%s',accessId='%s']", uaBackUrl, defaultBackUrl, internalAccessId);
            sendError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        LOG.exiting();
    }

    /**
     * private inner class to store forward parameters
     */
    private static class ForwardParams {

        public final String defaultBackUrl;
        public final String uaBackUrl;
        public final String internalAccessId;

        /**
         * @param defaultBackUrl
         * @param uaBackUrl
         * @param internalAccessId
         */
        public ForwardParams(String defaultBackUrl, String uaBackUrl, String internalAccessId) {
            this.defaultBackUrl = defaultBackUrl;
            this.uaBackUrl = uaBackUrl;
            this.internalAccessId = internalAccessId;
        }
    }
}