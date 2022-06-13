package com.dictao.dtp.web.servlet;

/**
 * Thrown to redirect user
 */
public class UserErrorRedirectException extends Exception {
    
    private final String errorRedirectStatus;
    
    public UserErrorRedirectException(String errorRedirectStatus, String message, Object... args) 
    {
        super(String.format(message, args));
        this.errorRedirectStatus = errorRedirectStatus;
    }
    
    public UserErrorRedirectException(String errorRedirectStatus, Throwable cause, String message, Object... args) 
    {
        super(String.format(message, args), cause);
        this.errorRedirectStatus = errorRedirectStatus;
    }

    /**
     * @return the custom error redirect status
     */
    public String getErrorRedirectStatus() {
        return errorRedirectStatus;
    }
}
