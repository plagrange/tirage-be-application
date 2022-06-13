package com.dictao.dtp.web.servlet.mvc;

/**
 * 
 * @author kchakali
 */
public class DACSUrlBuilder {

    public static final String AUTHENT_CALLBACK_URL_PARAM = "page";
    /** paramError values : <i></br>AuthnFailed.Locked</br>AuthnFailed.NoPlugin</br>
     * AuthnFailed.Canceled</br>InternalError</br>InvalidRequest</br>InvalidResponse</br>
     * Responder</br>UnknownPrincipal</i>*/
    public static final String AUTHENT_ERROR_PARAM = "dacserror";
    public static final String AUTHENT_ERROR_KEY = "auth_error_key";
    public static final String AUTHENT_SUCCESS_PAGE = "callbackOnSuccess";
    public static final String AUTHENT_FAILURE_PAGE = "callbackOnFailure";
    
    /**
     * @param tenant
     * @param application
     * @param accessId
     * @return
     */
    public static String buildAuthOnSuccessUrl(String tenant,
            String application, String accessId) {
        return buildAuthUrl(tenant, application, accessId, AUTHENT_SUCCESS_PAGE);
    }

    /**
     * @param tenant
     * @param application
     * @param accessId
     * @return
     */
    public static String buildAuthOnFailureUrl(String tenant,
            String application, String accessId) {
        return buildAuthUrl(tenant, application, accessId, AUTHENT_FAILURE_PAGE);
    }

    /**
     * @param tenant
     * @param application
     * @param accessId
     * @return URL on current authentication callback controller
     */
    public static String buildAuthUrl(String tenant, String application,
            String accessId) {
        return "/ui/" + tenant + "/" + application + "/" + accessId
                + "/authentcallback";
    }
    
    private static String buildAuthUrl(String tenant, String application,
            String accessId, String page) {
        return "/ui/" + tenant + "/" + application + "/" + accessId
                + "/authentcallback?" + AUTHENT_CALLBACK_URL_PARAM + "=" + page;
    }
}
