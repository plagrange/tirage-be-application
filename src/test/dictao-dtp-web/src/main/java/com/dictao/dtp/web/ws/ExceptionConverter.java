package com.dictao.dtp.web.ws;

import com.dictao.dtp.core.exceptions.EnvironmentException;
import com.dictao.dtp.core.exceptions.UserException;
import com.dictao.util.logging.Logger;
import com.dictao.util.logging.LoggerFactory;
import com.dictao.xsd.dtp.error.v3.SystemErrorCodeType;
import com.dictao.xsd.dtp.error.v3.SystemErrorInfo;
import com.dictao.xsd.dtp.error.v3.UserErrorCodeType;
import com.dictao.xsd.dtp.error.v3.UserErrorInfo;


/**
 * <p>Converts server exceptions into user-defined SOAPFaulExceptions.</p>
 * @author <a href="mailto:msauvee@dictao.com">Mickaël Sauvée</a>
 * @date 26 Jan. 2010
 *
 */
public abstract class ExceptionConverter<UFE extends Exception, EFE extends Exception> {

    /* *********************************************************************/
    /* CONSTANTS */ 
    /* *********************************************************************/
    private static final Logger LOG = LoggerFactory.getLogger(ExceptionConverter.class);
    
    private static final String MSG_UNEXPECTED_SERVER_CONDITION = "The server " +
            "encoutered an unexpected condition";

    protected abstract UFE newUserException(String msg, UserErrorInfo faultInfo);
    protected abstract EFE newEnvironmentException(String msg, SystemErrorInfo faultInfo);

    /* *********************************************************************/
    /* PUBLIC METHODS */
    /* *********************************************************************/

    public UFE toWSUserException(UserException e) throws EFE {
        try {
            UserErrorInfo faultInfo = new UserErrorInfo();
            faultInfo.setCode(UserErrorCodeType.fromValue(e.getCode().name()));
            faultInfo.setErrorName(e.getErrorName());
            faultInfo.setMessage(e.getMessage());
            UFE faultExc = newUserException(e.getMessage(), faultInfo);
            // deletes the orginal stacktrace for security purpose as this is
            // exception is propagated to the client
            faultExc.setStackTrace(new StackTraceElement[0]);
            return faultExc;
        } catch (Exception ex) {
            LOG.debug(ex);
            LOG.error("Caused by : %s", ex.getMessage());
            throw toInternalError();
        }
    }

    public EFE toWSEnvironmentException(EnvironmentException e){
        try {
            SystemErrorInfo faultInfo = new SystemErrorInfo();
            faultInfo.setCode(SystemErrorCodeType.fromValue(e.getCode().name()));
            faultInfo.setMessage(MSG_UNEXPECTED_SERVER_CONDITION);
            EFE faultExc = newEnvironmentException(
                    MSG_UNEXPECTED_SERVER_CONDITION, faultInfo);
            // deletes the orginal stacktrace for security purpose as this is
            // exception is propagated to the client
            faultExc.setStackTrace(new StackTraceElement[0]);
            return faultExc;
        } catch (Exception ex) {
            return toInternalError();
        }
    }

    public EFE toInternalError(){
        SystemErrorInfo faultInfo = new SystemErrorInfo();
        faultInfo.setCode(SystemErrorCodeType.DTP_ENV_INTERNAL_ERROR);
        EFE faultExc = newEnvironmentException(MSG_UNEXPECTED_SERVER_CONDITION, faultInfo);
        // deletes the orginal stacktrace for security purpose as this is exception
        // is propagated to the client
        faultExc.setStackTrace(new StackTraceElement[0]);
        return faultExc;
    }
}
