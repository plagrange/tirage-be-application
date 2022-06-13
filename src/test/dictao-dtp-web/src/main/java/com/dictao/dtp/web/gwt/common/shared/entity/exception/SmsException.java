/*
 * (@)SmsException.java
 * Copyright © 2010 Dictao. All rights reserved. Confidential and proprietary.
 * Creation on 15 janv. 10
 * Last Modified on 15 janv. 10
 *
 */
package com.dictao.dtp.web.gwt.common.shared.entity.exception;

import java.io.Serializable;

/**
 * SmsException class is an exception class. It is use to treat an error in the
 * OTP SMS authentication action.<br>
 *
 * @author FAM
 * @author glaurent
 */
public class SmsException extends Exception implements Serializable {
    /* ******************************************************************** */
    /* CONSTANTS */
    /* ******************************************************************** */
    /** Constant used for serialization purpose (serial number). */
    private static final long serialVersionUID = 9118618717802313485L;

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */

    /** Instance use to indicate the number of trial remains by the user. */
    private int trialRemainsNb = 0;
    /** Instance use to indicate if the user can retry. */
    private boolean retryAuthorized = false;

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */

    /**
     * Class constructor.
     */
    public SmsException() {
        super();
    }

    /**
     * Get the number of trial remains for the user in the OTP SMS
     * authentication process. <br>
     *
     * @return the number of trial remains
     */
    public int getTrialRemainsNb() {
        return trialRemainsNb;
    }

    /**
     * Set the number of trial remains for the user in the OTP SMS
     * authentication process. <br>
     *
     * @param trialRemainsNb
     *            the number of trial remains
     */
    public void setTrialRemainsNb(int trialRemainsNb) {
        this.trialRemainsNb = trialRemainsNb;
    }

    /**
     * Indicate if the user is authorized to process to a new OTP SMS
     * authentication. <br>
     *
     * @return true if the user could retry an authentication
     */
    public boolean isRetryAuthorized() {
        return this.retryAuthorized;
    }

    /**
     * Set if the user is authorized to process to a new OTP SMS authentication.
     * <br>
     *
     * @param retryAuthorized
     *            true if the user could retry an authentication
     */
    public void setRetryAuthorized(boolean retryAuthorized) {
        this.retryAuthorized = retryAuthorized;
    }

    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */
}
