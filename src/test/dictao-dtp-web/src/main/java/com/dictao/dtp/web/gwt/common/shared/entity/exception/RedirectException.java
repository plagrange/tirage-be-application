/*
 * (@)RedirectException.java
 * Copyright © 2010 Dictao. All rights reserved. Confidential and proprietary.
 * Creation on 15 janv. 10
 * Last Modified on 15 janv. 10
 *
 */
package com.dictao.dtp.web.gwt.common.shared.entity.exception;

import java.io.Serializable;

/**
 * RedirectException class is an exception class. It is use to treat the
 * redirect action.<br>
 *
 * @author FAM
 * @author glaurent
 *
 */
public class RedirectException extends RuntimeException implements Serializable {

    /* ******************************************************************** */
    /* CONSTANTS */
    /* ******************************************************************** */
    /** Constant used for serialization purpose (serial number). */
    private static final long serialVersionUID = 9118618717802313487L;

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */

    /** Instance use to indicate the url where the user must be redirect to. */
    private String redirectUrl = "";

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */

    /**
     * Class constructor.
     *
     */
    public RedirectException() {
        super();
    }

    /**
     * Get the url where the user must be redirect to. <br>
     *
     * @return an url
     */
    public String getRedirectUrl() {
        return this.redirectUrl;
    }

    /**
     * Set the url where the user must be redirect to. <br>
     *
     * @param redirectUrl
     *            the url where the user must be redirect to
     */
    public void setRedirectUrl(String redirectUrl) {
        this.redirectUrl = redirectUrl;
    }
    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */

}
