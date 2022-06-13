package com.dictao.dtp.web.gwt.common.shared.entity.exception;

import java.io.Serializable;

/**
 * Gestion des erreurs d'authentification par otp sms
 * 
 * @author nle
 */
public class OtpExpiredException extends Exception implements Serializable
{
    /* ******************************************************************** */
    /* CONSTANTS */
    /* ******************************************************************** */
	private static final long serialVersionUID = -4500584939319549032L;

    /* ******************************************************************** */
    /* ATTRIBUTES */
    /* ******************************************************************** */

    /* ******************************************************************** */
    /* PUBLIC METHODS */
    /* ******************************************************************** */

    /**
     * Class constructor.
     */
    public OtpExpiredException()
    {
        super();
    }

    /* ******************************************************************** */
    /* PROTECTED/PRIVATE METHODS */
    /* ******************************************************************** */
}
