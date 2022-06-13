package com.dictao.dtp.web.gwt.common.shared.entity.data;

public enum SMSOtpResult {

    /**
     * Verification succeeded
     */
    USER_AUTHENTICATED,

    /**
     * OTP verification failled. User might try again.
     */
    USER_NOT_AUTHENTICATED,

    /**
     * OTP verification failled. User might try again.
     */
    OTP_BLOCKED,

    /**
     * OTP expired.
     */
    OTP_EXPIRED,

    /**
     * User blocked after several failures.
     */
    OTP_TRIAL_EXHAUSTED,

    /**
     * User not found.
     */
    USER_NOT_FOUND,

    /**
     * An unexpected error occured.
     */
    UNKOWN_ERROR;
}