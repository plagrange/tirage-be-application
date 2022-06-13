package com.dictao.dtp.web.data;

import com.dictao.dtp.persistence.entity.StepNameEnum;


/**
 * Light RedirectStatusEnum enum contains the different redirect status.
 * 
 * @author FAM
 * 
 */
public enum RedirectStatus {
    
    /**
     * WARNING :
     * ---------
     *   > Make sure to only reference generic redirection status codes. No custom-specific status should be added here (OK, CANCEL, CONTINUE_LATER...)
     *   > Update the fromString() method.
     *   > Update error.jsp pages
     */
     
    /** The server does not meet one of the preconditions that the requester put
     * on the request */
    SESSION_EXPIRED("SESSION_EXPIRED", StepNameEnum.USER_ERROR),
    INTERNAL_ERROR("INTERNAL_ERROR", StepNameEnum.INTERNAL_ERROR),
    USER_ERROR("USER_ERROR", StepNameEnum.USER_ERROR),
    ENVIRONMENT_ERROR("ENVIRONMENT_ERROR", StepNameEnum.ENVIRONMENT_ERROR),
    CONTEXT_ERROR("CONTEXT_ERROR", StepNameEnum.USER_ERROR),
    TRANSACTION_ERROR("TRANSACTION_ERROR", StepNameEnum.USER_ERROR);

    private String _value;
    private StepNameEnum _stepName;
   
    /**
     * Get the enum from the name (String) value associate.<br>
     * 
     * @param value
     * @return RedirectStatusEnum
     */
    public static RedirectStatus fromString(String value) {
        if (value.equals(USER_ERROR.toString()))
            return USER_ERROR;
        else if (value.equals(ENVIRONMENT_ERROR.name()))
            return ENVIRONMENT_ERROR;
        else if (value.equals(INTERNAL_ERROR.name()))
            return INTERNAL_ERROR;
        else if (value.equals(SESSION_EXPIRED.toString()))
            return SESSION_EXPIRED;
        else if (value.equals(CONTEXT_ERROR.toString()))
            return CONTEXT_ERROR;
        else if (value.equals(TRANSACTION_ERROR.toString()))
            return TRANSACTION_ERROR;
        else
            return INTERNAL_ERROR;
    }
    
    /**
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return _value;
    }

    public StepNameEnum getStepName(){
        return _stepName;
    }

    /**
     * Class constructor.
     * 
     * @param value
     */
    private RedirectStatus(String value, StepNameEnum stepName) {
        this._value = value;
        this._stepName = stepName;
    }
}