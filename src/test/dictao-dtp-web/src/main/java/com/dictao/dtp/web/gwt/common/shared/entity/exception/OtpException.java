/*
 * (@)SmsException.java
 * Copyright © 2010 Dictao. All rights reserved. Confidential and proprietary.
 * Creation on 15 janv. 10
 * Last Modified on 15 janv. 10
 *
 */
package com.dictao.dtp.web.gwt.common.shared.entity.exception;

import java.io.Serializable;

import com.dictao.dtp.web.gwt.common.shared.entity.data.SMSOtpResult;

/**
 * Gestion des erreurs d'authentification par otp sms
 * 
 * @author gla
 */
public class OtpException extends Exception implements Serializable
{
    /* ******************************************************************** */
    /* CONSTANTS */
    /* ******************************************************************** */
    private static final long serialVersionUID = 9118618717802313485L;

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */

    /** Instance use to indicate the number of trial remains by the user. */
    private int trialRemainsNb = 0;

    /** Instance use to indicate if the user can retry. */
    private boolean retryAuthorized;

    /** Instance use to indicate if the user can retry. */
    private SMSOtpResult smsOtpResult;

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */


    /**
     * Class constructor.
     */
    public OtpException()
    {
        super();
        this.trialRemainsNb = 0;
        this.retryAuthorized = false; 
        this.smsOtpResult = SMSOtpResult.UNKOWN_ERROR;
    }

    /**
     * Class constructor.
     */
    public OtpException(SMSOtpResult smsOtpResult)
    {
        super();
        this.trialRemainsNb = 0;
        this.retryAuthorized = false; 
        this.smsOtpResult = smsOtpResult;
    }

    /**
     * Class constructor.
     */
    public OtpException(SMSOtpResult smsOtpResult, int trialRemainsNb)
    {
        super();
        this.smsOtpResult = smsOtpResult;
        this.trialRemainsNb = trialRemainsNb;
        this.retryAuthorized = false; 
    }

    /**
     * Class constructor.
     */
    public OtpException(SMSOtpResult smsOtpResult, int trialRemainsNb, boolean retryAuthorized)
    {
        super();
        this.smsOtpResult = smsOtpResult;
        this.trialRemainsNb = trialRemainsNb;
        this.retryAuthorized = retryAuthorized; 
    }

    /**
     * Get the number of trial remains for the user in the OTP SMS
     * authentication process. <br>
     * 
     * @return the number of trial remains
     */
    public int getTrialRemainsNb()
    {
        return trialRemainsNb;
    }

    /**
     * Set the number of trial remains for the user in the OTP SMS
     * authentication process. <br>
     * 
     * @param trialRemainsNb
     *            the number of trial remains
     */
    public void setTrialRemainsNb(int trialRemainsNb)
    {
        this.trialRemainsNb = trialRemainsNb;
    }

    /**
     * Indicate if the user is authorized to process to a new OTP SMS
     * authentication. <br>
     * 
     * @return true if the user could retry an authentication
     */
    public boolean isRetryAuthorized()
    {
        return this.retryAuthorized;
    }

    /**
     * @return the smsOtpResult
     */
    public SMSOtpResult getSmsOtpResult() {
        return smsOtpResult;
    }

    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */
}
