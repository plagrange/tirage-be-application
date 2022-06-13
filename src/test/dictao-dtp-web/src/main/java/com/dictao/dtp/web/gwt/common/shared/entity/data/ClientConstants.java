/*
 * (@)Constants.java
 * Copyright @ 2010 Dictao. All rights reserved. Confidential and proprietary.
 * Creation on 5 janv. 10
 * Last Modified on 5 janv. 10
 */
package com.dictao.dtp.web.gwt.common.shared.entity.data;

/**
 * This interface encapsulates commonly used DTP SAAS portal constants.
 * 
 * @author FAM
 */
public interface ClientConstants {
    
    /* ******************************************************************** */
    /* GWT PARAM */
    /* ******************************************************************** */

    public static final String GWT_DICO_NAME_WFID = "wfid";

    public static final String GWT_DICO_NAME_AID = "aid";
    
    public static final String GWT_DICO_INIT_DATA = "InitData";

    public static final String GWT_DICO_VIEW = "view";

    /* ******************************************************************** */
    /* URL PARAM */
    /* ******************************************************************** */

    public static final String DEFAULT_THEME = "default";
    
    public static final String THEME_URL = "/theme";

    //public static final String UI_URL = "/ui";

    public static final String WEB_INF = "/WEB-INF";
    
    public static final String ERROR_URL = "../jsp/error.jsp";
    

    /* ******************************************************************** */
    /* HTTP ERROR CODE */
    /* ******************************************************************** */

    public static final int SC_INTERNAL_SERVER_ERROR = 500;    
    
    public static final int SC_NOT_FOUND = 404;
    
    /* ******************************************************************** */
    /* HTTP QUERY STRING PARAM NAME */
    /* ******************************************************************** */
    /**
     * Constant for the name of the http request parameter which indicate the
     * OID associate with the user
     */
    public static final String HTTP_PARAM_WFID = "wfid";
    
    public static final String HTTP_PARAM_AID = "aid";
    
    public static final String HTTP_PARAM_EAID = "eaid";
    
    public static final String HTTP_PARAM_APPID = "appid";

    /**
     * Constant for the name of the http request parameter which indicate the
     * OID associate with the transaction
     */
    public static final String HTTP_PARAM_TRANSACTION_ID = "tid";

    public static final String SESSION_SPECIAL_AUTH_DONE = "auth";
    

    

    public static final String CURRENT_VIEW = "view";

    public static final String SIGN_PAGE = "sign";

    public static final String RESUME_PAGE = "resume";

    /* ******************************************************************** */
    /* CONFIGURATION DEFAULT VALUES */
    /* ******************************************************************** */
    
    /** Constant for the number of trial in the otp sms authentication. */
    public static final int DEFAULT_OTP_SMS_MAX_TRIAL_NUMBER = 3;

}
